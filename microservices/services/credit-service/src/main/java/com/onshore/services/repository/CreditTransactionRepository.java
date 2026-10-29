package com.onshore.services.repository;

import com.onshore.common_lib.enums.CreditTransactionType;
import com.onshore.services.model.CreditTransaction;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, UUID> {

  List<CreditTransaction> findByUserIdOrderByCreatedAtDesc(UUID userId);

  List<CreditTransaction> findByType(CreditTransactionType type);
}
