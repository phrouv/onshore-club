# Code Bootstrap Template

## Purpose

Generate sample code implementations to help developers start their work quickly.

## Template Variables

- `{{CONTROLLER_CODE}}` - Sample controller implementation with annotations
- `{{SERVICE_CODE}}` - Sample service implementation with business logic
- `{{CONFIGURATION_CODE}}` - Configuration classes and setup code

## Output Format

```markdown
## Code Bootstrap

### Controller Implementation

```java
{{CONTROLLER_CODE}}
```

### Service Implementation

```java
{{SERVICE_CODE}}
```

### Configuration

```java
{{CONFIGURATION_CODE}}
```

```

## Example Usage

```markdown
## Code Bootstrap

### Controller Implementation

```java
@RestController
@RequestMapping("/api/v1/payments")
@Validated
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> initiatePayment(
            @Valid @RequestBody PaymentRequestDTO request,
            @RequestHeader("X-Request-ID") Optional<String> requestId) {
        
        log.info("Initiating payment for order: {}", request.getOrderId());
        
        try {
            PaymentResponseDTO response = paymentService.processPayment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (PaymentValidationException e) {
            log.warn("Payment validation failed: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (PaymentProcessingException e) {
            log.error("Payment processing failed: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentStatus(@PathVariable String paymentId) {
        PaymentResponseDTO response = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFound(PaymentNotFoundException e) {
        ErrorResponse error = new ErrorResponse("PAYMENT_NOT_FOUND", e.getMessage());
        return ResponseEntity.notFound().build();
    }
}
```

### Service Implementation

```java
@Service
@Transactional
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentPopulator paymentPopulator;
    private final PaymentConverter paymentConverter;
    private final PSPClient pspClient;
    private final PaymentValidator paymentValidator;

    public PaymentService(PaymentRepository paymentRepository,
                         PaymentPopulator paymentPopulator,
                         PaymentConverter paymentConverter,
                         PSPClient pspClient,
                         PaymentValidator paymentValidator) {
        this.paymentRepository = paymentRepository;
        this.paymentPopulator = paymentPopulator;
        this.paymentConverter = paymentConverter;
        this.pspClient = pspClient;
        this.paymentValidator = paymentValidator;
    }

    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        // Validate request
        paymentValidator.validate(request);

        // Convert DTO to entity
        PaymentModel paymentModel = paymentPopulator.populate(request);
        paymentModel.setStatus(PaymentStatus.PENDING);

        // Save initial payment record
        PaymentModel savedPayment = paymentRepository.save(paymentModel);

        try {
            // Call external PSP
            PSPResponse pspResponse = pspClient.initiatePayment(paymentModel);
            
            // Update payment with PSP response
            savedPayment.setPspTransactionId(pspResponse.getTransactionId());
            savedPayment.setStatus(mapPSPStatus(pspResponse.getStatus()));
            savedPayment = paymentRepository.save(savedPayment);

            log.info("Payment processed successfully: {}", savedPayment.getId());
            
        } catch (PSPException e) {
            savedPayment.setStatus(PaymentStatus.FAILED);
            savedPayment.setErrorMessage(e.getMessage());
            paymentRepository.save(savedPayment);
            
            log.error("PSP processing failed for payment: {}", savedPayment.getId(), e);
            throw new PaymentProcessingException("Payment processing failed: " + e.getMessage());
        }

        return paymentConverter.convert(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentStatus(String paymentId) {
        PaymentModel payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));
        return paymentConverter.convert(payment);
    }

    private PaymentStatus mapPSPStatus(String pspStatus) {
        return switch (pspStatus.toUpperCase()) {
            case "SUCCESS" -> PaymentStatus.COMPLETED;
            case "PENDING" -> PaymentStatus.PROCESSING;
            case "FAILED" -> PaymentStatus.FAILED;
            default -> PaymentStatus.UNKNOWN;
        };
    }
}
```

### Configuration

```java
@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfiguration {

    @Bean
    @Primary
    public PaymentPopulator paymentPopulator() {
        return new PaymentPopulator();
    }

    @Bean
    public PaymentConverter paymentConverter() {
        return new PaymentConverter();
    }

    @Bean
    public PaymentValidator paymentValidator(PaymentProperties properties) {
        return new PaymentValidator(properties);
    }

    @Bean
    @ConditionalOnProperty(name = "payment.psp.enabled", havingValue = "true", matchIfMissing = true)
    public PSPClient pspClient(PaymentProperties properties) {
        return new PSPClient(properties.getPsp().getBaseUrl(), 
                           properties.getPsp().getApiKey(),
                           properties.getPsp().getTimeout());
    }

    @Bean
    @ConditionalOnProperty(name = "payment.psp.enabled", havingValue = "false")
    public PSPClient mockPspClient() {
        return new MockPSPClient();
    }
}

@ConfigurationProperties(prefix = "payment")
@Data
public class PaymentProperties {
    
    private PSPProperties psp = new PSPProperties();
    private ValidationProperties validation = new ValidationProperties();

    @Data
    public static class PSPProperties {
        private String baseUrl;
        private String apiKey;
        private Duration timeout = Duration.ofSeconds(30);
        private int maxRetries = 3;
    }

    @Data
    public static class ValidationProperties {
        private BigDecimal minAmount = new BigDecimal("0.01");
        private BigDecimal maxAmount = new BigDecimal("999999.99");
        private List<String> allowedCurrencies = List.of("USD", "EUR", "GBP");
    }
}
```

```

## Guidelines

- Provide complete, compilable code examples
- Include proper error handling and logging
- Use appropriate design patterns (dependency injection, strategy pattern, etc.)
- Include validation and security considerations
- Add comprehensive comments for complex business logic
- Follow coding standards and best practices
- Include configuration for different environments (dev, staging, production)
- Provide both positive and negative test scenarios
- Include performance optimizations where relevant
- Use type-safe configuration properties
