package com.platform.blog.services;

import com.platform.blog.entities.Comment;
import com.platform.blog.entities.security.SecurityUser;

import java.util.List;

public interface SecurityUserService {
    public SecurityUser saveSecurityUser(SecurityUser securityUser);
    public SecurityUser getSecurityUser(Long id);
    public SecurityUser getSecurityUser(String email);
    public boolean deleteSecurityUser(String email);
    public List<SecurityUser> getSecurityUsers();
}
