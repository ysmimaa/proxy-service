package com.proxy.service.repository;

import com.proxy.service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByAuthority(String authority);
}
