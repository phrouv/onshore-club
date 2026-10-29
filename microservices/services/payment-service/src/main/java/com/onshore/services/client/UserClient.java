package com.onshore.services.client;

import com.onshore.common_lib.dto.UserDTO;
import com.onshore.common_lib.exception.UserException;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallback = UserClientFallback.class)
public interface UserClient {

  @GetMapping("/api/users/{userId}")
  UserDTO getUserById(@PathVariable UUID userId) throws UserException;
}
