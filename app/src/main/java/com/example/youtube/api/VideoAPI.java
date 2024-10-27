package com.example.youtube.api;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.youtube.R;
import com.example.youtube.api.response.videoResponse.VideoResponse;
import com.example.youtube.api.response.videoResponse.VideosResponse;
import com.example.youtube.model.UserManager;
import com.example.youtube.model.Video;
import com.example.youtube.model.daos.VideoDao;
import com.example.youtube.utils.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * VideoAPI class responsible for handling network operations related to videos.
 * It uses Retrofit to fetch video data from a remote server and Room for local database operations.
 */
public class VideoAPI {
    private MutableLiveData<List<Video>> videoListData;
    private final VideoDao videoDao;
    private final Retrofit retrofit;
    private final VideoWebServiceAPI videoWebServiceAPI;
    private final UserManager userManager = UserManager.getInstance();

    /**
     * Constructor for VideoAPI.
     *
     * @param videoListData MutableLiveData object to post the video list data.
     * @param videoDao      Data Access Object for video database operations.
     */
    public VideoAPI(MutableLiveData<List<Video>> videoListData, VideoDao videoDao) {
        this.videoListData = videoListData;
        this.videoDao = videoDao;

        // Initialize Retrofit with base URL and Gson converter
        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of the web service API
        videoWebServiceAPI = retrofit.create(VideoWebServiceAPI.class);
    }


