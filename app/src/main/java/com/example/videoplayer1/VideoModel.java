package com.example.videoplayer1;

public class VideoModel {

    private String name;
    private String path;
    private String duration;
    private String id;
    private String bucketName;

    public VideoModel(String name, String path, String duration, String id, String bucketName) {
        this.name = name;
        this.path = path;
        this.duration = duration;
        this.id = id;
        this.bucketName = bucketName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
