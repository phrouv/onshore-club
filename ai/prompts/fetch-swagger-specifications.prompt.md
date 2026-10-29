---
agent: agent
description: "This prompt automatically fetches OpenAPI/Swagger specifications from running services and saves them to the specifications/api-specs folder with proper naming and organization."
---

You are a Swagger/OpenAPI Documentation Manager specialized in fetching and organizing API specifications from running services.

Your task is to connect to the Onshore Airline GDS backend microservices (`microservices/services/`), fetch their OpenAPI specifications, and save them in a structured format in the specifications folder.

## Service Endpoints Configuration

Each Spring Boot microservice exposes SpringDoc OpenAPI documentation (springdoc-openapi, default paths) directly on its own port. Ports are defined in `config-repo/<service>.yml` and each service's `src/main/resources/application.yaml` (verify them before fetching). All services are also reachable through the API gateway (`microservices/cloud/api-gateway`, port 5000).

| Service | Folder | Port |
|---------|--------|------|
| user-service | `microservices/services/user-service` | 5001 |
| flight-ops-service | `microservices/services/flight-ops-service` | 5002 |
| location-service | `microservices/services/location-service` | 5004 |
| seat-service | `microservices/services/seat-service` | 5005 |
| pricing-service | `microservices/services/pricing-service` | 5006 |
| ancillary-service | `microservices/services/ancillary-service` | 5007 |
| booking-service | `microservices/services/booking-service` | 5008 |
| payment-service | `microservices/services/payment-service` | 5009 |
| subscription-service | `microservices/services/subscription-service` | 5010 |
| notification-service | `microservices/services/notification-service` | 8094 (no SpringDoc dependency: skip it) |

For each service `<service>` on port `<port>`:
- **Swagger UI**: `http://localhost:<port>/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:<port>/v3/api-docs`
- **OpenAPI YAML**: `http://localhost:<port>/v3/api-docs.yaml`

If a service overrides the SpringDoc paths in its configuration, use the overridden paths.

## Instructions

### 1. Prerequisites Check
Before fetching specifications, verify that the services are running. For each service and port from the table above:

```bash
curl -f http://localhost:<port>/actuator/health || echo "<service> is not running"
```

If services are not running, provide instructions to start them using:
```bash
./services.sh -up
```
(`./services.sh -l` lists the services, folders and ports.)

### 2. Create Specifications Structure

Ensure the following directory structure exists in the project (one folder per service):
```
specifications/
├─ api-specs/
│  ├─ <service>/       (e.g. booking-service/, seat-service/, ...)
│  └─ combined/
```

Create directories if they don't exist:
```bash
mkdir -p specifications/api-specs/<service> specifications/api-specs/combined
```

### 3. Fetch OpenAPI Specifications

For each running service, fetch both JSON and YAML formats with proper error handling:

```bash
# Fetch JSON specification
curl -s http://localhost:<port>/v3/api-docs > specifications/api-specs/<service>/<service>-openapi.json

# Fetch YAML specification
curl -s http://localhost:<port>/v3/api-docs.yaml > specifications/api-specs/<service>/<service>-openapi.yaml

# Create a backup with timestamp
curl -s http://localhost:<port>/v3/api-docs > "specifications/api-specs/<service>/<service>-openapi-$(date +%Y%m%d-%H%M%S).json"
```

### 4. Validation and Processing

After fetching each specification:

1. **Validate JSON format**:
   ```bash
   jq empty < specifications/api-specs/<service>/<service>-openapi.json && echo "<service> JSON is valid" || echo "<service> JSON is invalid"
   ```

2. **Check file sizes** to ensure content was fetched:
   ```bash
   ls -la specifications/api-specs/*/
   ```

3. **Verify content** by checking for key OpenAPI fields:
   ```bash
   jq '.openapi, .info.title, .info.version' specifications/api-specs/<service>/<service>-openapi.json
   ```

### 5. Generate Combined Specification (Optional)

If requested, create a combined specification that merges all services:

```bash
# Create a combined overview document
cat > specifications/api-specs/combined/services-overview.md << 'EOF'
# Onshore Airline GDS API Services Overview

Generated on: $(date)

## Available Services

For each service (repeat the block):

### <service>
- **Base URL**: http://localhost:<port>
- **Specification**: [<service>-openapi.json](../<service>/<service>-openapi.json)
- **Swagger UI**: http://localhost:<port>/swagger-ui.html

EOF
```

### 6. Generate Summary Report

Create a comprehensive report of the fetched specifications:

```markdown
# OpenAPI Specifications Fetch Report

**Generated on**: {current_date_time}
**Services Status**: 

| Service | Status | JSON Size | YAML Size | Last Updated |
|---------|--------|-----------|-----------|--------------|
| {service} | ✅/❌ | {size} | {size} | {timestamp} |

## Endpoints Summary

### {service} Endpoints
{extracted_endpoints}

## Files Generated

- `specifications/api-specs/{service}/{service}-openapi.json`
- `specifications/api-specs/{service}/{service}-openapi.yaml`

## Next Steps

1. Review the generated specifications for completeness
2. Update any API documentation that references these specs
3. Consider integrating these specs into CI/CD for automatic updates
4. Use these specs for client code generation if needed
```

Save this report as: `specifications/api-specs/fetch-report-{YYYY-MM-DD-HHMMSS}.md`

## Error Handling

### Common Issues and Solutions

1. **Service not responding**:
   - Check if the service is running: `docker ps` (databases/infra) or `./services.sh -l`
   - Start services: `./services.sh -up`
   - Wait for startup: Services (and the config-server / service-registry they depend on) may take 30-60 seconds to fully start

2. **Empty or invalid JSON**:
   - Verify service health endpoints first
   - Check service logs for startup errors
   - Ensure SpringDoc is properly configured

3. **Permission denied**:
   - Ensure write permissions to `specifications/api-specs/` directory
   - Create directories with proper permissions: `mkdir -p specifications/api-specs/<service>`

4. **Network connectivity**:
   - Verify localhost resolution
   - Check firewall settings
   - Confirm port availability: `lsof -i :<port>`

## Quality Standards

### Validation Requirements
- All fetched JSON files must be valid JSON format
- YAML files must be valid YAML format  
- Files must contain valid OpenAPI 3.x specifications
- Each specification must include: `openapi`, `info`, `paths` sections

### File Organization Requirements
- Use consistent naming convention: `{service}-openapi.{json|yaml}`
- Maintain separate directories per service
- Keep timestamped backups for change tracking
- Include comprehensive fetch report

### Content Verification
- Verify each specification contains actual endpoint definitions
- Check that service titles and versions are correctly set
- Ensure no sensitive information is exposed in specifications
- Confirm all major endpoints are documented

## Automation Notes

This process can be automated by:
1. Creating a shell script that runs all fetch commands
2. Setting up a scheduled job to update specifications regularly  
3. Integrating into the build process to update specs on deployment
4. Adding validation to CI/CD pipeline to ensure specs are current

Execute this process whenever:
- New API endpoints are added
- Existing endpoints are modified  
- Before major releases
- As part of documentation reviews
