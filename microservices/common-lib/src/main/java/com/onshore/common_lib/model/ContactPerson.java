package com.onshore.common_lib.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ContactPerson {

  @Size(max = 100, message = "firstName must not exceed 100 characters")
  @Column(name = "first_name", length = 100)
  private String firstName;

  @Size(max = 100, message = "lastName must not exceed 100 characters")
  @Column(name = "last_name", length = 100)
  private String lastName;

  @Size(max = 500, message = "email must not exceed 500 characters")
  @Column(name = "email", length = 500)
  private String email;

  @Size(max = 100, message = "phoneNumber must not exceed 100 characters")
  @Column(name = "phone_number", length = 100)
  private String phoneNumber;
}
