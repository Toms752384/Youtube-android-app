package com.example.youtube.api;

import com.example.youtube.api.response.commentsResponse.CommentResponse;
import com.example.youtube.api.response.commentsResponse.CommentsResponse;
import com.example.youtube.api.response.commentsResponse.UpdateCommentResponse;
import com.example.youtube.model.Comment;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommentWebServiceAPI {

    // Fetch all comments for a specific video by video ID
    @GET("videos/{pid}/comments")
    Call<CommentsResponse> getCommentsByVideoId(@Path("pid") String videoId);

    // Add a new comment
    @POST("videos/{pid}/comments/{id}")
    Call<CommentResponse> add(@Path("pid") String videoId, @Path("id") String userId,
                              @Body Comment comment, @Header("Authorization") String token);

    // Edit a comment by comment ID
    @PUT("videos/{id}/{pid}/comments/{cid}")
    Call<UpdateCommentResponse> update(@Path("id") String userId, @Path("pid") String videoId,
                                       @Path("cid") String commentId, @Header("Authorization") String token, @Body Comment comment);

    // Delete a comment by comment ID
    @DELETE("videos/{id}/{pid}/comments/{cid}")
    Call<Void> delete(@Path("id") String userId, @Path("pid") String videoId,
                      @Path("cid") String commentId, @Header("Authorization") String token);
}
