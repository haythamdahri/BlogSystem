package com.platform.blog.services;

import com.platform.blog.dao.SecurityUserRepository;
import com.platform.blog.dao.UserRepository;
import com.platform.blog.entities.User;
import com.platform.blog.entities.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("securityUserServiceImpl")
public class SecurityUserServiceImpl implements SecurityUserService {

    /*
    * @Inject a new instance of user repository
    * */
    @Autowired
    private SecurityUserRepository securityUserRepository;

    /*
     * @Insert or update a securityUser in the database using the user repository
     * */
    @Override
    public SecurityUser saveSecurityUser(SecurityUser securityUser) {
        return this.securityUserRepository.save(securityUser);
    }

    /*
     * @Retrieve an existing securityUser in the database using the user repository
     * */
    @Override
    public SecurityUser getSecurityUser(Long id) {
        Optional<SecurityUser> optional = this.securityUserRepository.findById(id);
        if( optional.isPresent() ){
            return optional.get();
        }
        return null;
    }

    /*
     * @Retrieve an existing securityUser in the database by email using the user repository
     * */
    @Override
    public SecurityUser getSecurityUser(String email) {
        return this.securityUserRepository.findByEmail(email);
    }

    /*
     * @Delete an existing securityUser in the database using the user repository
     * */
    @Override
    public boolean deleteSecurityUser(String email) {
        return this.securityUserRepository.deleteByEmail(email);
    }

    /*
     * @Retrieve all existing securityUsers in the database using the user repository
     * */
    @Override
    public List<SecurityUser> getSecurityUsers() {
        return this.securityUserRepository.findAll();
    }


}
