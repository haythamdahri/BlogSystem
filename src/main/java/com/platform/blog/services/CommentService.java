package com.platform.blog.services;

import com.platform.blog.entities.Comment;
import com.platform.blog.entities.Post;

import java.util.List;

public interface CommentService {
    public Comment addComment(Comment comment);
    public Comment getComment(Long id);
    public boolean deleteComment(Long id);
    public List<Comment> getComments();
}
