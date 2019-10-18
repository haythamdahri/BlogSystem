package com.platform.blog.services;

import com.platform.blog.entities.Post;
import com.platform.blog.dao.PostRepository;
import com.platform.blog.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("postServiceImpl")
public class PostServiceImpl implements PostService {

    /*
     * @Inject a new instance of post repository
     * */
    @Autowired
    private PostRepository postRepository;

    /*
     * @Insert or update a post in the database using the post repository
     * */
    @Override
    public Post savePost(Post post) {
        return this.postRepository.save(post);
    }

    /*
     * @Retrieve an existing post in the database using the post repository
     * */
    @Override
    public Post getPost(Long id) {
        Optional<Post> optional = this.postRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    /*
     * @Retrieve an existing post in the database using the post repository
     * */
    @Override
    public Post getPostByCreatorId(Long postId, Long creatorId){
        return this.postRepository.findPostByIdAndCreator_Id(postId, creatorId);
    }

    /*
     * @Retrieve an existing post in the database using the post repository
     * */
    @Override
    public List<Post> getUserPosts(User creator){
        List<Post> posts = this.postRepository.findByCreator(creator);
        return posts;
    }

    /*
     * @Retrieve an existing post in the database using the post repository
     * */
    @Override
    public Page<Post> getPagedUserPosts(Long id, int page, int size){
        Page<Post> posts = this.postRepository.findByCreatorId(id, PageRequest.of(page, size));
        return posts;
    }

    /*
     * @Retrieve an existing post in the database using the post repository
     * */
    @Override
    public Page<Post> getActivePagedUserPosts(User creator, int page, int size){
        Page<Post> posts = this.postRepository.findByCreatorAndActiveTrue(creator, PageRequest.of(page, size));
        return posts;
    }

    /*
     * @Retrieve an existing post in the database using the post repository
     * */
    @Override
    public Long getUserPostsViews(User user){
        Long views = this.postRepository.sumViewsByCreator(user);
        return views == null ? 0 : views;
    }

    /*
     * @Count number of active posts
     * */
    public Long getApprouvedPostsCount(User user){
        return this.postRepository.countPostByActiveAndCreator(true, user);
    }

    /*
     * @Count number of inactive posts
     * */
    public Long getRejectedPostsCount(User user){
        return this.postRepository.countPostByActiveAndCreator(false, user);
    }


    /*
     * @Delete an existing post in the database using the post repository
     * */
    @Override
    public boolean deletePost(Long id) {
        Optional<Post> optional = this.postRepository.findById(id);
        if (optional.isPresent()) {
            Post post = optional.get();
            this.postRepository.delete(post);
            return true;
        }
        return false;
    }

    /*
     * @Retrieve all existing posts in the database using the post repository
     * */
    @Override
    public List<Post> getPosts() {
//        List<Post> posts = new ArrayList<>();
        List<Post> posts = this.postRepository.findByActiveTrue();
        System.out.println(posts.size());

        // First option
//        this.postRepository.findAll().iterator().forEachRemaining(posts::add);
        // Second option
        /*this.postRepository.findAll().iterator().forEachRemaining(post -> {
            posts.add(post);
        });*/
        return posts;
    }

    /*
     * @Retrieve paged existing posts in the database using the post repository
     * */
    @Override
    public Page<Post> getPagedPosts(int page, int size) {
//        return this.postRepository.findPagedPosts(PageRequest.of(page, max_per_page));
//        Page<Post> postsPage = this.postRepository.findAll(PageRequest.of(page, size));
        Page<Post> postsPage = this.postRepository.findAll(PageRequest.of(page, size));
        return postsPage;
    }


    /*
     * @Retrieve paged existing posts in the database using the post repository
     * */
    @Override
    public Page<Post> getActivePagedPosts(int page, int size) {
//        return this.postRepository.findPagedPosts(PageRequest.of(page, max_per_page));
//        Page<Post> postsPage = this.postRepository.findAll(PageRequest.of(page, size));
        Page<Post> postsPage = this.postRepository.findByActive(true, PageRequest.of(page, size));
        return postsPage;
    }

}
