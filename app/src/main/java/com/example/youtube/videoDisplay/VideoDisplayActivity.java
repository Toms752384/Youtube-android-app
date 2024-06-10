package com.example.youtube.videoDisplay;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youtube.R;


/**
 * Activity to display and play a selected video.
 */
public class VideoDisplayActivity extends AppCompatActivity {
    private VideoView vvVideo; // VideoView for playing the video
    private TextView titleView; // TextView for displaying the video title
    private TextView tvDescription; // TextView for displaying the video description

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display);

        // Initialize views
        vvVideo = findViewById(R.id.vvVideo);
        titleView = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);

        // Get data from intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String videoResId = getIntent().getStringExtra("videoResId");

        // Set data to views
        titleView.setText(title);
        tvDescription.setText(description);

        // Get the video resource ID
        int videoResIdInt = getRawResIdByName(videoResId);
        String videoPath = "android.resource://" + getPackageName() + "/" + videoResIdInt;

        // Set the video path and start playing
        vvVideo.setVideoPath(videoPath);
        vvVideo.start();
    }

    /**
     * Retrieves the resource ID of a raw resource by its name.
     *
     * @param resName The name of the raw resource.
     * @return The resource ID of the raw resource.
     */
    public int getRawResIdByName(String resName) {
        String packageName = getPackageName();
        return getResources().getIdentifier(resName, "raw", packageName);
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape mode
            ViewGroup.LayoutParams params = vvVideo.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            vvVideo.setLayoutParams(params);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Portrait mode
            ViewGroup.LayoutParams params = vvVideo.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) getResources().getDimension(R.dimen.video_height);
            vvVideo.setLayoutParams(params);
        }

    }
}