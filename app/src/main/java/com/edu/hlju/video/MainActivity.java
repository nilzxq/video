package com.edu.hlju.video;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
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
                }else{
                    play_controller_img.setImageResource(R.drawable.pause_btn_style);
                    //继续播放
                    videoView.start();
                }
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
