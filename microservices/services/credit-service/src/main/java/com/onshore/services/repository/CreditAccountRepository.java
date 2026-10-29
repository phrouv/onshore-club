package com.onshore.services.repository;

import com.onshore.services.model.CreditAccount;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface CreditAccountRepository extends JpaRepository<CreditAccount, UUID> {

  /** Row lock so concurrent debits cannot overspend a balance. */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select a from CreditAccount a where a.userId = :userId")
  Optional<CreditAccount> lockByUserId(UUID userId);

  @Query("select coalesce(sum(a.balance), 0) from CreditAccount a")
  long totalBalance();

  List<CreditAccount> findAll();
}
