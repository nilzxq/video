package com.edu.hlju.video;

import android.app.Activity;
import android.app.Notification;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;

public class MainActivity extends Activity {
    private VideoView videoView;
    private LinearLayout controllerLayout;
    private ImageView play_controller_img,screen_img;
    private TextView time_current_tv,time_total_tv;
    private SeekBar play_seek,volumn_seek;
    private static  final int UPDATE_UI=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setPlayerEvent();
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/test1.mp4";
        /**
         * 本地视频播放
         */
        videoView.setVideoPath(path);
        videoView.start();
        UIHandler.sendEmptyMessage(UPDATE_UI);
        /**
         * 网络播放
         */
  //      videoView.setVideoURI(Uri.parse("http://192.168.99.143:8080/video/test1.mp4"));
//        /**
//         * 使用MediaController控制视频播放
//         */
//        MediaController controller=new MediaController(this);
//        /**
//         * 设置VideoView与MediaController建立关联
//         */
//        videoView.setMediaController(controller);
//        /**
//         * 设置MediaController与VideoView建立关联
//         */
//        controller.setMediaPlayer(videoView);
 //       videoView.start();
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
    }
    /**
     * 初始化UI布局
     */
    private void initUI(){
        videoView=(VideoView)findViewById(R.id.videoView);
        controllerLayout=(LinearLayout) findViewById(R.id.controllerbar_layout);
        play_controller_img=(ImageView)findViewById(R.id.pause_img);
        screen_img=(ImageView)findViewById(R.id.screen_img);
        time_current_tv=(TextView)findViewById(R.id.time_current_tv);
        time_total_tv=(TextView)findViewById(R.id.time_total_tv);
        play_seek=(SeekBar)findViewById(R.id.play_seek);
        volumn_seek=(SeekBar)findViewById(R.id.volume_seek);
    }
}
