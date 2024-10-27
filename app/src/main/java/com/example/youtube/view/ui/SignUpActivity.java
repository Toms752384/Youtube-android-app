package com.example.youtube.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.youtube.R;
import com.example.youtube.model.User;
import com.example.youtube.model.UserManager;
import com.example.youtube.viewModel.UserViewModel;

public class SignUpActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private UserManager userManager;
    private UserViewModel userViewModel;
    private SwipeRefreshLayout srl_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize SwipeRefreshLayout
        srl_refresh = findViewById(R.id.srl_refresh);

        // Initialize UserViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize UserManager
        userManager = UserManager.getInstance();

        ImageView logoImage = findViewById(R.id.logoImage);
        usernameEditText = findViewById(R.id.TextUserName);
        passwordEditText = findViewById(R.id.TextPassword);

        Button buttonForSignUp = findViewById(R.id.buttonForSignUp);
        buttonForSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistrationActivity2.class);
            startActivity(intent);
        });

        Button buttonForHomePage = findViewById(R.id.loginButton);
        buttonForHomePage.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                showCustomToast("Enter username/password");
            } else {
                userViewModel.isExist(username).observe(this, isExist -> {
                    if (isExist) {
                        userViewModel.matchAccount(username, password).observe(this, result -> {
                            if (result) {
                                userViewModel.getUserByUsername(username).observe(this, user -> {
                                    userManager.setCurrentUser(user);
                                    userViewModel.createToken(user); //create token from server
                                    showCustomToast("Login successfully!");
                                    usernameEditText.setText("");
                                    passwordEditText.setText("");
                                    Intent intent = new Intent(SignUpActivity.this, VideoListActivity.class);
                                    startActivity(intent);
                                });
                            } else {
                                showCustomToast("Password and username do not match");
                            }
                        });
                    } else {
                        showCustomToast("Username does not exist");
                    }
                });
            }
        });



        if (savedInstanceState != null) {
            String username = savedInstanceState.getString("username");
            String password = savedInstanceState.getString("password");

            usernameEditText.setText(username);
            passwordEditText.setText(password);
        }

        logoImage.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, VideoListActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("username", usernameEditText.getText().toString());
        outState.putString("password", passwordEditText.getText().toString());
    }

    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
