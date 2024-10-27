package com.example.youtube.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube.R;
import com.example.youtube.model.User;
import com.example.youtube.model.UserManager;
import com.example.youtube.utils.CustomToast;
import com.example.youtube.view.ui.VideoDisplayActivity;
import com.example.youtube.model.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for managing the list of videos in a RecyclerView.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDescription,tvViews,tvLikes,tvAuthor; // TextViews for title, description, views, likes
        public VideoView vvVideo; // VideoView for displaying the video
        public ImageButton ib_collapse;
        public LinearLayout llCollapse;
        public TextView tv_edit, tv_delete;

        /**
         * ViewHolder constructor to initialize the views.
         *
         * @param itemView The view of the individual item.
         */
        public VideoViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            vvVideo = itemView.findViewById(R.id.vvVideo);
            tvViews = itemView.findViewById(R.id.tv_view);
            tvLikes = itemView.findViewById(R.id.tv_like);
            tvAuthor = itemView.findViewById(R.id.tv_author);

            ib_collapse = itemView.findViewById(R.id.ib_collapse);
            llCollapse = itemView.findViewById(R.id.ll_collapse);
            tv_edit = itemView.findViewById(R.id.tv_edit);
            tv_delete = itemView.findViewById(R.id.tv_delete);
        }
    }

    private final LayoutInflater inflater;
    private List<Video> videoList; // List of videos to display
    private final Context context;
    private VideoAdapterListener listener;
    private User currentUser = UserManager.getInstance().getCurrentUser();


    public VideoListAdapter(Context context, VideoAdapterListener listener) {
        inflater =LayoutInflater.from(context);
        this.videoList = new ArrayList<>();
        this.listener=listener;
        this.context=context;
    }


    /**
     * Method to create new ViewHolder instances.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new instance of VideoViewHolder.
     */
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.video_list_item, parent, false);
        return new VideoViewHolder(itemView);
    }


    /**
     * Method to bind data to the views in each ViewHolder.
     *
     * @param holder   The VideoViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {

        final Video video = videoList.get(position);
        holder.tvTitle.setText(video.getTitle());
        holder.tvDescription.setText("Description: "+ video.getDescription());
        holder.tvViews.setText("Views: " + video.getViews());
        holder.tvLikes.setText("Likes: " + video.getLikes());
        holder.tvAuthor.setText("Author: " + video.getUserName());

        holder.vvVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                String errorMessage;
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        errorMessage = "Unknown media error";
                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        errorMessage = "Media server died";
                        break;
                    default:
                        errorMessage = "Media error: " + what + " , " + extra;
                        break;
                }

                switch (extra) {
                    case MediaPlayer.MEDIA_ERROR_IO:
                        errorMessage += " (IO error)";
                        break;
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                        errorMessage += " (Malformed media)";
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        errorMessage += " (Unsupported format)";
                        break;
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        errorMessage += " (Media timeout)";
                        break;
                    default:
                        errorMessage += " (Unknown error: " + extra + ")";
                        break;
                }

                Log.e("vvVideoError", errorMessage);
                return true; // Returning true indicates we handled the error
            }
        });

        // Set the video URI
        holder.vvVideo.setVideoURI(Uri.parse(video.getVideoUrl()));
        holder.vvVideo.seekTo(50000);

        // Set click listener to play video on click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoDisplayActivity.class);
            intent.putExtra("extra_video", video);
            context.startActivity(intent);
        });

        //collapse function
        holder.ib_collapse.setOnClickListener(v -> {
            if (holder.llCollapse.getVisibility() == View.GONE) {
                holder.llCollapse.setVisibility(View.VISIBLE);
            } else {
                holder.llCollapse.setVisibility(View.GONE);
            }
        });
        //edit function
        holder.tv_edit.setOnClickListener(v -> {
            if (currentUser != null && currentUser.getUsername().equals(video.getUserName())){

                listener.onEditVideo(video, position);
                holder.llCollapse.setVisibility(View.GONE); // Collapse after editing
            }else {   CustomToast.showToast(context, "Option available just for the author user");}
        });
        //delete function
        holder.tv_delete.setOnClickListener(v -> {
            if (currentUser != null && currentUser.getUsername().equals(video.getUserName())) {
                listener.onDeleteVideo(video,position);
            }else {CustomToast.showToast(context, "Option available just for the author user");} });
    }



    public interface VideoAdapterListener {
        void onEditVideo(Video video, int position);
        void onDeleteVideo(Video video, int position);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setVideos(List<Video> videos){
        this.videoList=videos;
        notifyDataSetChanged();
    }

    /**
     * Method to return the total number of items.
     *
     * @return The total number of video items.
     */
    @Override
    public int getItemCount() {
        return videoList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(List<Video> filterList){
        this.videoList=filterList;
        notifyDataSetChanged();
    }

}
