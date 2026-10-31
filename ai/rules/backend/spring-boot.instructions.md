---
description: "Spring Boot 4.x development - REST APIs, JPA, Security, Testing, and Cloud-native patterns. Use for building enterprise Java applications with Spring Boot."
applyTo: "microservices/**"
---

# Spring Boot Skill

Enterprise Spring Boot 4.x development with focus on clean architecture and production-ready code.

## Core Workflow

1. **Analyze** - Understand requirements, identify service boundaries, APIs, data models
2. **Design** - Plan architecture, confirm design before coding
3. **Implement** - Build with constructor injection and layered architecture
4. **Secure** - Add Spring Security, OAuth2, method security; verify tests pass
5. **Test** - Write unit, integration tests; run `./mvnw test` and confirm all pass
6. **Deploy** - Configure health checks via Actuator; validate `/actuator/health` returns UP

## Framework and Technology Stack

- Java 25 or later as the primary programming language
- Spring Boot 4.x as the main framework
- Spring Framework for dependency injection and core functionality
- Maven for dependency management and build processes
- JUnit 5 for unit and integration testing
- Spring Data JPA for database operations
- Spring Security for authentication and authorization

## Quick Start Templates

### Entity
```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @DecimalMin("0.0")
    private BigDecimal price;

    // Getters/Setters (no Lombok)
}
```

### Repository
```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
}
```

### Service
```java
@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> search(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Product create(ProductRequest request) {
        var product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        return repo.save(product);
    }
}
```

### REST Controller
```java
@RestController
@RequestMapping("/api/v1/products")
@Validated
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> search(@RequestParam(defaultValue = "") String name) {
        return service.search(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody ProductRequest request) {
        return service.create(request);
    }
}
```

### DTO (Record)
```java
public record ProductRequest(
    @NotBlank String name,
    @DecimalMin("0.0") BigDecimal price
) {}
```

### Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField,
                    error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid"));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(EntityNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }
}
```

### Test Slice
```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean ProductService service;

    @Test
    void createProduct_validRequest_returns201() throws Exception {
        var product = new Product();
        product.setName("Widget");
        when(service.create(any())).thenReturn(product);

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Widget","price":10.0}"""))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Widget"));
    }
}
```

## Reference Guide

Load detailed patterns based on context:

| Topic | Reference | When to Load |
|-------|-----------|-------------|
| Web/REST | `references/web.instructions.md` | Controllers, validation, exception handling |
| Data Access | `references/data.instructions.md` | JPA, repositories, transactions, queries |
| Security | `references/security.instructions.md` | Spring Security 6, OAuth2, JWT, auth |
| Cloud/Config | `references/cloud.instructions.md` | Config server, discovery, resilience |
| Testing | `references/testing.instructions.md` | Unit, integration, slice tests |
| Errors | `references/errors.instructions.md` | ErrorMessage, ApiException, exception type enums |

## Constraints

### MUST DO
- Constructor injection (no field injection)
- `@Valid` on all request bodies
- `@Transactional` for multi-step writes
- `@Transactional(readOnly = true)` for reads
- Type-safe config with `@ConfigurationProperties`
- Global exception handling with `@RestControllerAdvice`
- Externalize secrets (use env vars, not properties files)

### MUST NOT DO
- Field injection (`@Autowired` on fields)
- Skip input validation on endpoints
- Mix blocking and reactive code
- Store secrets in application.properties
- Use deprecated Spring Boot 2.x patterns
- Hardcode URLs, credentials, environment values

## Architecture Patterns

**Project Structure:**
```
src/
├── main/
│   ├── java/
│   │   └── com/company/project/
│   │       ├── Application.java              # Main Spring Boot application class
│   │       ├── config/                       # Configuration classes
│   │       ├── controllers/                  # REST controllers
│   │       ├── services/                     # Business logic services
│   │       ├── repositories/                 # Data access layer
│   │       ├── models/                       # Entity classes
│   │       ├── dtos/                         # Data Transfer Objects
│   │       ├── exceptions/                   # Custom exceptions
│   │       └── utils/                        # Utility classes
│   └── resources/
│       ├── application.yml                   # Main configuration
│       ├── application-dev.yml               # Development profile
│       ├── application-test.yml              # Test profile
│       └── application-prod.yml              # Production profile
└── test/
    └── java/
        └── com/company/project/
            ├── controllers/                  # Controller tests
            ├── services/                     # Service tests
            └── repositories/                 # Repository tests
```

**Layering:**
- Controller → Service → Repository
- Controller handles HTTP, validation
- Service handles business logic, transactions
- Repository handles data persistence

**Clean Architecture Principles:**
- Domain models independent of frameworks
- Use case driven design
- Dependency inversion (interfaces)
- Clear boundaries between layers

## Common Annotations

| Annotation | Purpose |
|------------|---------|
| `@RestController` | REST controller (combines @Controller + @ResponseBody) |
| `@Service` | Business logic component |
| `@Repository` | Data access component |
| `@Transactional` | Transaction management |
| `@Valid` | Trigger validation |
| `@ConfigurationProperties` | Bind properties to class |
| `@EnableMethodSecurity` | Enable method security |

## Reactive WebFlux Endpoint

```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<OrderDto>> getOrder(@PathVariable UUID id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }
}
```

## Spring Security JWT

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
```

## Naming Conventions

### Class and Interface Names

1. **Class Naming**
   - Use PascalCase for class names
   - Examples: `UserController`, `OrderService`, `UserRepository`
   - Use descriptive names that indicate purpose and responsibility

2. **Interface Naming**
   - Use PascalCase for interface names
   - Examples: `UserService`, `OrderRepository`
   - Avoid prefixing with "I" (e.g., use `UserService` not `IUserService`)

### Method and Variable Names

1. **Method Naming**
   - Use camelCase for method names
   - Use descriptive verbs that indicate action
   - Examples: `findUserById`, `createOrder`, `validateUserInput`

2. **Variable Naming**
   - Use camelCase for variable names
   - Use descriptive names that indicate purpose
   - Examples: `userRepository`, `orderService`, `isValid`

3. **Constants**
   - Use ALL_CAPS for constants
   - Use underscores to separate words
   - Examples: `MAX_RETRY_ATTEMPTS`, `DEFAULT_PAGE_SIZE`

## Knowledge Base

Spring Boot 4.x, Java 25, Spring WebFlux, Project Reactor, Spring Data JPA, Spring Security 7, OAuth2/JWT, Hibernate, R2DBC, Spring Cloud, Resilience4j, Micrometer, JUnit 5, TestContainers, Mockito, Maven/Gradle
