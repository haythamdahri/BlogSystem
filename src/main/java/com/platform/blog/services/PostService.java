package com.platform.blog.services;

import com.platform.blog.entities.Post;
import com.platform.blog.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PostService {
    public Post savePost(Post post);
    public Post getPost(Long id);
    public Post getPostByCreatorId(Long postId, Long creatorId);
    public boolean deletePost(Long id);
    public List<Post> getPosts();
    public Page<Post> getPagedPosts(int page, int size);
    public Page<Post> getActivePagedPosts(int page, int size);
    public Long getUserPostsViews(User user);
    public Long getApprouvedPostsCount(User user);
    public Long getRejectedPostsCount(User user);
    public List<Post> getUserPosts(User creator);
    public Page<Post> getPagedUserPosts(Long id, int page, int size);
    public Page<Post> getActivePagedUserPosts(User creator, int page, int size);
}
