package com.onshore.services.repository;

import com.onshore.common_lib.enums.UserRole;
import com.onshore.services.model.User;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  User findByEmail(String email);

  Set<User> findByRole(UserRole role);
}
