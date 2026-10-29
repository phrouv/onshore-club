package com.onshore.services.service;

import com.onshore.common_lib.exception.UserException;
import com.onshore.services.model.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
  User getUserByEmail(String email) throws UserException;

  User getUserById(UUID id) throws UserException;

  List<User> getUsers() throws UserException;
}
