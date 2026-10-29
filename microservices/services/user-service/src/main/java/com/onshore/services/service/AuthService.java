package com.onshore.services.service;

import com.onshore.common_lib.dto.UserDTO;
import com.onshore.common_lib.exception.UserException;
import com.onshore.common_lib.payload.response.AuthResponse;

public interface AuthService {
  AuthResponse login(String email, String password) throws UserException;

  AuthResponse signup(UserDTO req) throws UserException;
}
