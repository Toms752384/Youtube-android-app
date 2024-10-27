package com.example.youtube.model.repository;

import androidx.lifecycle.LiveData;

import androidx.room.Room;

import com.example.youtube.api.CommentAPI;
import com.example.youtube.model.AppDB;
import com.example.youtube.model.Comment;
import com.example.youtube.model.daos.CommentDao;
import com.example.youtube.utils.MyApplication;

import java.util.List;

public class CommentsRepository {
    private final CommentDao commentDao;
    private final CommentAPI commentAPI;

    public CommentsRepository() {
        AppDB db = Room.databaseBuilder(MyApplication.context, AppDB.class, "CommentsDB")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        commentDao = db.commentDao();
        commentAPI = new CommentAPI( commentDao);
    }


    public LiveData<List<Comment>> getCommentsByVideoId(String videoId) {
        new Thread(() -> {
            commentAPI.fetchCommentsByVideoId(videoId);
        }).start();
        return commentDao.getCommentsByVideoId(videoId); // Return LiveData from Room
    }

    public void add(Comment comment) {
        new Thread(() -> {
            commentAPI.add(comment);
        }).start();
    }

    public void delete(Comment comment) {
        new Thread(() -> {
            commentAPI.delete(comment);
        }).start();
    }

    public void update(Comment comment) {
        new Thread(() -> {
            commentAPI.update(comment);
        }).start();
    }
}
