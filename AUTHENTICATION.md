# JWT Authentication Implementation

## Overview
This document describes the JWT-based authentication system added to the AI Data Analysis project.

## Backend (Spring Boot)

### Dependencies Added
- Spring Security
- JWT (JJWT)
- BCrypt for password encryption

### Key Components

#### 1. User Entity (`model/User.java`)
- Stores user information with roles (USER, ADMIN)
- Passwords are encrypted using BCrypt
- Fields: id, username, email, password, role, active

#### 2. Authentication DTOs
- `SignUpRequest`: Contains username, email, password, confirmPassword
- `LoginRequest`: Contains username, password
- `AuthResponse`: Returns token, username, email, role, success flag, message

#### 3. JWT Utility (`util/JwtUtil.java`)
- Generates JWT tokens with user claims
- Validates tokens
- Extracts claims (username, email, role)
- Token expiration: 24 hours (configurable via `jwt.expiration`)

#### 4. JWT Filter (`config/JwtFilter.java`)
- Intercepts all requests
- Extracts JWT token from Authorization header
- Validates token and sets authentication in SecurityContext
- Adds ROLE_ prefix to roles for Spring Security

#### 5. Security Configuration (`config/SecurityConfig.java`)
- Enables CORS for frontend development (localhost:5173, localhost:3000)
- Disables CSRF for stateless JWT authentication
- Permits `/api/auth/signup` and `/api/auth/login` without authentication
- Requires authentication for all other endpoints
- Uses stateless session management

#### 6. Authentication Service (`service/AuthService.java`)
- Handles user signup with validation
- Handles user login with BCrypt password verification
- Creates JWT tokens on successful authentication

#### 7. Authentication Controller (`controller/AuthController.java`)
- `POST /api/auth/signup`: Register new user
- `POST /api/auth/login`: Login existing user
- `GET /api/auth/me`: Get current user info (requires authentication)

### Configuration (`application.properties`)

```properties
jwt.secret=my-super-secret-key-that-is-at-least-32-characters-long-for-HS256
jwt.expiration=86400000  # 24 hours in milliseconds
```

**Important**: Change the `jwt.secret` to a secure value in production.

### API Endpoints

#### Sign Up
```bash
POST /api/auth/signup
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "secure_password",
  "confirmPassword": "secure_password"
}

Response (201 Created):
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "success": true,
  "message": "User registered successfully"
}
```

#### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "secure_password"
}

Response (200 OK):
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER",
  "success": true,
  "message": "Login successful"
}
```

#### Accessing Protected Endpoints
```bash
GET /api/auth/me
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## Frontend (React)

### Dependencies Added
- react-router-dom: For routing

### Key Components

#### 1. Auth Context (`context/AuthContext.jsx`)
- Manages global authentication state
- Provides `useAuth` hook for components
- Stores token and user info in localStorage
- Handles login/logout operations
- Automatically restores session on page reload

#### 2. Login Component (`components/Login.jsx`)
- Form for user login
- Validates input and displays errors
- Stores JWT token on successful login
- Redirects to dashboard

#### 3. Signup Component (`components/Signup.jsx`)
- Form for user registration
- Password confirmation validation
- Handles signup errors gracefully
- Auto-logs in user after successful signup

#### 4. Dashboard Component (`components/Dashboard.jsx`)
- Main app interface after authentication
- Displays welcome message with username
- Logout button
- Renders Upload and Chat components

#### 5. Protected Route (`components/ProtectedRoute.jsx`)
- HOC for protecting routes
- Redirects unauthenticated users to login

#### 6. API Service (`service/api.js`)
- Axios instance with JWT interceptor
- Automatically attaches token to all requests
- Handles 401 responses by clearing auth and redirecting to login
- Centralized API configuration

#### 7. Styling
- `Auth.css`: Styles for login/signup pages
- `Dashboard.css`: Styles for authenticated dashboard

### Routing Structure

| Route | Component | Protection |
|-------|-----------|------------|
| `/` | Redirects to `/dashboard` or `/login` | - |
| `/login` | Login page | Not authenticated only |
| `/signup` | Signup page | Not authenticated only |
| `/dashboard` | Dashboard (main app) | Authenticated only |
| `/*` | Redirects to `/` | - |

### Authentication Flow

1. User visits `/` → Redirected to `/login` (if not authenticated)
2. User enters credentials and clicks "Login"
3. Frontend sends POST request to `/api/auth/login`
4. Backend validates credentials and returns JWT token
5. Frontend stores token in localStorage and user context
6. Frontend redirects to `/dashboard`
7. All subsequent API requests include token in Authorization header
8. If token expires, user is redirected to login on next request

---

## Setup Instructions

### Backend

1. **Install dependencies** (Maven):
```bash
cd backend
mvn clean install
```

2. **Update JWT secret** in `application.properties`:
```properties
jwt.secret=<your-secure-secret-key>
```

3. **Run the application**:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend

1. **Install dependencies**:
```bash
cd frontend
npm install
```

2. **Run the development server**:
```bash
npm run dev
```

The frontend will start on `http://localhost:5173`

3. **Build for production**:
```bash
npm run build
```

---

## Security Considerations

### Implemented Security Measures
✅ JWT tokens with expiration (24 hours)
✅ BCrypt password hashing (10 rounds)
✅ CORS configured for development
✅ CSRF protection disabled (stateless JWT)
✅ Secure password storage (never stored in plain text)
✅ Token validation on every request
✅ Automatic logout on token expiration

### Production Recommendations
1. Use HTTPS only
2. Change `jwt.secret` to a strong, random value
3. Adjust `jwt.expiration` based on security requirements
4. Implement refresh tokens for long-lived sessions
5. Add rate limiting to prevent brute force attacks
6. Store sensitive data (tokens) securely
7. Implement API logging and monitoring
8. Use secure cookies with httpOnly and secure flags
9. Add input validation and sanitization
10. Regular security audits and dependency updates

---

## Testing the Authentication

### Using cURL or Postman

1. **Sign Up**:
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123","confirmPassword":"password123"}'
```

2. **Login**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

3. **Access Protected Endpoint**:
```bash
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/auth/me
```

---

## Future Enhancements

- [ ] Email verification
- [ ] Password reset functionality
- [ ] Refresh token mechanism
- [ ] Role-based access control (RBAC) middleware
- [ ] User profile management
- [ ] Two-factor authentication (2FA)
- [ ] Social login (Google, GitHub, etc.)
- [ ] Session management and device tracking
- [ ] Audit logs for security events

---

## Troubleshooting

### Common Issues

**Issue**: CORS errors in browser console
- **Solution**: Verify CORS configuration in `SecurityConfig.java` matches your frontend URL

**Issue**: 401 Unauthorized on protected endpoints
- **Solution**: Ensure token is correctly stored and sent in Authorization header

**Issue**: Token not being sent with requests
- **Solution**: Check that API interceptor in `service/api.js` is correctly configured

**Issue**: "Invalid token" error
- **Solution**: Token may have expired or secret key doesn't match backend configuration

---

## Version Information

- Spring Boot: 4.0.5
- Java: 17
- React: 19.2.4
- JWT (JJWT): 0.12.5
- BCrypt: Included in Spring Security
