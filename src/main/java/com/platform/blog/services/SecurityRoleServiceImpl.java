package com.platform.blog.services;

import com.platform.blog.dao.SecurityRoleRepository;
import com.platform.blog.entities.security.RoleType;
import com.platform.blog.entities.security.SecurityRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("securityRoleServiceImpl")
public class SecurityRoleServiceImpl implements SecurityRoleService{

    /*
    * @Inject a new instance of user repository
    * */
    @Autowired
    private SecurityRoleRepository securityRoleRepository;

    /*
     * @Insert or update a securityRole in the database using the user repository
     * */
    @Override
    public SecurityRole saveSecurityRole(SecurityRole securityRole) {
        return this.securityRoleRepository.save(securityRole);
    }

    /*
     * @Retrieve an existing securityRole in the database using the user repository
     * */
    @Override
    public SecurityRole getSecurityRole(Long id) {
        Optional<SecurityRole> optional = this.securityRoleRepository.findById(id);
        if( optional.isPresent() ){
            return optional.get();
        }
        return null;
    }

    /*
     * @Retrieve an existing securityRole in the database using the user repository
     * */
    @Override
    public SecurityRole getSecurityRoleByRoleType(RoleType roleType){
        return this.securityRoleRepository.findByRole(roleType);
    }

    /*
     * @Retrieve an existing securityRole in the database using the user repository
     * */
    @Override
    public boolean deleteSecurityRoleByRoleType(RoleType roleType){
        this.securityRoleRepository.delete(this.getSecurityRoleByRoleType(roleType));
        return true;
    }


    /*
     * @Delete an existing user in the database using the user repository
     * */
    @Override
    public boolean deleteSecurityRole(Long id) {
        Optional<SecurityRole> optional= this.securityRoleRepository.findById(id);
        if( optional.isPresent() ){
            SecurityRole securityRole = optional.get();
            this.securityRoleRepository.delete(securityRole);
            return true;
        }
        return false;
    }

    /*
     * @Retrieve all existing securityRoles in the database using the user repository
     * */
    @Override
    public List<SecurityRole> getSecurityRoles() {
        return this.securityRoleRepository.findAll();
    }

}
