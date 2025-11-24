# JWT Authentication Testing Guide

## 1. JWT_SECRET Configuration

### For Development/Testing:
You can use **any string** you want, but it's better to use a secure random key.

**Option 1: Use the generated secure key (recommended)**
Add this to your `.env.local` file:
```
JWT_SECRET=bb8dd3a5b5c9312a36de559edfab114b464af5c1343c93ee643ca0ed15e1b3bd
```

**Option 2: Use any string (for quick testing)**
```
JWT_SECRET=my-test-secret-key-123
```

**Note:** For production, always use a strong, randomly generated secret key (at least 32 characters).

---

## 2. Setting Up the Application

### Step 1: Create/Update `.env.local` file
Create a file named `.env.local` in the project root and add:
```env
JWT_SECRET=bb8dd3a5b5c9312a36de559edfab114b464af5c1343c93ee643ca0ed15e1b3bd
```

### Step 2: Install Dependencies (if not done)
```bash
composer install
```

### Step 3: Start the Symfony Server

**Option A: Using Symfony CLI (recommended)**
```bash
symfony serve
```
The server will start on `http://127.0.0.1:8000`

**Option B: Using PHP Built-in Server**
```bash
php -S localhost:8000 -t public/
```

---

## 3. Testing with Postman

### Test 1: Register a New User

1. **Open Postman** and create a new request
2. **Method:** `POST`
3. **URL:** `http://localhost:8000/api/register`
4. **Headers:**
   - `Content-Type: application/json`
5. **Body:** Select `raw` → `JSON` and paste:
```json
{
    "email": "test@example.com",
    "username": "testuser",
    "plainPassword": "password123",
    "phoneNumber": "+1234567890"
}
```
6. **Click Send**

**Expected Response (201 Created):**
```json
{
    "success": true,
    "message": "User registered successfully",
    "token": "eyJ0eXAiOiJKV1QiLCJhbGc...",
    "user": {
        "id": 1,
        "email": "test@example.com",
        "username": "testuser",
        "roles": ["ROLE_USER"]
    }
}
```

**⚠️ IMPORTANT:** Copy the `token` value from the response - you'll need it for protected endpoints!

---

### Test 2: Login with Existing User

1. **Create a new request** in Postman
2. **Method:** `POST`
3. **URL:** `http://localhost:8000/api/login`
4. **Headers:**
   - `Content-Type: application/json`
5. **Body:** Select `raw` → `JSON` and paste:
```json
{
    "email": "henry.client@test.com",
    "password": "password123"
}
```
6. **Click Send**

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Authentication successful",
    "token": "eyJ0eXAiOiJKV1QiLCJhbGc...",
    "user": {
        "id": 1,
        "email": "test@example.com",
        "username": "testuser",
        "roles": ["ROLE_USER"]
    }
}
```

**⚠️ IMPORTANT:** Copy the `token` value from the response!

---

### Test 3: Access Protected API Endpoint

Now test accessing a protected endpoint using the JWT token:

1. **Create a new request** in Postman
2. **Method:** `GET` (or whatever method your API endpoint uses)
3. **URL:** `http://localhost:8000/api/places` (or any protected `/api/*` endpoint)
4. **Headers:**
   - `Authorization: Bearer YOUR_TOKEN_HERE`
   - `Content-Type: application/json`
   
   **Example:**
   ```
   Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGc...
   ```

5. **Click Send**

**Expected Response (200 OK):**
You should get the protected resource data.

**If token is missing/invalid (401 Unauthorized):**
```json
{
    "success": false,
    "message": "No bearer token provided"
}
```

---

## 4. Postman Collection Setup (Optional but Recommended)

### Create an Environment Variable in Postman:

1. Click the **eye icon** (👁️) in the top right → **Environments** → **+ Add**
2. Name it: `TuniWay Local`
3. Add variables:
   - `base_url`: `http://localhost:8000`
   - `jwt_token`: (leave empty, will be set automatically)
4. Click **Save**

### Create a Pre-request Script (for auto-token extraction):

1. In your **Login** or **Register** request, go to **Tests** tab
2. Add this script:
```javascript
if (pm.response.code === 200 || pm.response.code === 201) {
    var jsonData = pm.response.json();
    if (jsonData.token) {
        pm.environment.set("jwt_token", jsonData.token);
        console.log("Token saved to environment");
    }
}
```

3. In your **protected API requests**, set the Authorization header to:
   ```
   Bearer {{jwt_token}}
   ```

This way, after you login/register, the token will automatically be saved and used in subsequent requests!

---

## 5. Common Issues & Solutions

### Issue: "Invalid JSON payload"
**Solution:** Make sure `Content-Type: application/json` header is set

### Issue: "No bearer token provided"
**Solution:** 
- Check that you're using `Authorization: Bearer TOKEN` (with a space after "Bearer")
- Make sure you copied the full token (it's a long string)

### Issue: "Invalid or expired token"
**Solution:**
- Token might be expired (default: 1 hour)
- Token might be invalid - try logging in again to get a new token

### Issue: "User not found"
**Solution:**
- Make sure the email in the token matches a user in your database
- Try registering/login again

### Issue: Server not responding
**Solution:**
- Make sure the Symfony server is running
- Check the URL (should be `http://localhost:8000` or `http://127.0.0.1:8000`)
- Check for errors in the terminal where the server is running

---

## 6. Quick Test Checklist

- [ ] `.env.local` file created with `JWT_SECRET`
- [ ] Symfony server running (`symfony serve` or `php -S localhost:8000 -t public/`)
- [ ] Test registration endpoint - get token
- [ ] Test login endpoint - get token
- [ ] Test protected endpoint with token in Authorization header
- [ ] Verify token works for multiple requests

---

## 7. Example Postman Collection Structure

```
TuniWay API
├── Auth
│   ├── Register User
│   └── Login User
├── Places (Protected)
│   ├── Get All Places
│   └── Get Place by ID
├── Reservations (Protected)
│   └── Create Reservation
└── Reviews (Protected)
    └── Create Review
```

All protected endpoints should use: `Authorization: Bearer {{jwt_token}}`

---

**Happy Testing! 🚀**

