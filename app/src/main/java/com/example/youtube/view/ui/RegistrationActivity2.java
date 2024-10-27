package com.example.youtube.view.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.youtube.R;
import com.example.youtube.model.User;
import com.example.youtube.utils.PasswordValidator;
import com.example.youtube.utils.UploadImage;
import com.example.youtube.viewModel.UserViewModel;

public class RegistrationActivity2 extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 100;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText nicknameEditText;
    private PasswordValidator passwordValidator;
    private UploadImage uploadImage;
    private UserViewModel userViewModel;
    private String profileImageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        passwordValidator = new PasswordValidator(userViewModel);
        uploadImage = new UploadImage(this);

        usernameEditText = findViewById(R.id.TextUserName);
        passwordEditText = findViewById(R.id.TextPassword);
        confirmPasswordEditText = findViewById(R.id.TextVerificationPassword);
        nicknameEditText = findViewById(R.id.TextNikeName);

        ImageView logoImage = findViewById(R.id.logoImage);
        logoImage.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity2.this, VideoListActivity.class);
            startActivity(intent);
        });

        checkPermissions();

        Button uploadImageButton = findViewById(R.id.button_upload_image);
        uploadImageButton.setOnClickListener(v -> uploadImage.showImagePickerDialog());

        Button buttonForSignUp = findViewById(R.id.alredyReg);
        buttonForSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            passwordValidator.registerUser(
                    RegistrationActivity2.this,
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString(),
                    confirmPasswordEditText.getText().toString(),
                    nicknameEditText.getText().toString(),
                    new PasswordValidator.ValidationCallback() {
                        @Override
                        public void onValidationResult(String resultMessage) {
                            showCustomToast(resultMessage);

                            if (resultMessage.equals("User registered successfully")) {
                                String avatarBase64 = profileImageBase64 != null ? profileImageBase64 : "";
                                if (avatarBase64.isEmpty()) {
                                    avatarBase64 = "/localPhotos/defualtAvatar.png";
                                } else if (!avatarBase64.startsWith("data:image/")) {
                                    avatarBase64 = "data:image/jpeg;base64," + avatarBase64;
                                }
                                User newUser = new User(usernameEditText.getText().toString(), passwordEditText.getText().toString(),
                                        nicknameEditText.getText().toString(), avatarBase64);
                                userViewModel.insert(newUser);
                                Intent intent = new Intent(RegistrationActivity2.this, SignUpActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
            );
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
                Toast.makeText(this, "Camera and storage permissions are required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setProfileImageBase64(String base64Image) {
        this.profileImageBase64 = base64Image;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uploadImage.handleActivityResult(requestCode, resultCode, data);
    }
}
