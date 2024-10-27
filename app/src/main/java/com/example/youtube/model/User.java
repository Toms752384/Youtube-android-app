package com.example.youtube.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "user")
public class User implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int roomId;

    @SerializedName("_id")
    private String apiId;
    private final String username;
    private final String password;
    private String nickname;
    private String avatar;

    public User(String username, String password, String nickname, String avatar) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.avatar = avatar;
        this.apiId = null;
    }
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) { this.nickname = nickname;}

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {this.avatar = avatar;}

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }
}
