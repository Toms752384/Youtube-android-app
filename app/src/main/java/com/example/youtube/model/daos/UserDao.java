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

    // Query to login a user
    @Query("SELECT * FROM user WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    //////////////////////////////////////////////////
    // Set the current user
    @Query("UPDATE user SET isCurrentUser = 1 WHERE username = :username AND password = :password")
    void setCurrentUser(String username, String password);

    // Clear the current user
    @Query("UPDATE user SET isCurrentUser = 0 WHERE isCurrentUser = 1")
    void clearCurrentUser();

    // Get the current user
    @Query("SELECT * FROM user WHERE isCurrentUser = 1 LIMIT 1")
    User getCurrentUser();
}
