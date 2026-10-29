package com.onshore.services.service.impl;

import com.onshore.common_lib.enums.UserRole;
import com.onshore.services.model.User;
import com.onshore.services.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    initializeAdminUser();
  }

  private void initializeAdminUser() {
    String adminUsername = "pym.rouvray@gmail.com";

    if (userRepository.findByEmail(adminUsername) == null) {
      User adminUser = new User();

      adminUser.setPassword(passwordEncoder.encode("password"));
      adminUser.setFullName("admin");
      adminUser.setEmail(adminUsername);
      adminUser.setRole(UserRole.ROLE_SYSTEM_ADMIN);

      User admin = userRepository.save(adminUser);
    }
  }
}
