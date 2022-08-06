package com.example.videoplayer1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;

public class VideoPlayingActivity extends AppCompatActivity {

    private MxVideoPlayerWidget videoPlayer;
    private int position;
    private List<VideoModel> videoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playing);

        videoPlayer = findViewById(R.id.videoPlayer);

        if(getIntent() != null)
        {
            position = getIntent().getIntExtra("Position",0);
            videoList = (List<VideoModel>)getIntent().getSerializableExtra("List");
        }

        videoPlayer.startPlay(videoList.get(position).getPath(), MxVideoPlayer.SCREEN_WINDOW_FULLSCREEN, videoList.get(position));
//        videoPlayer.setList(videoList, position);
    }
}