package com.platform.blog.controllers;

import com.platform.blog.entities.Post;
import com.platform.blog.services.EmailService;
import com.platform.blog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// Allow access from all clients in the world
@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/v0/posts")
public class BlogRestController {

    /*
    * @Inject an instance of PostService
    */
    @Autowired
    private PostService postService;

    /*
     * @Inject an instance of EmailService
     */
    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.sender}")
    private String adminSender;

    /*
     * @Handle accept of reject a post
     */
    @RequestMapping(value = "/decide-post", method = RequestMethod.POST)
    public ResponseEntity<String> decidePost(@RequestParam(value = "id", defaultValue = "0") long id, @RequestParam(value = "action", defaultValue = "false") boolean action) {
        Post post = this.postService.getPost(id);
        if (post != null) {
            post.setActive(action);
            post = this.postService.savePost(post);
            Map<String, Object> data = new HashMap<>();

            if (post != null) {
                String text = post.isActive() ? "Congratulation, your post has been approved" : "Sorry, your post has been rejected";
                data.put("post", post);
                this.emailService.sendProfessionalMessage(this.adminSender, post.getCreator().getEmail(), "Post state updated", text, data);
                return new ResponseEntity<String>("Post has been update successflly!", HttpStatus.OK);
            }
            return new ResponseEntity<String>("No post found with id " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("No post found with id " + id, HttpStatus.NOT_FOUND);
    }

}
