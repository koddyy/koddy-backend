package com.koddy.server.member.domain.repository;

import com.koddy.server.member.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
