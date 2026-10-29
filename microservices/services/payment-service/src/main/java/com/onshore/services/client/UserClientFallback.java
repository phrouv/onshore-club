package com.onshore.services.client;

import com.onshore.common_lib.dto.UserDTO;
import com.onshore.common_lib.exception.UserException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

  @Override
  public UserDTO getUserById(UUID userId) throws UserException {
    return null;
  }
}
