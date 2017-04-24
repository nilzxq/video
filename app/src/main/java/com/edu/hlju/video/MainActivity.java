package com.edu.hlju.video;

import android.app.Activity;
import android.app.Fragment;
import android.app.Notification;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {
    private VideoView videoView;
    private LinearLayout controllerLayout;
    private ImageView play_controller_img,screen_img,volume_img;
    private TextView time_current_tv,time_total_tv;
    private SeekBar play_seek,volume_seek;
    private static  final int UPDATE_UI=1;
    private int screen_width,screen_height;
    private RelativeLayout videoLayout;
    private AudioManager mAudioManager;
    private boolean isFullScreen=false;
    private boolean isAdjust=false;
    private int threshold=54;
    private float lastX=0,lastY=0;
    private float mBrightness;
    private ImageView operation_bg,operation_percent;
    private FrameLayout progress_layout;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAudioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
        initUI();
        setPlayerEvent();
//        String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/english.mp4";
//        /**
//         * 本地视频播放
//         */
//        videoView.setVideoPath(path);
//        Log.e(path,"");
//        videoView.start();
//        UIHandler.sendEmptyMessage(UPDATE_UI);

        /**
         * 网络播放
         */
        videoView.setVideoURI(Uri.parse("http://192.168.99.143:8080/video/english.mp4"));
        /**
         * 使用MediaController控制视频播放
         */
        MediaController controller=new MediaController(this);
        /**
         * 设置VideoView与MediaController建立关联
         */
        videoView.setMediaController(controller);
        /**
         * 设置MediaController与VideoView建立关联
         */
        controller.setMediaPlayer(videoView);
        videoView.start();
    }

    /**
     * 时间的格式化
     * @param textView 显示时间的文本
     * @param millisecond 毫秒
     */
    private void updateTextViewWithTimeFormat(TextView textView,int millisecond){
        int second=millisecond/1000;
        int hh=second/3600;
        int mm=second%3600/60;
        int ss=second%60;
        String str=null;
        if(hh!=0){
         str=String.format("%02d:%02d:%02d",hh,mm,ss);
        }else{
            str=String.format("%02d:%02d",mm,ss);
        }
        textView.setText(str);
    }

    /**
     * 刷新UI的操作
     */
    private Handler UIHandler=new Handler(){

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what==UPDATE_UI) {
                //获取视频当前的播放时间
                int currentPosition = videoView.getCurrentPosition();
                //获取视频播放的总时间
                int totalduration = videoView.getDuration();
                //格式化视频播放时间
                updateTextViewWithTimeFormat(time_current_tv, currentPosition);
                updateTextViewWithTimeFormat(time_total_tv, totalduration);
                play_seek.setMax(totalduration);
                play_seek.setProgress(currentPosition);
                UIHandler.sendEmptyMessageDelayed(UPDATE_UI, 500);
            }
        }
    };
 @Override
  protected void onPause(){
    super.onPause();
    UIHandler.removeMessages(UPDATE_UI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setPlayerEvent(){
        /**
         * 控制视频的播放和暂停
         */
        play_controller_img.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    play_controller_img.setImageResource(R.drawable.play_btn_style);
                    //暂停播放
                    videoView.pause();
                    UIHandler.removeMessages(UPDATE_UI);
                }else{
                    play_controller_img.setImageResource(R.drawable.pause_btn_style);
                    //继续播放
                    videoView.start();
                    UIHandler.sendEmptyMessage(UPDATE_UI);
                }
            }
        });
        play_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                   updateTextViewWithTimeFormat(time_current_tv,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                UIHandler.removeMessages(UPDATE_UI);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress=seekBar.getProgress();
                //令视频播放进度遵循seekBar停止拖动的这一刻的进度
                videoView.seekTo(progress);
                UIHandler.sendEmptyMessage(UPDATE_UI);
            }
        });
        volume_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                /**
                 * 设置当前设备的音量
                 */
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        /**
         * 横竖屏切换
         */
        screen_img.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(isFullScreen){
                    //切换全屏或半屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }else{
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });
        /**
         * 控制VideoView的手势事件
         */
        videoView.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x=event.getX();
                float y=event.getY();

                switch (event.getAction()){
                    /**
                     * 手指落下屏幕的那一刻（只会调用一次）
                     */
                    case MotionEvent.ACTION_DOWN:
                    {
                        lastX=x;
                        lastY=y;
                        break;
                    }
                    /**
                     * 手指在屏幕上移动(调用多次)
                     */
                    case MotionEvent.ACTION_MOVE:
                    {
                        float detlaX=x-lastX;
                        float detlaY=y-lastY;
                        float absdetlaX=Math.abs(detlaX);
                        float absdetlaY=Math.abs(detlaY);
                        if(absdetlaX>threshold&&absdetlaY>threshold){
                            if(absdetlaX<absdetlaY){
                                isAdjust=true;
                            }else{
                                isAdjust=false;
                            }
                        }else if(absdetlaX<threshold&&absdetlaY>threshold){
                            isAdjust=true;
                        }else if(absdetlaX>threshold&&absdetlaY<threshold){
                            isAdjust=false;
                        }
                        Log.e("Main","手势是否合法"+isAdjust);
                        if(isAdjust){
                            /**
                             * 在判断好当前手势事件已经合法的前提下，去区分此时手势应该调节亮度还是调节声音
                             */
                            if(x<screen_width/2){
                                /**
                                 * 调节亮度
                                 */
                                if(detlaY>0){
                                    /**
                                     * 降低亮度
                                     */
                                    Log.e("Main","降低亮度"+detlaY);
                                }else{
                                    /**
                                     * 升高亮度
                                     */
                                    Log.e("Main","升高亮度"+detlaY);
                                }
                                changeBrightness(-detlaY);
                            }else{
                                /**
                                 * 调节声音
                                 */
                                if(detlaY>0){
                                    /**
                                     * 减小声音
                                     */
                                    Log.e("Main","减小声音"+detlaY);
                                }else{
                                    /**
                                     * 增大声音
                                     */
                                    Log.e("Main","增大声音"+detlaY);
                                }
                                changeVolume(-detlaY);
                            }
                        }
                        lastX=x;
                        lastY=y;
                        break;
                    }
                    /**
                     * 手指离开屏幕的那一刻（调用一次）
                     */
                    case MotionEvent.ACTION_UP:
                    {
                        progress_layout.setVisibility(View.GONE);
                        break;
                    }

                }
                return true;
            }
        });
    }

    /**
     * 改变声音
     * @param detlaY
     */
    private void changeVolume(float detlaY){
        int max=mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int index=(int)(detlaY/screen_height*max*3);
        int volume=Math.max(current+index,0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);
        if(progress_layout.getVisibility()==View.GONE) {
            progress_layout.setVisibility(View.VISIBLE);
        }
        operation_bg.setImageResource(R.mipmap.video_volume_bg);
        ViewGroup.LayoutParams layoutParams=operation_percent.getLayoutParams();
        layoutParams.width=(int)(PixelUtil.dp2px(94)*(float)volume/max);
        operation_percent.setLayoutParams(layoutParams);
        volume_seek.setProgress(volume);

    }
    /**
     * 调节亮度
     */
   private  void changeBrightness(float detlaY){
       WindowManager.LayoutParams attributes=getWindow().getAttributes();
       mBrightness=attributes.screenBrightness;
       float index=detlaY/screen_height/3;
       mBrightness+=index;


       if(mBrightness>1.0f){
           mBrightness=1.0f;
       }
       if(mBrightness<0.01f){
           mBrightness=0.01f;
       }
       attributes.screenBrightness=mBrightness;
       if(progress_layout.getVisibility()==View.GONE) {
           progress_layout.setVisibility(View.VISIBLE);
       }
       operation_bg.setImageResource(R.mipmap.video_brightness_bg);
       ViewGroup.LayoutParams layoutParams=operation_percent.getLayoutParams();
       layoutParams.width=(int)(PixelUtil.dp2px(94)*mBrightness);
       operation_percent.setLayoutParams(layoutParams);
       getWindow().setAttributes(attributes);

   }

    /**
     * 初始化UI布局
     */
    private void initUI(){
        PixelUtil.initContext(this);
        videoView=(VideoView)findViewById(R.id.videoView);
        controllerLayout=(LinearLayout) findViewById(R.id.controllerbar_layout);
        play_controller_img=(ImageView)findViewById(R.id.pause_img);
        screen_img=(ImageView)findViewById(R.id.screen_img);
        time_current_tv=(TextView)findViewById(R.id.time_current_tv);
        time_total_tv=(TextView)findViewById(R.id.time_total_tv);
        play_seek=(SeekBar)findViewById(R.id.play_seek);
        volume_seek=(SeekBar)findViewById(R.id.volume_seek);
        volume_img=(ImageView)findViewById(R.id.volume_img);
        screen_width=getResources().getDisplayMetrics().widthPixels;
        screen_height=getResources().getDisplayMetrics().heightPixels;
        videoLayout=(RelativeLayout)findViewById(R.id.videoLayout);
        operation_bg=(ImageView)findViewById(R.id.operation_bg);
        operation_percent=(ImageView)findViewById(R.id.operation_percent);
        progress_layout=(FrameLayout) findViewById(R.id.progress_layout);
        /**
         * 当前设备的最大音量
         */
        int streamMaxVolume=mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        /**
         * 获取设备当前的音量
         */
        int streamVolume=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume_seek.setMax(streamMaxVolume);
        volume_seek.setProgress(streamVolume);
    }

    private void setVideoView(int width,int height){
        ViewGroup.LayoutParams layoutParams=videoView.getLayoutParams();
        layoutParams.width=width;
        layoutParams.height=height;
        videoView.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams layoutParams1=videoLayout.getLayoutParams();
        layoutParams1.width=width;
        layoutParams1.height=height;
        videoLayout.setLayoutParams(layoutParams1);
    }
    /**
     * 监听到屏幕方向的改变
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /**
         * 当屏幕方向为横屏时
         */
        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
            setVideoView(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
             volume_img.setVisibility(View.VISIBLE);
            volume_seek.setVisibility(View.VISIBLE);
            isFullScreen=true;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        /**
         * 当屏幕方向为竖屏时
         */
        else{
           setVideoView(ViewGroup.LayoutParams.MATCH_PARENT,PixelUtil.dp2px(240));
            volume_img.setVisibility(View.GONE);
            volume_seek.setVisibility(View.GONE);
            isFullScreen=false;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    public void writeToSdcard(String s) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();
            try {
                File file = new File(sdCardDir.getCanonicalPath() + fileName);
                FileOutputStream fos = new FileOutputStream(file);

                fos.write(s.getBytes());
                fos.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        else {
            Toast.makeText(MainActivity.this, "sd卡异常", Toast.LENGTH_LONG)
                    .show();
        }

    }
}
