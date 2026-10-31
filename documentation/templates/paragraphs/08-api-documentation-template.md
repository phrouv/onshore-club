# API Documentation Template

## Purpose

Generate comprehensive API documentation with endpoints, examples, and specifications.

## Template Variables

- `{{ENDPOINT_DETAILS}}` - Complete endpoint specifications including URL, method, headers, parameters
- `{{EXAMPLE_REQUEST}}` - Sample request payload in JSON format
- `{{EXAMPLE_RESPONSE}}` - Sample response payload in JSON format

## Output Format

```markdown
## API Documentation & Reference

### Endpoint Specifications

{{ENDPOINT_DETAILS}}

### Request Example

```json
{{EXAMPLE_REQUEST}}
```

### Response Example

```json
{{EXAMPLE_RESPONSE}}
```

```

## Example Usage

```markdown
## API Documentation & Reference

### Endpoint Specifications

**Base URL**: `https://api.example.com/v1`

**Authentication**: Bearer Token (OAuth 2.0)

**Content-Type**: `application/json`

#### Payment Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/payments` | Initiate a new payment |
| GET | `/payments/{id}` | Retrieve payment status |
| PUT | `/payments/{id}/cancel` | Cancel a pending payment |
| GET | `/payments` | List payments with pagination |

#### POST /payments

**Headers:**
- `Authorization: Bearer {access_token}`
- `Content-Type: application/json`
- `X-Request-ID: {unique_request_id}` (optional)

**Request Parameters:**
- `orderId` (string, required): Unique order identifier
- `amount` (decimal, required): Payment amount (min: 0.01, max: 999999.99)
- `currency` (string, required): ISO 4217 currency code (USD, EUR, GBP)
- `customerId` (string, required): Customer identifier
- `paymentMethod` (string, required): Payment method type (credit_card, debit_card, bank_transfer)

**Response Codes:**
- `201 Created`: Payment successfully initiated
- `400 Bad Request`: Invalid request parameters
- `401 Unauthorized`: Missing or invalid authentication
- `422 Unprocessable Entity`: Business rule validation failed
- `500 Internal Server Error`: System error

### Request Example

```json
{
  "orderId": "ORD-12345-2025",
  "amount": 129.99,
  "currency": "USD",
  "customerId": "CUST-98765",
  "paymentMethod": "credit_card",
  "metadata": {
    "customerEmail": "customer@example.com",
    "orderDescription": "Premium subscription renewal"
  }
}
```

### Response Example

```json
{
  "paymentId": "PAY-ABC123-XYZ789",
  "status": "pending",
  "orderId": "ORD-12345-2025",
  "amount": 129.99,
  "currency": "USD",
  "createdAt": "2025-11-04T10:30:00Z",
  "expiresAt": "2025-11-04T11:00:00Z",
  "paymentUrl": "https://secure.payment.com/pay/PAY-ABC123-XYZ789",
  "metadata": {
    "processingFee": 3.90,
    "estimatedCompletionTime": "2025-11-04T10:32:00Z"
  }
}
```

#### Error Response Format

```json
{
  "error": {
    "code": "INVALID_AMOUNT",
    "message": "Payment amount must be between 0.01 and 999999.99",
    "details": {
      "field": "amount",
      "value": "0.00",
      "constraint": "min_value"
    },
    "timestamp": "2025-11-04T10:30:00Z",
    "requestId": "req-123456789"
  }
}
```

```

## Guidelines

- Document all endpoints with complete specifications
- Include authentication and authorization requirements
- Provide realistic example data that follows actual business rules
- Document all possible response codes and error scenarios
- Include rate limiting and pagination information where applicable
- Specify data validation rules and constraints
- Use consistent naming conventions across all endpoints
- Include versioning strategy and deprecation notices
- Document any async operations and callback mechanisms
- Provide Postman collections or OpenAPI specifications as supplements
