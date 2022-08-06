package com.example.videoplayer1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.jiajunhui.xapp.medialoader.MediaLoader;
import com.jiajunhui.xapp.medialoader.bean.VideoFolder;
import com.jiajunhui.xapp.medialoader.bean.VideoItem;
import com.jiajunhui.xapp.medialoader.bean.VideoResult;
import com.jiajunhui.xapp.medialoader.callback.OnVideoLoaderCallBack;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    public List<VideoFolder> videoFoldersList = new ArrayList<>();
    public static List<VideoItem> videoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (isPermissionGranted())
        {
            loadVideoFolder();
            init();
        }
        else
        {
            GetPermissions();
        }
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

    public void init()
    {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        },1000);
    }

    private void GetPermissions() {

        List<String> permissionsNeeded = new ArrayList<>();
        final List<String> permissionsList = new ArrayList<>();

        if(!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read External Storage");

        if(!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write External Storage");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                for (int i = 0; i < 1; i++)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 1);
                    }

                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 1);
            }
            return;
        }

        if (isPermissionGranted())
            init();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (permissions.length >= 1)
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadVideoFolder();
                    init();
                }
        }
        else
        {
            GetPermissions();
        }
    }

    private void loadVideoFolder() {
        MediaLoader.getLoader().loadVideos(SplashActivity.this, new OnVideoLoaderCallBack() {

            @Override
            public void onResult(VideoResult result) {
                Collections.sort(result.getFolders(), (o1, o2) -> Integer.valueOf(o2.getItems().size()).compareTo(o1.getItems().size()));
                videoFoldersList = result.getFolders();
                File cameraFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                for (int i = 1; i < videoFoldersList.size(); i++) {
                    VideoFolder videoFolder = videoFoldersList.get(i);
                    if (videoFolder.getItems().get(0).getPath().contains(cameraFile.toString())) {
                        VideoFolder videoFolder0 = videoFoldersList.get(0);
                        videoFoldersList.set(0, videoFoldersList.get(i));
                        videoFoldersList.remove(i);
                        videoFoldersList.add(1, videoFolder0);
                        break;
                    }
                }
                for (int i = 2; i < videoFoldersList.size(); i++) {
                    VideoFolder videoFolder = videoFoldersList.get(i);
                    if (videoFolder.getName() != null) {
                        if (videoFolder.getName().equalsIgnoreCase("WhatsApp Video")) {
                            VideoFolder videoFolder1 = videoFoldersList.get(1);
                            videoFoldersList.set(1, videoFoldersList.get(i));
                            videoFoldersList.remove(i);
                            videoFoldersList.add(2, videoFolder1);
                            break;
                        }
                    }
                }

                for(int i=0; i<videoFoldersList.size(); i++)
                {
                    List<VideoItem> videoItem = videoFoldersList.get(i).getItems();
                    videoList.addAll(videoItem);
                }
            }
        });
    }
}