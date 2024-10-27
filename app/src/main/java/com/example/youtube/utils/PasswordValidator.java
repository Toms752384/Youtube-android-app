package com.example.youtube.utils;

import android.content.Context;
import androidx.lifecycle.Observer;
import com.example.youtube.R;
import com.example.youtube.model.User;
import com.example.youtube.viewModel.UserViewModel;

public class PasswordValidator {
    private UserViewModel userViewModel;

    public PasswordValidator(UserViewModel userViewModel) {
        this.userViewModel = userViewModel;
    }

    public void registerUser(Context context, String username, String password, String confirmPassword, String nickname, ValidationCallback callback) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || nickname.isEmpty()) {
            callback.onValidationResult("All fields must be filled");
            return;
        }

        if (!isPasswordValid(password)) {
            callback.onValidationResult(context.getString(R.string.password_restrictions));
            return;
        }

        if (!password.equals(confirmPassword)) {
            callback.onValidationResult("The passwords do not match");
            return;
        }

        // Check if username is already taken asynchronously
        userViewModel.getUserByUsername(username).observeForever(new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    callback.onValidationResult("Username already taken");
                } else {
                    callback.onValidationResult("User registered successfully");
                }
                // Remove the observer after getting the result
                userViewModel.getUserByUsername(username).removeObserver(this);
            }
        });
    }

    public boolean isPasswordValid(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }
        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }

    public interface ValidationCallback {
        void onValidationResult(String result);
    }
}
