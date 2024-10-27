package com.example.youtube.model.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.youtube.api.UserAPI;
import com.example.youtube.model.AppDB;
import com.example.youtube.model.User;
import com.example.youtube.model.daos.UserDao;

import java.io.File;
import java.util.List;

public class UserRepository {
    private UserDao userDao;
    private UserAPI userAPI;
    private MutableLiveData<List<User>> userListData;

    public UserRepository(Context context) {
        AppDB db = Room.databaseBuilder(context, AppDB.class, "AppDB")
                .fallbackToDestructiveMigration()
                .build();
        userDao = db.userDao();

        userListData = new MutableLiveData<>();
        userAPI = new UserAPI(userListData, userDao);

        // Load initial data from the server
        userAPI.getAllUsers();
    }

    public LiveData<List<User>> getAllUsers() {
        return userListData;
    }

    public void insertUser(User user) {
        userAPI.add(user);
    }

    public LiveData<Boolean> isExist(String username) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
         getUserByUsername(username).observeForever(user -> {
         result.setValue(user != null);
      });
     return result;
    }

    public LiveData<User> getUserByUsername(String username) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        new Thread(() -> {
            User user = userDao.findByUsername(username);
            userLiveData.postValue(user);
        }).start();
        return userLiveData;
    }

    public LiveData<Boolean> matchAccount(String username, String password) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        getUserByUsername(username).observeForever(user -> {
            if (user != null) {
                result.setValue(user.getPassword().equals(password));
            } else {
                result.setValue(false);
            }
        });
        return result;
    }

    public void deleteUser(User user){
        userAPI.delete(user);
    }

    public void createToken(User user){
        userAPI.createToken(user);
    }

    public void updateUser(String nickname, String avatar) {
        userAPI.updateUser(nickname, avatar);
    }
}
