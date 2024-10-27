package com.example.youtube.model.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.youtube.model.Comment;
import com.example.youtube.model.User;

import java.util.List;

@Dao
public interface CommentDao {
    // Query to get comments by video ID
    @Query("SELECT * FROM comment WHERE videoId = :videoId")
    LiveData<List<Comment>> getCommentsByVideoId(String videoId);

    @Query("SELECT * FROM comment")
    List<Comment> index();

    // Query to get a comment by its ID
    @Query("SELECT * FROM comment WHERE roomId = :id")
    Comment get(int id);

    // Insert one or more comments
    @Insert
    void insert(Comment... comments);

    // Update one or more comments
    @Update
    void update(Comment... comments);

    // Delete one or more comments
    @Delete
    void delete(Comment... comments);

    // Delete all entries from the comment table
    @Query("DELETE FROM comment")
    void clear();

    // Insert a list of comments
    @Insert
    void insertList(List<Comment> commentList);

}
