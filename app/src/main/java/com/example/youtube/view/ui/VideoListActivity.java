package com.example.youtube.view.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.youtube.R;

import com.example.youtube.model.User;
import com.example.youtube.utils.CustomToast;
import com.example.youtube.model.Video;
import com.example.youtube.utils.EditUserDialogFragment;
import com.example.youtube.view.adapter.VideoListAdapter;
import com.example.youtube.viewModel.UserViewModel;
import com.example.youtube.viewModel.VideoViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import com.example.youtube.model.UserManager;

public class VideoListActivity extends AppCompatActivity implements VideoListAdapter.VideoAdapterListener {

    VideoListAdapter videoListAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private ShapeableImageView profileImageView;
    private VideoViewModel videoViewModel;
    private UserViewModel userViewModel;
    private List<Video> currentVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);

        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // RecyclerView for displaying the video list
        SwipeRefreshLayout srl_refresh = findViewById(R.id.srl_refresh);
        RecyclerView rvListVideo = findViewById(R.id.rvListVideo);
        rvListVideo.setLayoutManager(new LinearLayoutManager(this));
        videoListAdapter = new VideoListAdapter(this, this);
        rvListVideo.setAdapter(videoListAdapter);
        videoViewModel.get().observe(this, videos -> {
            videoListAdapter.setVideos(videos);
            currentVideos = videos;
            srl_refresh.setRefreshing(false);
        });

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Initialize DrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::NavigationItemSelected);

        // Initialize user info views from header
        View headerView = navigationView.getHeaderView(0);
        profileImageView = headerView.findViewById(R.id.profileImageView);

        // Load user data from UserManager
        loadUserInfoFromManager();

        // Search function
        SearchView sv_search = findViewById(R.id.svSearch);
        sv_search.clearFocus();
        sv_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterVideoList(newText);
                return true;
            }
        });

        // Refresh function
        srl_refresh.setOnRefreshListener(() -> {
            videoViewModel.reload();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("VideoListActivity", "onDestroy called");
    }

    private boolean NavigationItemSelected(MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.login_yes) {
                Intent intentForLogIn = new Intent(VideoListActivity.this, SignUpActivity.class);
                CustomToast.showToast(VideoListActivity.this, "Login");
                startActivity(intentForLogIn);
                return true;
            } else if (itemId == R.id.logout_yes) {
                // Clear user session data
                UserManager.getInstance().clearCurrentUser();
                UserManager.getInstance().clearToken(); //added clear token!!!!
                // Navigate to login page
                Intent intentForLogIn = new Intent(VideoListActivity.this, SignUpActivity.class);
                CustomToast.showToast(VideoListActivity.this, "Logout");
                startActivity(intentForLogIn);
                finish(); // Close the current activity
                return true;

            }
            else  if (item.getItemId() == R.id.edit_user) {
                if (UserManager.getInstance().getToken() != null) {
                    EditUserDialogFragment dialogFragment = new EditUserDialogFragment();
                    dialogFragment.show(getSupportFragmentManager(), "EditUserDialogFragment");
                }
                else {
                    CustomToast.showToast(this, "Option available only for registered users");
                }
                return true;
            } else if (itemId == R.id.upload_data_yes) {
                if (UserManager.getInstance().getToken() != null) {
                    Intent intentForVideo = new Intent(VideoListActivity.this, AddVideoActivity.class);
                    CustomToast.showToast(VideoListActivity.this, "Upload video");
                    startActivity(intentForVideo);
                } else {
                    CustomToast.showToast(this, "Option available only for registered users");
                }
                return true;
            } else if (itemId == R.id.dark_mode_yes) {
                // Toggle dark mode
                int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    CustomToast.showToast(VideoListActivity.this, "Switched to Dark Mode");
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    CustomToast.showToast(VideoListActivity.this, "Switched to Light Mode");
                }
                return true;
            } else if (itemId == R.id.Help) {
                CustomToast.showToast(VideoListActivity.this, "Help");
                return true;
            }
            else if (itemId == R.id.delete_user) {
                if (UserManager.getInstance().getToken() != null) {
                    CustomToast.showToast(VideoListActivity.this, "Delete User");
                    userViewModel.delete(UserManager.getInstance().getCurrentUser()); //delete user from database
                    UserManager.getInstance().clearCurrentUser(); //clear logged in user from user manager
                    UserManager.getInstance().clearToken(); //clear token from user manager
                    Intent intentForDeleteUser = new Intent(VideoListActivity.this, SignUpActivity.class);
                    startActivity(intentForDeleteUser);
                    finish(); // Close the current activity
                }
                else {
                    CustomToast.showToast(VideoListActivity.this, "You need to log in to delete user");
                }
                return true;
            }
            return true;

    }

    private void loadUserInfoFromManager() {
        UserManager userManager = UserManager.getInstance();
        User currentUser = userManager.getCurrentUser();

        if (currentUser != null) {
            String username = currentUser.getUsername();
            String nickname = currentUser.getNickname();
            String profileImageBase64 = currentUser.getAvatar();

            Log.d("VideoListActivity", "Loading user info: username=" + username + ", nickname=" + nickname);

            if (currentUser.getAvatar().equals("/localPhotos/Maayan.png")) {
                profileImageView.setImageResource(R.drawable.maayan);
            } else if (currentUser.getAvatar().equals("/localPhotos/Alon.png")) {
                profileImageView.setImageResource(R.drawable.alon);
            } else if (currentUser.getAvatar().equals("/localPhotos/Tom.png")) {
                profileImageView.setImageResource(R.drawable.tom);
            } else if (currentUser.getAvatar().equals("/localPhotos/defualtAvatar.png")) {
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

            // Update Navigation Drawer menu items
            updateNavigationDrawer(username, nickname);
        } else {
            profileImageView.setImageResource(R.drawable.profile_pic);
        }
    }

    private void updateNavigationDrawer(String username, String nickname) {
        MenuItem usernameItem = navigationView.getMenu().findItem(R.id.profile_username);
        MenuItem nicknameItem = navigationView.getMenu().findItem(R.id.profile_nickname);

        if (usernameItem != null) {
            usernameItem.setTitle(username);
        }
        if (nicknameItem != null) {
            nicknameItem.setTitle(nickname);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void filterVideoList(String text) {
        List<Video> filteredList = new ArrayList<>();
        for (Video video : currentVideos) {
            if (video.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(video);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No video found", Toast.LENGTH_SHORT).show();
        } else {
            videoListAdapter.setFilterList(filteredList);
        }
    }

    @Override
    public void onEditVideo(Video video, int position) {
        // Show dialog to edit comment
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Title and Description");

        // Create a LinearLayout to hold the EditTexts
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10); // Optional: Add padding for better UI

        // Create EditTexts for title and description
        final EditText inputTitle = new EditText(this);
        inputTitle.setHint("Title");
        inputTitle.setText(video.getTitle());
        layout.addView(inputTitle);

        final EditText inputDescription = new EditText(this);
        inputDescription.setHint("Description");
        inputDescription.setText(video.getDescription());
        layout.addView(inputDescription);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newTitle = inputTitle.getText().toString();
            String newDescription = inputDescription.getText().toString();
            video.setTitle(newTitle);
            video.setDescription(newDescription);
            videoViewModel.update(video);
            videoListAdapter.notifyItemChanged(position);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onDeleteVideo(Video video, int position) {
        videoViewModel.delete(video);
        currentVideos.remove(position);
        videoListAdapter.notifyItemRemoved(position);
        videoListAdapter.notifyItemRangeChanged(position, currentVideos.size());
    }
}
