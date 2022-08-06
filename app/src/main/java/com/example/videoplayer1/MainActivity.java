package com.example.videoplayer1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import static com.example.videoplayer1.SplashActivity.videoList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvVideoList;
//    private List<VideoModel> videoList;
    private VideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvVideoList = findViewById(R.id.rvVideoList);

        if(isPermissionGranted())
        {
            setVideoAdapter();
//            new getAllVideos().execute();
        }
    }

    /*public class getAllVideos extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {

            videoList = new ArrayList<>();
            String[] projections = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DURATION, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            Cursor videoCursor = getContentResolver().query(uri, projections, null, null, "LOWER(" + MediaStore.Video.Media.DATE_MODIFIED + ") DESC");
            if(videoCursor.getCount() > 0)
            {
                while (videoCursor.moveToNext())
                {
                    String id = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String name = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    String path = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    String duration = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                    String bucket = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));

                    if (duration != null && !duration.equals(""))
                    {
                        VideoModel model = new VideoModel(name, path, duration, id, bucket);
                        videoList.add(model);
                    }
                }
                videoCursor.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            setVideoAdapter();
        }
    }*/

    public void setVideoAdapter()
    {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rvVideoList.setLayoutManager(layoutManager);

        adapter = new VideoAdapter(this, videoList);
        rvVideoList.setAdapter(adapter);
    }

    public boolean isPermissionGranted()
    {
        if(Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        else //permission is automatically granted for version less than 23
        {
            return true;
        }
    }
}