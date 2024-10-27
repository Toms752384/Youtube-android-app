package com.example.youtube.api;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.example.youtube.api.response.TokenRequest;
import com.example.youtube.api.response.TokenResponse;
import com.example.youtube.api.response.UpdateUserResponse;
import com.example.youtube.api.response.UserResponse;
import com.example.youtube.model.User;
import com.example.youtube.model.UserManager;
import com.example.youtube.model.daos.UserDao;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAPI {
    private MutableLiveData<List<User>> userListData;
    private UserDao userDao;
    private Retrofit retrofit;
    private UserWebServiceAPI userWebServiceAPI;
    private static final String TAG = "UserAPI";
    private UserManager userManager;

    public UserAPI(MutableLiveData<List<User>> userListData, UserDao userDao) {
        this.userListData = userListData;
        this.userDao = userDao;

        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:80/") // base url
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userWebServiceAPI = retrofit.create(UserWebServiceAPI.class);

        // Initialize UserManager
        userManager = UserManager.getInstance();
    }

    public void getAllUsers() {
        Call<UsersResponse> call = userWebServiceAPI.getUsers();
        call.enqueue(new Callback<UsersResponse>() {
            @Override
            public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response: " + response.body().toString());
                    List<User> users = response.body().getUsers();
                    new Thread(() -> {
                        userDao.clear();
                        userDao.insertList(users);
                        userListData.postValue(userDao.index());
                    }).start();
                } else {
                    Log.e(TAG, "Response body is null or not successful");
                }
            }

            @Override
            public void onFailure(Call<UsersResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch users", t);
            }
        });
    }

    public void getUserById(String id) {
        Call<SingleUserResponse> call = userWebServiceAPI.getUser(id);
        call.enqueue(new Callback<SingleUserResponse>() {
            @Override
            public void onResponse(Call<SingleUserResponse> call, Response<SingleUserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response: " + response.body().toString());
                    User user = response.body().getUser();
                    new Thread(() -> {
                        userDao.insert(user);
                    }).start();
                } else {
                    Log.e(TAG, "Response body is null or not successful");
                }
            }

            @Override
            public void onFailure(Call<SingleUserResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch one user", t);
            }
        });
    }

    public void delete(User user) {
        String token = UserManager.getInstance().getToken();
        Call<Void> call = userWebServiceAPI.deleteUser(user.getApiId(), "Bearer " + token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                new Thread(() -> {
                    userDao.delete(user);
                    userListData.postValue(userDao.index());
                }).start();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle the failure
            }
        });
    }

    // we asked from server throw the retrofit - Call<Void> createUser(@Body User user)
    // - to sent an object to the server
    public void add(User user) {
        Call<Void> call = userWebServiceAPI.createUser(user);
        // we use call back to get the respond from the server
        // good respond - we save the user in room
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        userDao.insert(user);
                        userListData.postValue(userDao.index());
                    }).start();
                } else {
                    Log.e(TAG, "Response body is null or not successful");
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle the failure
                Log.e(TAG, "Failed to add a new user", t);
            }
        });
    }

    public void createToken(User user) {
        //use webServiceApi to call server and create token
        TokenRequest tokenRequest = new TokenRequest(user.getApiId());
        Call<TokenResponse> call = userWebServiceAPI.createToken(tokenRequest);

        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                //if call was successful, set the token to be the token from the server
                if (response.isSuccessful() && response.body() != null) {
                    userManager.setToken(response.body().getToken());
                    Log.d("Token", "Token: " + response.body().getToken());
                } else {
                    Log.e("Token", "Failed to get token");
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.e("Token", "Failed to get token", t);
            }
        });
    }

    public void updateUser(String nickname, String avatarBase64) {
        UserManager userManager = UserManager.getInstance();
        String token = userManager.getToken();
        String id = userManager.getCurrentUser().getApiId();

        // Prepare the request body
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("nickname", nickname);
        if (avatarBase64 != null) {
            jsonObject.addProperty("avatar", avatarBase64);
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        // Create the call
        Call<UpdateUserResponse> call = userWebServiceAPI.updateUser(id, "Bearer " + token, requestBody);

        call.enqueue(new Callback<UpdateUserResponse>() {
            @Override
            public void onResponse(Call<UpdateUserResponse> call, Response<UpdateUserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        userDao.update(response.body().getUser());
                        userListData.postValue(userDao.index());
                        userManager.setCurrentUser(response.body().getUser());
                    }).start();
                    Log.d(TAG, "User updated successfully: " + response.body().getUser());
                } else {
                    Log.e(TAG, "Response body is null or not successful. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UpdateUserResponse> call, Throwable t) {
                Log.e(TAG, "Failed to update user", t);
            }
        });
    }

}
