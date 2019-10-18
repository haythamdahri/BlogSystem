package com.platform.blog.services;

import com.platform.blog.entities.User;
import com.platform.blog.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    /*
    * @Inject a new instance of user repository
    * */
    @Autowired
    private UserRepository userRepository;

    /*
     * @Insert or update a user in the database using the user repository
     * */
    @Override
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    /*
     * @Retrieve an existing user in the database using the user repository
     * */
    @Override
    public User getUser(Long id) {
        Optional<User> optional = this.userRepository.findById(id);
        if( optional.isPresent() ){
            return optional.get();
        }
        return null;
    }

    /*
     * @Retrieve an existing user in the database using the user repository
     * */
    @Override
    public User getUser(String email) {
        User user = this.userRepository.findUsersByEmail(email);
        return user;
    }

    /*
     * @Delete an existing user in the database using the user repository
     * */
    @Override
    public boolean deleteUser(Long id) {
        Optional<User> optional= this.userRepository.findById(id);
        if( optional.isPresent() ){
            User user = optional.get();
            this.userRepository.delete(user);
            return true;
        }
        return false;
    }

    /*
     * @Retrieve all existing users in the database using the user repository
     * */
    @Override
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }
}
