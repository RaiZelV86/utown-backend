# UTOWN Backend

UTOWN Food Delivery & Services Platform - Backend API

## Technologies

- Java 21
- Spring Boot 3.5.5
- Spring Security with JWT
- Spring Data JPA
- MySQL 8
- Swagger/OpenAPI 3
- Lombok
- Maven

## Features

- JWT Authentication (Access + Refresh tokens)
- Password Reset Flow
- Role-based Authorization (CLIENT, ADMIN)
- CRUD API for Users and Restaurants
- Global Exception Handling
- CORS Configuration
- API Documentation with Swagger UI

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+

## Setup

### 1. Clone the repository

```bash
git clone <repository-url>
cd utown-backend
```

### 2. Configure Environment Variables

Copy the example environment file:

```bash
cp .env.example .env
```

Edit `.env` and set your actual values:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=uTown
DB_USERNAME=root
DB_PASSWORD=your_actual_password

# JWT Configuration
JWT_SECRET=your_super_secret_jwt_key_min_256_bits_for_HS256_algorithm
JWT_ACCESS_TOKEN_EXPIRATION=900000
JWT_REFRESH_TOKEN_EXPIRATION=604800000

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173

# Server Configuration
SERVER_PORT=8080
```

**IMPORTANT:** Never commit `.env` file to git. It contains sensitive credentials.

### 3. Load Environment Variables

#### On Linux/macOS:

```bash
export $(cat .env | xargs)
```

#### On Windows (PowerShell):

```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^=]+)=(.*)$') {
        [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2])
    }
}
```

#### Alternative: Use IDE Environment Variables

In IntelliJ IDEA:
1. Run > Edit Configurations
2. Select your Spring Boot application
3. Add environment variables from `.env` file

### 4. Create Database

```sql
CREATE DATABASE uTown;
```

Or let the application create it automatically (configured in application.properties).

### 5. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

Or run from IDE (IntelliJ IDEA, Eclipse, etc.)

## API Documentation

Once the application is running, access Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

## Authentication Flow

### 1. Register

```bash
POST /api/auth/register
Content-Type: application/json

{
  "phoneNumber": "+996700123456",
  "password": "securePassword123",
  "name": "John Doe"
}
```

### 2. Login

```bash
POST /api/auth/login
Content-Type: application/json

{
  "phoneNumber": "+996700123456",
  "password": "securePassword123"
}
```

Response:
```json
{
  "userId": 1,
  "phoneNumber": "+996700123456",
  "name": "John Doe",
  "role": "CLIENT",
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. Use Access Token

Add the access token to Authorization header:

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 4. Refresh Token

```bash
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 5. Logout

```bash
POST /api/auth/logout
Authorization: Bearer <access_token>
```

## Password Reset Flow

### 1. Request Reset Code

```bash
POST /api/auth/password/reset/request
Content-Type: application/json

{
  "username": "+996700123456"
}
```

Response: Returns a 4-digit code (currently hardcoded as "1234" for development)

### 2. Verify Code

```bash
POST /api/auth/password/reset/verify
Content-Type: application/json

{
  "username": "+996700123456",
  "code": "1234"
}
```

Response: Returns a reset token

### 3. Confirm New Password

```bash
POST /api/auth/password/reset/confirm
Content-Type: application/json

{
  "resetToken": "abc123...",
  "newPassword": "newSecurePassword123"
}
```

## API Endpoints

### Authentication
- POST `/api/auth/register` - Register new user
- POST `/api/auth/login` - Login
- POST `/api/auth/refresh` - Refresh access token
- POST `/api/auth/logout` - Logout (requires auth)
- POST `/api/auth/password/reset/request` - Request password reset
- POST `/api/auth/password/reset/verify` - Verify reset code
- POST `/api/auth/password/reset/confirm` - Confirm new password

### Users
- GET `/api/users/me` - Get current user profile
- PUT `/api/users/me` - Update current user profile
- PUT `/api/users/me/password` - Change password
- GET `/api/users` - Get all users (ADMIN only)
- GET `/api/users/{id}` - Get user by ID (ADMIN only)
- DELETE `/api/users/{id}` - Delete user (ADMIN only)

### Restaurants
- POST `/api/restaurants` - Create restaurant (ADMIN only)
- GET `/api/restaurants` - Get all restaurants (public)
- GET `/api/restaurants/{id}` - Get restaurant by ID (public)
- PUT `/api/restaurants/{id}` - Update restaurant (ADMIN/OWNER only)
- DELETE `/api/restaurants/{id}` - Delete restaurant (ADMIN only)

## Configuration

### JWT Token Expiration

- Access Token: 15 minutes (900000 ms)
- Refresh Token: 7 days (604800000 ms)

### CORS

By default, the following origins are allowed:
- http://localhost:3000
- http://localhost:5173
- http://localhost:4200

Configure via `CORS_ALLOWED_ORIGINS` environment variable.

### Database

Default configuration:
- Host: localhost
- Port: 3306
- Database: uTown
- DDL: update (auto-create/update tables)

## Development

### Hot Reload

Spring Boot DevTools is included for automatic restart on code changes.

### Database Logging

SQL queries are logged in development mode:
- `spring.jpa.show-sql=true`
- `spring.jpa.properties.hibernate.format_sql=true`

### Logging Levels

- Application: DEBUG
- Spring Web: INFO
- Hibernate SQL: DEBUG

## Security Notes

1. **JWT Secret**: Must be at least 256 bits for HS256 algorithm
2. **Database Password**: Never commit to git
3. **CORS Origins**: Restrict to your actual frontend domains in production
4. **Password Reset Code**: Currently hardcoded as "1234" - integrate SMS service in production

## Production Deployment

Before deploying to production:

1. Set `spring.jpa.hibernate.ddl-auto=validate`
2. Use Flyway or Liquibase for database migrations
3. Set `spring.jpa.show-sql=false`
4. Configure proper logging levels
5. Use strong JWT secret (256+ bits)
6. Integrate real SMS service for password reset
7. Configure production CORS origins
8. Use HTTPS only
9. Enable rate limiting
10. Set up monitoring and alerting

## License

MIT
