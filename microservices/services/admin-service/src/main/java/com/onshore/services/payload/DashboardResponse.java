package com.onshore.services.payload;

import com.onshore.common_lib.club.BookingStats;
import com.onshore.common_lib.club.CreditStats;
import com.onshore.common_lib.club.PayoutStats;
import java.math.BigDecimal;

/**
 * revenue = monthly recurring revenue + credit pack purchases; margin = revenue - partner payouts.
 */
public record DashboardResponse(
    BookingStats bookings,
    CreditStats credits,
    PayoutStats payouts,
    BigDecimal revenue,
    BigDecimal margin) {}
