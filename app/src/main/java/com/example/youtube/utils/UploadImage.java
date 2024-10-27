package com.example.youtube.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import androidx.appcompat.app.AlertDialog;

import com.example.youtube.view.ui.RegistrationActivity2;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class UploadImage {
    public static final int PICK_IMAGE = 1;
    public static final int TAKE_PHOTO = 2;
    private final Activity activity;
    private Uri photoUri;

    public UploadImage(Activity activity) {
        this.activity = activity;
    }

    public void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Image");
        builder.setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    photoUri = createImageUri();
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    activity.startActivityForResult(takePicture, TAKE_PHOTO);
                    break;
                case 1:
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activity.startActivityForResult(pickPhoto, PICK_IMAGE);
                    break;
            }
        });
        builder.show();
    }

    private Uri createImageUri() {
        String fileName = "temp_photo_" + System.currentTimeMillis() + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = null;
            if (requestCode == PICK_IMAGE) {
                selectedImageUri = data.getData();
            } else if (requestCode == TAKE_PHOTO) {
                selectedImageUri = photoUri;
            }

            if (selectedImageUri != null) {
                String base64Image = convertImageToBase64(selectedImageUri);
                ((RegistrationActivity2) activity).setProfileImageBase64(base64Image);
            }
        }
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream imageStream = activity.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
