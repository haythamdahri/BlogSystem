package com.platform.blog.services;

import com.platform.blog.entities.Comment;
import com.platform.blog.dao.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("commentServiceImpl")
public class CommentServiceImpl implements CommentService {

    /*
     * @Inject a new instance of comment repository
     * */
    @Autowired
    private CommentRepository commentRepository;

    /*
     * @Insert or update a comment in the database using the comment repository
     * */
    @Override
    public Comment addComment(Comment comment) {
        return this.commentRepository.save(comment);
    }

    /*
     * @Retrieve an existing comment in the database using the comment repository
     * */
    @Override
    public Comment getComment(Long id) {
        Optional<Comment> optional = this.commentRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    /*
     * @Delete an existing comment in the database using the comment repository
     * */
    @Override
    public boolean deleteComment(Long id) {
        Optional<Comment> optional = this.commentRepository.findById(id);
        if (optional.isPresent()) {
            Comment comment = optional.get();
            this.commentRepository.delete(comment);
            return true;
        }
        return false;
    }

    /*
     * @Retrieve all existing comments in the database using the comment repository
     * */
    @Override
    public List<Comment> getComments() {
        return this.commentRepository.findAll();
    }

}