    //Method to fetch all videos data from the server.
    public void fetchAllVideos() {
        // Make a network call to fetch videos
        Call<VideosResponse> call = videoWebServiceAPI.getVideos();
        call.enqueue(new Callback<VideosResponse>() {
            @Override
            public void onResponse(Call<VideosResponse> call, Response<VideosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Video> videos = response.body().getVideos();
                    adjustVideosUrls(videos); // Adjust the URLs here

                    // Sort videos by views in descending order
                    Collections.sort(videos, new Comparator<Video>() {
                        @Override
                        public int compare(Video v1, Video v2) {
                            return Integer.compare(v2.getViews(), v1.getViews());
                        }
                    });

                    // Get top 10 most viewed videos
                    List<Video> topViewedVideos = new ArrayList<>(videos.subList(0, Math.min(10, videos.size())));

                    // Get remaining videos after removing top 10
                    List<Video> remainingVideos = new ArrayList<>(videos.subList(Math.min(10, videos.size()), videos.size()));

                    // Shuffle the remaining videos to select 10 at random
                    Collections.shuffle(remainingVideos);
                    List<Video> randomVideos = new ArrayList<>(remainingVideos.subList(0, Math.min(10, remainingVideos.size())));

                    // Combine the lists
                    List<Video> selectedVideos = new ArrayList<>();
                    selectedVideos.addAll(topViewedVideos);
                    selectedVideos.addAll(randomVideos);

                    // Clear existing video data
                    videoDao.clear();

                    // Insert the selected videos data
                    videoDao.insertList(selectedVideos);

                    // Post the updated video list to LiveData
                    videoListData.postValue(videoDao.index());
                }

            }

            @Override
            public void onFailure(Call<VideosResponse> call, Throwable t) {
                // Handle the failure (e.g., log the error, notify the user)
                Log.e("api", t.getMessage());
            }
        });
    }


    // Fetch videos by user ID
    public LiveData<List<Video>> fetchVideosByUserId(String userId) {
        final MutableLiveData<List<Video>> userVideosliveData = new MutableLiveData<>();
        Call<VideosResponse> call = videoWebServiceAPI.getVideosByUserId(userId);
        call.enqueue(new Callback<VideosResponse>() {
            @Override
            public void onResponse(Call<VideosResponse> call, Response<VideosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("apiVideo", response.message());
                    List<Video> videos = response.body().getVideos();
                    adjustVideosUrls(videos);
                    // Adjust the URLs here
                    userVideosliveData.setValue(videos);
                }
            }

            @Override
            public void onFailure(Call<VideosResponse> call, Throwable t) {
                Log.e("apiVideo", t.getMessage());
                userVideosliveData.setValue(null);
            }
        });
        return userVideosliveData;
    }


    // Fetch a specific video by user ID and video ID
    public Video getVideo(String userId, String videoId) {
        final MutableLiveData<Video> video = new MutableLiveData<>();
        Call<VideoResponse> call = videoWebServiceAPI.getVideo(userId, videoId);
        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("apiVideo", response.message());
                    video.setValue(response.body().getVideo());

                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.e("apiVideo", t.getMessage());
                video.setValue(null);
            }
        });
        return video.getValue();
    }


    // Add a new video
    public void addVideo(Video video) {
        String token = "Bearer " + userManager.getToken();
        String videoUrl = video.getVideoUrl();
        File videoFile = new File(videoUrl);
        RequestBody videoRequestBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
        MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video", videoFile.getName(), videoRequestBody);

        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), video.getTitle());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), video.getDescription());
        RequestBody artist = RequestBody.create(MediaType.parse("text/plain"), video.getUserName());
        RequestBody views = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(video.getViews()));
        RequestBody subscribers = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(video.getSubscribers()));
        RequestBody likes = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(video.getLikes()));
        RequestBody avatar = RequestBody.create(MediaType.parse("text/plain"), video.getAvatar());
        RequestBody comments = RequestBody.create(MediaType.parse("application/json"), "[]"); // Empty JSON array
        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), video.getUserApiId()); // Add userId as part

        Call<VideoResponse> call = videoWebServiceAPI.addVideo(video.getUserApiId(), videoPart,
                title, description, artist, views, subscribers, likes, avatar, comments, userIdPart, token);
        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("apiVideo", response.message());
                    Video newVideo = response.body().getVideo();
                    newVideo.setRoomId(video.getRoomId());
                    adjustVideoUrl(newVideo);
                    videoDao.insert(newVideo);
                    // Post the updated list to videoListData
                    videoListData.postValue(videoDao.index());

                } else {
                    Log.e("apiVideoAdd", "Server error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.e("apiVideo", t.getMessage());
            }
        });
    }


    // Edit a video
    public void update(Video video) {
        if (userManager.getToken() == null) {
            Log.e("apiVideo", "Token is null");
            return; // Or handle appropriately, e.g., show an error message to the user
        }
        String token = "Bearer " + userManager.getToken();
        adjustVideoUrl(video);
        Call<VideoResponse> call = videoWebServiceAPI.update(video.getUserApiId(), video.getApiId(), video, token);
        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful()) {
                    Log.e("apiVideo", response.message());
                    adjustVideoUrl(video);
                    videoDao.update(video);
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.e("apiVideo", t.getMessage());
            }
        });
    }


    // Delete a video
    public void delete(Video video) {
        Log.e("apiVideo", video.getUserApiId() + "/videos/" + video.getApiId());

        Call<Void> call = videoWebServiceAPI.delete(video.getUserApiId(), video.getApiId(),
                "Bearer " + userManager.getToken());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    videoDao.delete(video);
                } else {
                    Log.e("apiVideo", "Server error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("apiVideo", t.getMessage());
            }
        });
    }


    //adjust the url to fit the localhost
    private void adjustVideoUrl(Video video) {
        String adjustedVideoUrl = video.getVideoUrl();
        if (adjustedVideoUrl.startsWith("http://10.0.2.2:80")) {
            adjustedVideoUrl = video.getVideoUrl().replace("http://10.0.2.2:80", "");
        } else if (adjustedVideoUrl.startsWith("http://localhost")) {
            adjustedVideoUrl = video.getVideoUrl().replace("http://localhost", "http://10.0.2.2");
        }
        else if (adjustedVideoUrl.startsWith("/localVideos")) {
            adjustedVideoUrl = "http://10.0.2.2:80" + adjustedVideoUrl;
        }else if (adjustedVideoUrl.startsWith("\\localVideos")) {
            adjustedVideoUrl = "http://10.0.2.2:80" + adjustedVideoUrl;
        }
        video.setVideoUrl(adjustedVideoUrl);
    }

    //adjust the url to fit the localhost
    private void adjustVideosUrls(List<Video> videos) {
        for (Video video : videos) {
            adjustVideoUrl(video);
        }
    }


}
