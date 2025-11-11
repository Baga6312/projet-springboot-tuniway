# Quick Start Guide - Tuniway Spring Boot App

## 🚀 Quick Start (3 Steps)

### Step 1: Start the Application
Open PowerShell or Command Prompt in the project directory and run:

**Option A: Using Maven Wrapper (Recommended - no Maven installation needed):**
```bash
.\mvnw.cmd spring-boot:run
```

**Option B: Using Maven (if Maven is installed):**
```bash
mvn spring-boot:run
```

**Wait for this message:**
```
Started TuniwayApplication in X.XXX seconds
```

### Step 2: Verify Application is Running
Open your browser and go to:
- **Health Check:** http://localhost:8081/api/health
- **Login Page:** http://localhost:8081/login.html
- **Signup Page:** http://localhost:8081/signup.html

You should see:
- Health endpoint returns: `{"status":"UP","message":"Tuniway application is running"}`
- Login/Signup pages display with background image and styling

### Step 3: Check Browser Console
1. Press **F12** to open Developer Tools
2. Go to **Console** tab - should have no errors
3. Go to **Network** tab - refresh page
4. Check that all resources load (status 200):
   - `/assets/css/style.css`
   - `/assets/images/logo.png`
   - `/assets/images/logsignback.jpg`

## ✅ Success Indicators

- ✅ Application starts without errors
- ✅ Can access http://localhost:8081/api/health
- ✅ Login page shows background image
- ✅ Signup page shows background image
- ✅ White card with rounded corners is visible
- ✅ Logo displays correctly
- ✅ Form fields are styled
- ✅ No 404 errors in browser console
- ✅ No CSS/image loading errors

## ❌ Common Problems & Solutions

### Problem 1: Port 8081 already in use
**Solution:** The application uses port 8081 by default. If it's already in use, change it in `application.properties`:
```properties
server.port=8082
```
Then access at: http://localhost:8082 (or your chosen port)

### Problem 2: Database connection error
**✅ FIXED!** The database configuration is now commented out by default for frontend testing.

**To use database later:** Uncomment the database properties in `application.properties` when MySQL is ready.

See `FRONTEND_ONLY_SETUP.md` for details.

### Problem 3: Static resources not loading
**Check:**
1. Files are in `src/main/resources/static/` folder
2. Browser cache - Press `Ctrl + Shift + R` to hard refresh
3. Access pages through Spring Boot (not by opening HTML files directly)

### Problem 4: Background image not showing
**Check:**
1. File exists: `src/main/resources/static/assets/images/logsignback.jpg`
2. Browser Network tab shows image loads (status 200)
3. Check browser console for CORS or path errors

## 🧪 Test Commands

### Test Health Endpoint (PowerShell):
```powershell
Invoke-WebRequest -Uri http://localhost:8081/api/health | Select-Object -ExpandProperty Content
```

### Test Health Endpoint (curl):
```bash
curl http://localhost:8081/api/health
```

### Test Static Resources:
- CSS: http://localhost:8081/assets/css/style.css
- Logo: http://localhost:8081/assets/images/logo.png
- Background: http://localhost:8081/assets/images/logsignback.jpg

## 📝 Next Steps

Once the application is running:
1. ✅ Verify all pages load correctly
2. ✅ Test form functionality (login/signup)
3. ✅ Connect forms to API endpoints
4. ✅ Test database operations (if database is connected)

## 🆘 Still Having Issues?

1. **Check logs** - Look at console output for error messages
2. **Verify Java version** - Run `java -version` (should be 17+)
3. **Verify Maven** - Run `mvn -version`
4. **Check file structure** - Verify files are in correct locations
5. **Clear browser cache** - Hard refresh (Ctrl + Shift + R)

---

**Need more help?** Check `HOW_TO_RUN.md` for detailed instructions.

