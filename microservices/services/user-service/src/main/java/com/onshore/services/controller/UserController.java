package com.onshore.services.controller;

import com.onshore.common_lib.dto.UserDTO;
import com.onshore.common_lib.exception.UserException;
import com.onshore.services.mapper.UserMapper;
import com.onshore.services.model.User;
import com.onshore.services.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/api/users/profile")
  public ResponseEntity<UserDTO> getUserProfile(@RequestHeader("X-User-Email") String email)
      throws UserException {
    User user = userService.getUserByEmail(email);
    return ResponseEntity.ok(UserMapper.toDTO(user));
  }

  @GetMapping("/api/users/{userId}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable UUID userId) throws UserException {
    User user = userService.getUserById(userId);
    return ResponseEntity.ok(UserMapper.toDTO(user));
  }

  @GetMapping("/api/users")
  public ResponseEntity<List<UserDTO>> getUsers() throws UserException {
    List<User> users = userService.getUsers();
    return ResponseEntity.ok(UserMapper.toDTOList(users));
  }
}
