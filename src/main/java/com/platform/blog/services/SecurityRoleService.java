package com.platform.blog.services;

import com.platform.blog.entities.security.RoleType;
import com.platform.blog.entities.security.SecurityRole;
import com.platform.blog.entities.security.SecurityUser;

import javax.management.relation.Role;
import java.util.List;

public interface SecurityRoleService {
    public SecurityRole saveSecurityRole(SecurityRole securityRole);
    public SecurityRole getSecurityRole(Long id);
    public SecurityRole getSecurityRoleByRoleType(RoleType roleType);
    public boolean deleteSecurityRoleByRoleType(RoleType roleType);
    public boolean deleteSecurityRole(Long id);
    public List<SecurityRole> getSecurityRoles();
}
