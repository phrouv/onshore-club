package com.onshore.common_lib.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/** Optional geographical coordinates of a site. */
@Embeddable
@Getter
@Setter
public class GeoLocation {

  @Column(name = "latitude")
  private BigDecimal latitude;

  @Column(name = "longitude")
  private BigDecimal longitude;
}
