# How to Run and Verify Spring Boot Application

## Prerequisites

1. **Java 17** - Verify installation:
   ```bash
   java -version
   ```

2. **Maven** - Verify installation:
   ```bash
   mvn -version
   ```

3. **MySQL Database** (Optional for testing static resources):
   - Make sure MySQL is running
   - Database `tuniway_db` should exist
   - Or configure the app to skip database initialization for testing

## Step 1: Start the Application

### Option A: Using Maven Wrapper (Recommended - no Maven installation needed)
**On Windows:**
```bash
.\mvnw.cmd spring-boot:run
```

**On Linux/Mac:**
```bash
./mvnw spring-boot:run
```

### Option B: Using Maven (if Maven is installed)
```bash
mvn spring-boot:run
```

### Option C: Build and Run JAR
```bash
mvn clean package
java -jar target/tuniway-0.0.1-SNAPSHOT.jar
```

## Step 2: Verify Application Started Successfully

### Check Console Output
You should see messages like:
```
Started TuniwayApplication in X.XXX seconds
Tomcat started on port(s): 8081 (http)
```

### Common Issues:

1. **Port 8081 already in use:**
   - Change port in `application.properties`:
     ```properties
     server.port=8082
     ```

2. **Database connection error:**
   - Make sure MySQL is running
   - Verify database credentials in `application.properties`
   - Or temporarily disable database requirement (see below)

3. **Compilation errors:**
   - Run `mvn clean install` first
   - Check for missing dependencies

## Step 3: Test Static Resources (Frontend)

### Access the Pages:
1. **Home Page:**
   ```
   http://localhost:8081/
   http://localhost:8081/index.html
   ```

2. **Login Page:**
   ```
   http://localhost:8081/login.html
   ```

3. **Signup Page:**
   ```
   http://localhost:8081/signup.html
   ```

4. **Admin Dashboard:**
   ```
   http://localhost:8081/admin/dashboard.html
   ```

### Test Static Assets:
- **CSS:** http://localhost:8081/assets/css/style.css
- **Logo:** http://localhost:8081/assets/images/logo.png
- **Background:** http://localhost:8081/assets/images/logsignback.jpg

### Verify in Browser:
1. Open browser Developer Tools (F12)
2. Go to **Network** tab
3. Refresh the page
4. Check that all resources load with status **200 OK**
5. If any resource shows **404**, check the file path

## Step 4: Test API Endpoints

### Using Browser:
```
http://localhost:8081/api/users
http://localhost:8081/api/places
http://localhost:8081/api/reservations
```

### Using curl (Command Line):
```bash
curl http://localhost:8081/api/users
curl http://localhost:8081/api/places
```

### Using PowerShell (Windows):
```powershell
Invoke-WebRequest -Uri http://localhost:8081/api/users
Invoke-WebRequest -Uri http://localhost:8081/api/places
```

## Step 5: Check Application Logs

The console will show:
- Application startup messages
- Database connection status
- API request logs (if enabled)
- Any errors or warnings

## Troubleshooting

### Problem: Static resources not loading

**Solution 1: Check file paths**
- Files should be in `src/main/resources/static/`
- Access them at root URL: `/filename.html`
- Not at `/static/filename.html`

**Solution 2: Clear browser cache**
- Press `Ctrl + Shift + R` (Windows/Linux)
- Press `Cmd + Shift + R` (Mac)

**Solution 3: Check Spring Boot is serving static resources**
- Verify `spring-boot-starter-web` is in `pom.xml`
- Check that files are in `src/main/resources/static/`

### Problem: Database connection errors

**Temporary Solution (for testing frontend only):**
Add to `application.properties`:
```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
```

**Note:** This disables database features. Remove when you need database functionality.

### Problem: Port already in use

**Solution:** Change port in `application.properties`:
```properties
server.port=8081
```

Then access at: `http://localhost:8081`

## Quick Verification Checklist

- [ ] Application starts without errors
- [ ] Can access http://localhost:8081
- [ ] Login page loads: http://localhost:8081/login.html
- [ ] Signup page loads: http://localhost:8081/signup.html
- [ ] Background image loads (check Network tab)
- [ ] CSS file loads (check Network tab)
- [ ] Logo image loads (check Network tab)
- [ ] No 404 errors in browser console
- [ ] API endpoints respond (if database is connected)

## Next Steps

1. **If static resources work:** Connect frontend forms to API endpoints
2. **If API endpoints work:** Test database operations
3. **If everything works:** Start developing features!

