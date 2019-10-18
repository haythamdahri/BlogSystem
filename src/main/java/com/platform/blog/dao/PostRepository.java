package com.platform.blog.dao;

import com.platform.blog.entities.Post;
import com.platform.blog.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@RepositoryRestResource(path = "/posts")
// Allow sources to access into the infrastructure: [*: for all]
@CrossOrigin(value = "*")
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {


    public Post findPostByIdAndCreator_Id(@Param("id") long postId, @Param("creator_Id") long creatorId);

    /*
    * @Retrieve paged active posts from database
    */
    public Page<Post> findByActive(@Param("active") boolean active, @PageableDefault Pageable pageable);

    /*
     * @Retrieve paged active posts from database written by a user
     */
    public List<Post> findByCreatorAndActiveTrue(User creator);

    /*
     * @Retrieve paged posts from database written by a user
     */
    public List<Post> findByCreator(User creator);

    /*
     * @Retrieve paged posts from database written by a user
     */
    public Page<Post> findByCreatorId(@Param("id") long id,  @PageableDefault Pageable pageable);


    /*
     * @Retrieve all active posts from database written by a user
     */
    public List<Post> findByActiveTrue();

    // We are using @RestResource to disable method export to prevvent use it with same name so that we prevent ambiguity problems with rest api
    @RestResource(exported = false)
    public Page<Post> findByCreatorAndActiveTrue(User creator, @PageableDefault Pageable pageable);

    /*
     * @Calculate sum of views for a user's posts
     */
    @Query(value = "SELECT SUM(p.views) from Post as p where p.creator = :creator")
    public Long sumViewsByCreator(@Param("creator") User user);


    /*
     * @Count number of posts for a user
     */
    public Long countPostByActiveAndCreator(@Param("active") boolean active, @Param("creator") User user );


}
