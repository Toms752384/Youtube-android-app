package com.example.youtube.model.converters;
import androidx.room.TypeConverter;
import android.net.Uri;
public class UriConverter {
    @TypeConverter
    public static String fromUri(Uri uri) {
        return uri == null ? null : uri.toString();
    }

    @TypeConverter
    public static Uri toUri(String uriString) {
        return uriString == null ? null : Uri.parse(uriString);
    }
}
