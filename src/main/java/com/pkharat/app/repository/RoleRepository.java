package com.pkharat.app.repository;

import java.util.Optional;

import com.pkharat.app.model.ERole;
import com.pkharat.app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
