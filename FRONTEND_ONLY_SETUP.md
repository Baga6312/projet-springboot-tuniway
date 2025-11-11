# Frontend-Only Setup (No Database Required)

This guide explains how to run the application **without MySQL** to test the frontend pages (login, signup, etc.).

## ✅ What I've Done

I've modified the application to allow running without a database:

1. **Commented out database configuration** in `application.properties`
2. **Made DatabaseConfig conditional** - it only loads when database properties are present
3. **Application can now start** even without MySQL

## 🚀 How to Run (Frontend Only)

### Step 1: Start the Application
```bash
.\mvnw.cmd spring-boot:run
```

The application should now start successfully without MySQL!

### Step 2: Access the Pages
- **Login:** http://localhost:8081/login.html
- **Signup:** http://localhost:8081/signup.html
- **Home:** http://localhost:8081/
- **Health Check:** http://localhost:8081/api/health

### Step 3: Verify Static Resources
- **CSS:** http://localhost:8081/assets/css/style.css
- **Logo:** http://localhost:8081/assets/images/logo.png
- **Background:** http://localhost:8081/assets/images/logsignback.jpg

## ⚠️ Important Notes

### What Works:
- ✅ Static HTML pages (login, signup, home)
- ✅ CSS styling
- ✅ Images and assets
- ✅ Frontend JavaScript
- ✅ Health check endpoint

### What Doesn't Work:
- ❌ API endpoints that require database (they will return errors)
- ❌ User registration/login functionality (needs database)
- ❌ Any database operations

## 🔄 To Enable Database (When Ready)

When you want to use the database:

1. **Start MySQL** and create the database:
   ```sql
   CREATE DATABASE tuniway_db;
   ```

2. **Uncomment database properties** in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://127.0.0.1:3306/tuniway_db
   spring.datasource.username=root
   spring.datasource.password=password
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
   spring.jpa.properties.hibernate.format_sql=true
   ```

3. **Restart the application**

## 🧪 Testing the Frontend

1. **Start the app:** `.\mvnw.cmd spring-boot:run`
2. **Open browser:** http://localhost:8081/login.html
3. **Check:**
   - Background image displays
   - White card with rounded corners
   - Logo displays
   - Form fields are styled
   - Buttons work (visual feedback)
   - No console errors (F12 → Console tab)
   - All resources load (F12 → Network tab)

## 🆘 Troubleshooting

### Application still won't start?
- Make sure you've saved `application.properties` after commenting out database config
- Check that `DatabaseConfig.java` has `@ConditionalOnProperty` annotation
- Look for other errors in the console

### Pages load but no styling?
- Check browser console (F12) for 404 errors
- Verify files are in `src/main/resources/static/`
- Hard refresh: `Ctrl + Shift + R`

### Background image not showing?
- Check Network tab in browser DevTools
- Verify image exists: `src/main/resources/static/assets/images/logsignback.jpg`
- Check browser console for CORS or path errors

---

**Next Steps:** Once frontend is working, you can connect it to the API endpoints by enabling the database.

