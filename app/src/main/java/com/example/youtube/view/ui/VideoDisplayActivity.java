package com.example.youtube.view.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube.R;
import com.example.youtube.model.Comment;
import com.example.youtube.model.User;
import com.example.youtube.model.UserManager;
import com.example.youtube.model.Video;
import com.example.youtube.utils.CustomToast;
import com.example.youtube.view.adapter.CommentAdapter;
import com.example.youtube.viewModel.CommentViewModel;
import com.example.youtube.viewModel.UserViewModel;
import com.example.youtube.viewModel.VideoViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

/**
 * Activity to display and play a selected video.
 */
public class VideoDisplayActivity extends AppCompatActivity implements CommentAdapter.CommentAdapterListener {
    private VideoView vvVideo; // VideoView for playing the video
    private ConstraintLayout clControl; // Control buttons layout
    private boolean isFullScreen = false; // Fullscreen indicator
    List<Comment> commentList;

    private Video video;
    private CommentAdapter commentAdapter;
    private VideoViewModel videoViewModel;
    private CommentViewModel commentViewModel;
    private User currentUser;
    private ShapeableImageView profileImageView;
    private boolean exist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // remove title of screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_display);

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        currentUser = UserManager.getInstance().getCurrentUser();

        // Get data from intent
        video = (Video) getIntent().getSerializableExtra("extra_video");

        // Initialize views and set data to views
        vvVideo = findViewById(R.id.vvVideo);
        TextView titleView = findViewById(R.id.tvTitle);
        TextView tvDescription = findViewById(R.id.tvDescription);
        titleView.setText(video.getTitle());
        tvDescription.setText("Description: " + video.getDescription());
        TextView tv_like = findViewById(R.id.tv_like);
        TextView tv_views = findViewById(R.id.tv_view);
        TextView tv_author = findViewById(R.id.tv_author);
        tv_like.setText("Likes: " + video.getLikes());
        tv_author.setText("Artist: " + video.getUserName());
        // Increment views count when the video starts playing
        video.setViews(video.getViews() + 1);
        videoViewModel.update(video);
        tv_views.setText("Views: " + video.getViews());


        profileImageView = findViewById(R.id.siv_profile_pic);
        loadUserPic(video.getAvatar());


        // Set the video URI and start playing
        vvVideo.setVideoURI(Uri.parse(video.getVideoUrl()));
        vvVideo.start();

