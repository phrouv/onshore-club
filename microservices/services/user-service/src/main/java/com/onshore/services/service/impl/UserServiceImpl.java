package com.onshore.services.service.impl;

import com.onshore.common_lib.exception.UserException;
import com.onshore.services.model.User;
import com.onshore.services.repository.UserRepository;
import com.onshore.services.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public User getUserByEmail(String email) throws UserException {
    User user = userRepository.findByEmail(email);
    if (user == null) {
      throw new UserException("User not found with email: " + email);
    }
    return user;
  }

  @Override
  public User getUserById(UUID id) throws UserException {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new UserException("User not found with id: " + id));
  }

  @Override
  public List<User> getUsers() throws UserException {
    return userRepository.findAll();
  }
}
