package com.onshore.services.service.impl;

import com.onshore.common_lib.club.CreditStats;
import com.onshore.common_lib.enums.CreditTransactionType;
import com.onshore.common_lib.enums.DemandLevel;
import com.onshore.common_lib.enums.SubscriptionStatus;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.services.mapper.CreditTransactionMapper;
import com.onshore.services.model.CreditAccount;
import com.onshore.services.model.CreditSetting;
import com.onshore.services.model.CreditTransaction;
import com.onshore.services.model.Subscription;
import com.onshore.services.payload.CreditTransactionResponse;
import com.onshore.services.repository.CreditAccountRepository;
import com.onshore.services.repository.CreditSettingRepository;
import com.onshore.services.repository.CreditTransactionRepository;
import com.onshore.services.repository.SubscriptionRepository;
import com.onshore.services.service.CreditService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditServiceImpl implements CreditService {

  /** Allowed credit packs: credits -> price in euros. */
  private static final Map<Integer, BigDecimal> PACKS =
      Map.of(10, new BigDecimal("15"), 25, new BigDecimal("35"));

  private static final String FALLBACK_CATEGORY = "Autre";

  private final CreditAccountRepository accountRepository;
  private final CreditTransactionRepository transactionRepository;
  private final CreditSettingRepository settingRepository;
  private final SubscriptionRepository subscriptionRepository;

  @Override
  @Transactional(readOnly = true)
  public int balance(UUID userId) {
    return accountRepository.findById(userId).map(CreditAccount::getBalance).orElse(0);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CreditTransactionResponse> transactions(UUID userId) {
    return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(CreditTransactionMapper::toResponse)
        .toList();
  }

  @Override
  public int apply(
      UUID userId,
      int amount,
      CreditTransactionType type,
      String description,
      BigDecimal pricePaid) {
    CreditAccount account =
        accountRepository
            .lockByUserId(userId)
            .orElseGet(
                () -> accountRepository.save(CreditAccount.builder().userId(userId).build()));
    int next = account.getBalance() + amount;
    if (next < 0) {
      throw new ApiException(
          ClubExceptionType.INSUFFICIENT_CREDITS,
          "Balance "
              + account.getBalance()
              + " is lower than the "
              + (-amount)
              + " credits required");
    }
    account.setBalance(next);
    account.setUpdatedAt(Instant.now());
    accountRepository.save(account);
    transactionRepository.save(
        CreditTransaction.builder()
            .userId(userId)
            .amount(amount)
            .type(type)
            .description(description)
            .pricePaid(pricePaid)
            .build());
    return next;
  }

  @Override
  public int purchase(UUID userId, int credits, BigDecimal price) {
    BigDecimal expected = PACKS.get(credits);
    if (expected == null || expected.compareTo(price) != 0) {
      throw new ApiException(
          ClubExceptionType.INVALID_PAYLOAD,
          "Unknown pack: allowed packs are 10 credits / 15 EUR and 25 credits / 35 EUR");
    }
    return apply(
        userId,
        credits,
        CreditTransactionType.CREDIT_PURCHASE,
        "Purchase of " + credits + " credits (" + price + " EUR)",
        price);
  }

  @Override
  @Transactional(readOnly = true)
  public int price(String category, DemandLevel demand) {
    CreditSetting setting =
        settingRepository.findAll().stream()
            .filter(s -> s.getId().equalsIgnoreCase(category.trim()))
            .findFirst()
            .or(() -> settingRepository.findById(FALLBACK_CATEGORY))
            .orElseThrow(
                () ->
                    new ApiException(
                        ClubExceptionType.CREDIT_SETTING_NOT_FOUND,
                        "No credit setting for " + category));
    return switch (demand) {
      case LOW -> setting.getCreditsLow();
      case NORMAL -> setting.getCreditsNormal();
      case HIGH -> setting.getCreditsHigh();
    };
  }

  @Override
  @Transactional(readOnly = true)
  public CreditStats stats() {
    List<Subscription> active = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);
    BigDecimal mrr =
        active.stream().map(Subscription::getMonthlyPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal purchases =
        transactionRepository.findByType(CreditTransactionType.CREDIT_PURCHASE).stream()
            .map(t -> t.getPricePaid() == null ? BigDecimal.ZERO : t.getPricePaid())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new CreditStats(active.size(), mrr, purchases, accountRepository.totalBalance());
  }
}
