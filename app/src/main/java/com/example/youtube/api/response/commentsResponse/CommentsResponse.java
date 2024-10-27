package com.example.youtube.api.response.commentsResponse;

import com.example.youtube.model.Comment;

import java.util.List;

public class CommentsResponse {
    private String message;
    private List<Comment> comments;

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
