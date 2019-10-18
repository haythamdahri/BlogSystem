package com.platform.blog.dao;

import com.platform.blog.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "/comments")
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
