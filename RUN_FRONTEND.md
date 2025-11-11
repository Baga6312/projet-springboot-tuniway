# Running in Frontend-Only Mode

## Current Status

The application is configured to run without a database for frontend testing. However, there's a known issue:

**Services and controllers that depend on repositories will fail to initialize** because repositories are disabled when there's no database.

## Quick Fix Options

### Option 1: Temporary Workaround (Simplest)
Temporarily comment out the `@Service` and `@RestController` annotations in service and controller classes, or move them to a separate package that's excluded from component scanning.

### Option 2: Use Conditional Annotations (Recommended for Production)
Add `@ConditionalOnProperty(name = "spring.datasource.url")` to all service and controller classes to make them only load when database is configured.

### Option 3: Create Stub Implementations
Create empty/stub implementations of repositories that return empty results when database is not available.

## Current Configuration

The application.properties is set up to:
- Disable JPA repositories: `spring.data.jpa.repositories.enabled=false`
- Exclude DataSource auto-configuration
- Exclude JPA/Hibernate auto-configuration

## Next Steps

1. **Try running the application** - it might work if Spring Boot handles the missing beans gracefully
2. **If it fails**, we'll need to implement one of the options above
3. **For now**, the HealthController should work, and static resources should be served

## Test Command

```bash
.\mvnw.cmd spring-boot:run
```

If you see errors about missing beans for services/controllers, we'll need to implement the conditional loading approach.

