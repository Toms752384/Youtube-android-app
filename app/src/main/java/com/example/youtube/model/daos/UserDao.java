package com.example.youtube.model.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.youtube.model.User;

import java.util.List;

@Dao
public interface UserDao {

    // Query to get all users from the table
    @Query("SELECT * FROM user")
    List<User> index();

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    User findByUsername(String username);

    // Query to get a user by their ID
    @Query("SELECT * FROM user WHERE roomId = :id")
    User get(int id);

    // Insert one or more users
    @Insert
    void insert(User... users);

    // Update one or more users
    @Update
    void update(User... users);

    // Delete one or more users
    @Delete
    void delete(User... users);

    // Delete all entries from the user table
    @Query("DELETE FROM user")
    void clear();

    // Insert a list of users
    @Insert
    void insertList(List<User> userList);

}
