package com.example.youtube.model.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.youtube.model.Video;

import java.util.List;

@Dao
public interface VideoDao {

    // Query to get all videos from the table
    @Query("SELECT * FROM video")
    List<Video> index();

    // Query to get a video by its ID
    @Query("SELECT * FROM video WHERE roomId = :id")
    Video get(int id);

    // Insert one or more videos
    @Insert
    void insert(Video... videos);

    // Update one or more videos
    @Update
    void update(Video... videos);

    // Delete one or more videos
    @Delete
    void delete(Video... videos);

    // Delete all entries from the video table
    @Query("DELETE FROM video")
    void clear();

    // Insert a list of videos
    @Insert
    void insertList(List<Video> videoList);
    @Query("SELECT * FROM video WHERE userApiId = :userId")
    List<Video> getVideosByUserId(String userId);
}
