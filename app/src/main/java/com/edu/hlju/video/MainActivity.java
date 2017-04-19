package com.edu.hlju.video;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends Activity {
    private VideoView videoView;
    private LinearLayout controllerbar_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView=(VideoView)findViewById(R.id.videoView);
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/test1.mp4";
        /**
         * 本地视频播放
         */
      //videoView.setVideoPath(path);
        /**
         * 网络播放
         */
        videoView.setVideoURI(Uri.parse("http://192.168.99.143:8080/video/test1.mp4"));
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
}
