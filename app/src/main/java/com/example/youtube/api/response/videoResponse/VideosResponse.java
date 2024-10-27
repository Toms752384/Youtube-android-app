package com.example.youtube.api.response.videoResponse;

import com.example.youtube.model.Video;

import java.util.List;

// the VideoResponse class to match the server response structure
public class VideosResponse {
    private String message;
    private List<Video> videos;

    public String getMessage() {
        return message;
    }
    public List<Video> getVideos() {
        return videos;
    }
}