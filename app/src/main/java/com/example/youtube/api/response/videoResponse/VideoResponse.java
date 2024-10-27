package com.example.youtube.api.response.videoResponse;

import com.example.youtube.model.Video;

import java.util.List;

public class VideoResponse {
    private String message;
    private Video video;

    public String getMessage() {
        return message;
    }

    public Video getVideo() {
        return video;
    }
}