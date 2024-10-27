package com.example.youtube.api.response.commentsResponse;

import com.example.youtube.model.Comment;

public class CommentResponse {
    private String message;
    private Comment comment;

    public String getMessage() {
        return message;
    }

    public Comment getComment() {
        return comment;
    }
}
