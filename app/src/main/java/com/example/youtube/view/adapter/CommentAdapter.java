package com.example.youtube.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtube.R;
import com.example.youtube.model.User;
import com.example.youtube.model.UserManager;
import com.example.youtube.utils.CustomToast;
import com.example.youtube.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTextView, commentTextView;
        public ImageButton ib_collapse;
        public LinearLayout llCollapse;
        public TextView textViewEdit, textViewDelete;

        public CommentViewHolder(View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            ib_collapse = itemView.findViewById(R.id.ib_collapse);
            llCollapse = itemView.findViewById(R.id.ll_collapse);
            textViewEdit = itemView.findViewById(R.id.tv_edit);
            textViewDelete = itemView.findViewById(R.id.tv_delete);
        }
    }

    private final LayoutInflater inflater;
    private List<Comment> commentList;
    private final Context context;
    CommentAdapterListener listener;
    private User currentUser = UserManager.getInstance().getCurrentUser();

    public CommentAdapter(Context context, CommentAdapterListener listener) {
        inflater = LayoutInflater.from(context);
        this.commentList = new ArrayList<>();
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.comment_list_item, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.authorTextView.setText(comment.getUsername());
        holder.commentTextView.setText(comment.getText());

        holder.ib_collapse.setOnClickListener(v -> {
            if (holder.llCollapse.getVisibility() == View.GONE) {
                holder.llCollapse.setVisibility(View.VISIBLE);
            } else {
                holder.llCollapse.setVisibility(View.GONE);
            }
        });

        holder.textViewEdit.setOnClickListener(v -> {
            if (currentUser != null && currentUser.getUsername().equals(comment.getUsername())) {
                listener.onEditComment(comment, position);
                holder.llCollapse.setVisibility(View.GONE); // Collapse after editing
            } else {
                CustomToast.showToast(context, "Option available just for the author user");
            }
        });


        holder.textViewDelete.setOnClickListener(v -> {
            if (currentUser != null && currentUser.getUsername().equals(comment.getUsername())) {
                listener.onDeleteComment(comment, position);
            } else {
                CustomToast.showToast(context, "Option available just for the author user");
            }
        });
    }

    public interface CommentAdapterListener {
        void onEditComment(Comment comment, int position);

        void onDeleteComment(Comment comment, int position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setComments(List<Comment> comments) {
        this.commentList = comments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


}


