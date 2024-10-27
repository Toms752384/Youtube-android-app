package com.example.youtube.model.converters;

import androidx.room.TypeConverter;

import com.example.youtube.model.Comment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class CommentConverter {
    private static Gson gson = new Gson();

    @TypeConverter
    public static List<Comment> stringToCommentList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Comment>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String commentListToString(List<Comment> someObjects) {
        return gson.toJson(someObjects);
    }
}
