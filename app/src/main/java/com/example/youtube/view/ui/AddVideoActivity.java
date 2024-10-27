package com.example.youtube.view.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;


import com.example.youtube.R;
import com.example.youtube.model.User;
import com.example.youtube.model.UserManager;
import com.example.youtube.utils.CustomToast;
import com.example.youtube.model.Video;
import com.example.youtube.viewModel.VideoViewModel;

import java.util.Date;

public class AddVideoActivity extends AppCompatActivity {
    private static final int SELECT_VIDEO = 1;
    private EditText etTitle, etDescription;
    private String videoPath;
    private VideoViewModel videoViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_video);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);

        findViewById(R.id.btnSelectVideo).setOnClickListener(v -> selectVideo());

        findViewById(R.id.btnAddVideo).setOnClickListener(v -> addVideo());

        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());


        // Find the logo ImageView
        ImageView logoImage = findViewById(R.id.ivLogoImage);

        // Set up the logo click listener
        logoImage.setOnClickListener(v -> {
            Intent intent = new Intent(AddVideoActivity.this, VideoListActivity.class);
            startActivity(intent);
        });

    }

    private void selectVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a video"), SELECT_VIDEO);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_VIDEO && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();

            if (selectedVideoUri != null) {
                // Take persistable URI permission
                int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(selectedVideoUri, takeFlags);

//                videoPath = selectedVideoUri.toString();
                videoPath=getRealPathFromURI(selectedVideoUri);
                Log.d("videoPath", videoPath);
                CustomToast.showToast(this, "Video Selected: " + videoPath);
                VideoView vvVideo = findViewById(R.id.vvTest);
                vvVideo.setVideoURI(selectedVideoUri);
                vvVideo.start();
            }
        }
    }


    private void addVideo() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();

        if (title.isEmpty() || description.isEmpty() || videoPath == null) {
            CustomToast.showToast(this, "Please fill all fields and select a video");
            return;
        }

        User currentUser = UserManager.getInstance().getCurrentUser();
        String userId = String.valueOf(currentUser.getApiId());
        String username =currentUser.getUsername();
        String avatar = currentUser.getAvatar();

        // Create a new Video object
        Video newVideo = new Video(userId,title, description, videoPath, username, 0, 0, 0, avatar);
        videoViewModel.add(newVideo);

        Intent intent = new Intent(AddVideoActivity.this, VideoListActivity.class);
        startActivity(intent);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }
}