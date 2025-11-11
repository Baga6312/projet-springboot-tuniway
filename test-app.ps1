# Test Script for Tuniway Spring Boot Application
# This script helps verify that the application is running correctly

Write-Host "=== Tuniway Application Test Script ===" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8081"
$healthEndpoint = "$baseUrl/api/health"
$loginPage = "$baseUrl/login.html"
$signupPage = "$baseUrl/signup.html"

# Test Health Endpoint
Write-Host "1. Testing Health Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri $healthEndpoint -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "   ✓ Health endpoint is working!" -ForegroundColor Green
        Write-Host "   Response: $($response.Content)" -ForegroundColor Gray
    }
} catch {
    Write-Host "   ✗ Health endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   Make sure the application is running on port 8081" -ForegroundColor Yellow
}

Write-Host ""

# Test Login Page
Write-Host "2. Testing Login Page..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri $loginPage -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "   ✓ Login page is accessible!" -ForegroundColor Green
    }
} catch {
    Write-Host "   ✗ Login page failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test Signup Page
Write-Host "3. Testing Signup Page..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri $signupPage -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "   ✓ Signup page is accessible!" -ForegroundColor Green
    }
} catch {
    Write-Host "   ✗ Signup page failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test Static Resources
Write-Host "4. Testing Static Resources..." -ForegroundColor Yellow

$staticResources = @(
    "$baseUrl/assets/css/style.css",
    "$baseUrl/assets/images/logo.png",
    "$baseUrl/assets/images/logsignback.jpg"
)

foreach ($resource in $staticResources) {
    try {
        $response = Invoke-WebRequest -Uri $resource -UseBasicParsing -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            $fileName = Split-Path $resource -Leaf
            Write-Host "   ✓ $fileName is accessible" -ForegroundColor Green
        }
    } catch {
        $fileName = Split-Path $resource -Leaf
        Write-Host "   ✗ $fileName failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Open your browser and go to: $loginPage" -ForegroundColor White
Write-Host "2. Check that the background image and styling are visible" -ForegroundColor White
Write-Host "3. Press F12 to open Developer Tools and check the Console tab for errors" -ForegroundColor White
Write-Host "4. Check the Network tab to verify all resources load successfully" -ForegroundColor White