        // Comment list
        RecyclerView rvCommentsRecyclerView = findViewById(R.id.rvComments);
        rvCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, this);
        rvCommentsRecyclerView.setAdapter(commentAdapter);

        commentViewModel.getCommentsByVideoId(video.getApiId()).observe(this, comments -> {
            commentAdapter.setComments(comments);
            commentList = comments;
        });


        // New comment function
        ImageView iv_post = findViewById(R.id.iv_post);
        EditText et_CommentInput = findViewById(R.id.et_CommentInput);
        iv_post.setOnClickListener(view -> addComment(et_CommentInput));

        // Like function:
        ImageView iv_like = findViewById(R.id.iv_like);
        iv_like.setOnClickListener(view -> clickLike(view, tv_like));

        // Share function:
        ImageView iv_share = findViewById(R.id.iv_share);
        iv_share.setOnClickListener(view -> shareVideo());

        // Video control buttons
        clControl = findViewById(R.id.clControl);
        ImageButton btnPlayPause = findViewById(R.id.btnPlayPause);
        ImageButton btnForward = findViewById(R.id.btnForward);
        ImageButton btnRewind = findViewById(R.id.btnRewind);
        ImageButton btnFullscreen = findViewById(R.id.btnFullscreen);

        // Video control function
        btnPlayPause.setOnClickListener(this::playPause);
        btnForward.setOnClickListener(v -> forward());
        btnRewind.setOnClickListener(v -> rewind());
        btnFullscreen.setOnClickListener(v -> screenSize());

        // Show control buttons when user touches the video
        vvVideo.setOnClickListener(v -> showControls(clControl));

        profileImageView.setOnClickListener(v -> {
            userViewModel.isExist(video.getUserName()).observe(this, bol -> {
                exist = bol;
            });
            if (exist) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("extra_avatar", video.getAvatar());
                intent.putExtra("extra_userId", video.getUserApiId());
                startActivity(intent);
            }
        });
    }

    private void playPause(View view) {
        view.setSelected(!view.isSelected());
        if (view.isSelected()) {
            vvVideo.pause();
        } else {
            vvVideo.start();
        }
    }

    private void forward() {
        int currentPosition = vvVideo.getCurrentPosition();
        int duration = vvVideo.getDuration();
        if (currentPosition + 10000 < duration) {
            vvVideo.seekTo(currentPosition + 10000);
        } else {
            vvVideo.seekTo(duration);
        }
    }

    private void rewind() {
        int currentPosition = vvVideo.getCurrentPosition();
        if (currentPosition - 10000 > 0) {
            vvVideo.seekTo(currentPosition - 10000);
        } else {
            vvVideo.seekTo(0);
        }
    }

    private void screenSize() {
        if (isFullScreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isFullScreen = false;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isFullScreen = true;
        }
    }

    private void showControls(ConstraintLayout clControl) {
        if (clControl.getVisibility() == View.GONE) {
            clControl.setVisibility(View.VISIBLE);
            clControl.postDelayed(() -> clControl.setVisibility(View.GONE), 3000); // Hide after 3 seconds
        } else {
            clControl.setVisibility(View.GONE);
        }
    }


    private void clickLike(View view, TextView tv_like) {
        view.setSelected(!view.isSelected());
        if (view.isSelected()) {
            video.setLikes(video.getLikes() + 1);
            tv_like.setText("Likes: " + video.getLikes());
            Toast.makeText(getApplicationContext(), "Liked!", Toast.LENGTH_SHORT).show();
        } else {
            video.setLikes(video.getLikes() - 1);
            tv_like.setText("Likes: " + video.getLikes());
            Toast.makeText(getApplicationContext(), "Unliked!", Toast.LENGTH_SHORT).show();
        }
        videoViewModel.update(video);
    }

    private void shareVideo() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, video.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, "Check out this video: " + video.getVideoUrl());
        startActivity(Intent.createChooser(intent, "Share Video"));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup.LayoutParams params = vvVideo.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape mode
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Portrait mode
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) getResources().getDimension(R.dimen.video_height);
        }
        vvVideo.setLayoutParams(params);
    }

    private void addComment(EditText et_CommentInput) {
        if (UserManager.getInstance().getToken() != null) {
            String commentText = et_CommentInput.getText().toString().trim();
            if (!commentText.isEmpty()) {
                Comment newComment = new Comment(video.getApiId(), currentUser.getApiId(), currentUser.getNickname(), commentText, currentUser.getAvatar());
                commentList.add(newComment);
                commentAdapter.notifyItemInserted(commentList.size() - 1);
                commentViewModel.add(newComment);
                et_CommentInput.setText("");
            } else {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            CustomToast.showToast(this, "Option available just for registered users");
        }
    }

    @Override
    public void onEditComment(Comment comment, int position) {
        // Show dialog to edit comment
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Comment");

        final EditText input = new EditText(this);
        input.setText(comment.getText());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newCommentText = input.getText().toString();
            comment.setText(newCommentText);
            commentAdapter.notifyItemChanged(position);
            commentViewModel.update(comment);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onDeleteComment(Comment comment, int position) {
        commentList.remove(position);
        commentAdapter.notifyItemRemoved(position);
        commentAdapter.notifyItemRangeChanged(position, commentList.size());
        commentViewModel.delete(comment);
    }

    private void loadUserPic(String avatar) {
        if (avatar.startsWith(" ")){
           avatar= avatar.replace(" ","");
        }
        String profileImageBase64 = avatar;
        if (avatar.equals("/localPhotos/Maayan.png")) {
            profileImageView.setImageResource(R.drawable.maayan);
        } else if (avatar.equals("/localPhotos/Alon.png")) {
            profileImageView.setImageResource(R.drawable.alon);
        } else if (avatar.equals("/localPhotos/Tom.png")) {
            profileImageView.setImageResource(R.drawable.tom);
        } else if (avatar.equals("/localPhotos/defualtAvatar.png")) {
            profileImageView.setImageResource(R.drawable.profile_pic);
        } else if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
            if (!profileImageBase64.startsWith("data:image/")) {
                profileImageBase64 = "data:image/jpeg;base64," + profileImageBase64;
            }
            byte[] decodedString = Base64.decode(profileImageBase64.split(",")[1], Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImageView.setImageBitmap(decodedByte);

        } else {
            profileImageView.setImageResource(R.drawable.profile_pic);
        }
    }
}

