package com.example.youtube.api.response.commentsResponse;

import com.example.youtube.model.Comment;

public class UpdateCommentResponse {
    private String message;
    private Comment updatedComment;

    public String getMessage() {
        return message;
    }

    public Comment getUpdatedComment() {
        return updatedComment;
    }
}
