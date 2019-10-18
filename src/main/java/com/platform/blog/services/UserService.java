package com.platform.blog.services;

import com.platform.blog.entities.Post;
import com.platform.blog.entities.User;

import java.util.List;

public interface UserService {
    public User saveUser(User user);
    public User getUser(Long id);
    public User getUser(String email);
    public boolean deleteUser(Long id);
    public List<User> getUsers();
}
