package com.example.youtube.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.youtube.model.Comment;
import com.example.youtube.model.repository.CommentsRepository;

import java.util.List;

public class CommentViewModel extends ViewModel {
    private CommentsRepository repository;
    private LiveData<List<Comment>> comments;

    public CommentViewModel() {
        repository = new CommentsRepository();
        comments = repository.getAll();
    }

    public LiveData<List<Comment>> get() {
        return comments;
    }

    public LiveData<List<Comment>> getCommentsByVideoId(int videoId) {
        return repository.getCommentsByVideoId(videoId);
    }

    public void add(Comment comment) {
        repository.add(comment);
    }

    public void delete(Comment comment) {
        repository.delete(comment);
    }

    public void update(Comment comment) {
        repository.update(comment);
    }
}
