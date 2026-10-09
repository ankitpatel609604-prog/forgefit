# ForgeFit Gym - Professional Fitness Management System

A full-stack web application for gym and fitness training management with modern authentication, responsive UI, and comprehensive member management.

---

## 🎯 Project Overview

ForgeFit is a premium gym management platform designed to provide:
- Professional user authentication with secure session management
- Modern, responsive UI for desktop and mobile devices
- RESTful API backend for all gym operations
- Real-time member profile management
- Comprehensive fitness program and trainer management

**Live URL:** `http://localhost:8080`

---

## ✨ Features

### 🔐 Authentication & User Management
- **User Registration** - Create new member accounts with email validation
- **User Login** - Secure authentication with BCrypt password hashing
- **Session Management** - HTTP session-based authentication
- **User Profile** - Display logged-in user with first name initial avatar
- **Logout Functionality** - Clear session and return to login state
- **Remember Me** - 30-day cookie-based session persistence
- **Password Security** - Minimum 8 characters with uppercase, lowercase, number, and special character requirements

### 🎨 User Interface
- **Modern Login/Signup Form** - Beautiful gradient design with smooth animations
- **Responsive Design** - Works seamlessly on desktop, tablet, and mobile
- **Tab Navigation** - Easy switching between Login and Sign Up
- **Real-time Validation** - Client-side and server-side form validation
- **Loading States** - Visual feedback with spinner indicators
- **Error Handling** - Clear, user-friendly error messages
- **Success Alerts** - Confirmation messages with auto-redirect

### 🏋️ Gym Features
- **Fitness Programs** - Browse available training programs (Strength Lab, Fat Burn, Performance Elite)
- **Trainer Profiles** - View certified coaches with specialties
- **Membership Plans** - Choose from Starter, Pro, and Elite tiers
- **Schedule & Classes** - Weekly training schedule with multiple sessions
- **Contact Form** - Send inquiries and schedule consultations
- **Testimonials** - Member success stories and ratings
- **Gallery** - Browse gym facilities and training environment

### 🔒 Security
- **BCrypt Password Hashing** - Cost factor 10 with adaptive timing (~200ms per hash)
- **Unique Email Validation** - Prevent duplicate registrations
- **CSRF Protection** - Disabled for API (can be enabled with token handling)
- **HTTP-only Secure Cookies** - Prevent JavaScript access to session data
- **Session Fixation Protection** - Prevent session hijacking attacks
- **Spring Security Integration** - Enterprise-grade authentication framework
- **DaoAuthenticationProvider** - Custom user details service integration

---

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17 | Core programming language |
| **Spring Boot** | 3.3.2 | Web framework & application foundation |
| **Spring Security** | 3.x | Authentication & authorization |
| **Spring Data JPA** | 3.3.2 | Database ORM & persistence |
| **Hibernate** | 6.5.2 | JPA implementation |
| **Apache Tomcat** | 10.1.26 | Embedded web server |
| **H2 Database** | Latest | In-memory relational database |
| **HikariCP** | 5.0+ | Connection pooling |
| **Lombok** | Latest | Code generation (optional) |
| **Jakarta Persistence** | 3.x | JPA specification implementation |

### Frontend
| Technology | Purpose |
|-----------|---------|
| **HTML5** | Semantic markup & structure |
| **CSS3** | Modern styling with gradients, animations, flexbox |
| **JavaScript (Vanilla)** | Client-side logic & form handling |
| **LocalStorage API** | Client-side user data persistence |

### Build & Deployment
| Tool | Version | Purpose |
|------|---------|---------|
| **Maven** | 3.10.0 | Build automation & dependency management |
| **Maven Compiler Plugin** | 3.13.0 | Java compilation |
| **Maven Jar Plugin** | 3.4.2 | JAR packaging |
| **Spring Boot Maven Plugin** | 3.3.2 | Spring Boot JAR repackaging |
| **Maven Surefire Plugin** | 3.2.5 | Unit test execution |

### Development
| Tool | Purpose |
|------|---------|
| **Git** | Version control |
| **PowerShell** | Build scripting (Windows) |
| **Maven Central** | Dependency repository |

---

## 📁 Project Structure

```
ForgeFit-Gym/
├── src/
│   ├── main/
│   │   ├── java/com/forgefit/
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java          # Spring Security configuration
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java          # Authentication endpoints
│   │   │   │   └── GymController.java           # Gym info endpoints (disabled)
│   │   │   ├── model/
│   │   │   │   ├── User.java                    # User entity
│   │   │   │   ├── Program.java                 # Training program entity
│   │   │   │   ├── Trainer.java                 # Trainer entity
│   │   │   │   ├── MembershipPlan.java          # Membership tier entity
│   │   │   │   └── ContactMessage.java          # Contact form entity
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java          # User data access
│   │   │   │   ├── ProgramRepository.java       # Program data access
│   │   │   │   ├── TrainerRepository.java       # Trainer data access
│   │   │   │   ├── MembershipPlanRepository.java # Membership data access
│   │   │   │   └── ContactMessageRepository.java # Contact form data access
│   │   │   ├── service/
│   │   │   │   └── CustomUserDetailsService.java # User authentication service
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.java            # Login request DTO
│   │   │   │   ├── RegisterRequest.java         # Registration request DTO
│   │   │   │   └── AuthResponse.java            # Authentication response DTO
│   │   │   ├── DataLoader.java                  # Initial data loader (disabled)
│   │   │   └── FitnessApplication.java          # Spring Boot entry point
│   │   └── resources/
│   │       ├── application.properties           # Application configuration
│   │       └── static/
│   │           ├── index.html                   # Home page with program listings
│   │           ├── login.html                   # Login & signup form
│   │           ├── dashboard.html               # Post-login dashboard
│   │           ├── styles.css                   # Global styles for home page
│   │           ├── script.js                    # Home page scripts
│   │           └── auth.js                      # Authentication form scripts
│   └── test/
│       └── java/com/forgefit/repository/
│           └── UserRepositoryTest.java          # User repository tests
├── pom.xml                                       # Maven build configuration
├── README.md                                     # This file
└── .gitignore                                    # Git ignore rules
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+** installed
- **Maven 3.10.0+** installed
- **Git** (optional, for version control)

### Installation & Setup

#### 1. Clone/Download the Project
```bash
cd c:\Users\ANKIT\Downloads\java project
```

#### 2. Download Maven (if not installed)
```powershell
# PowerShell - automatic setup
.\setup-maven.ps1
```

#### 3. Build the Project
```bash
mvn clean install -DskipTests
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: ~10 seconds
```

#### 4. Run the Application
```bash
java -jar target/forgefit-gym-1.0.0.jar
```

**Expected Output:**
```
[INFO] Tomcat started on port 8080 (http) with context path '/'
[INFO] Started FitnessApplication in 7.7 seconds
```

#### 5. Access the Application
- **Home Page:** `http://localhost:8080/index.html`
- **Login/Signup:** `http://localhost:8080/login.html`
- **H2 Console:** `http://localhost:8080/h2-console`

---

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(254) NOT NULL UNIQUE,
  full_name VARCHAR(100) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL DEFAULT 'USER'
);
```

### Additional Tables
- **Programs** - Training program offerings
- **Trainers** - Coach profiles and specialties
- **Membership_Plans** - Available membership tiers
- **Contact_Messages** - Inquiry submissions

---

## 🔌 API Endpoints

### Authentication Endpoints

#### Register User
```
POST /api/auth/register
Content-Type: application/json

Request:
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "Password123!"
}

Response: 200 OK
{
  "id": 1,
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "USER",
  "message": "Registration successful"
}
```

#### Login User
```
POST /api/auth/login
Content-Type: application/json

Request:
{
  "email": "john@example.com",
  "password": "Password123!"
}

Response: 200 OK
{
  "email": "john@example.com",
  "role": "USER",
  "message": "Login successful"
}
```

#### Logout User
```
POST /api/auth/logout

Response: 200 OK
```

---

## 🎨 UI Components

### Login Form
- Email input with validation
- Password input with strength indicator
- Sign Up / Login tab switching
- Remember Me checkbox
- Real-time error messages
- Loading indicator with spinner
- Success/error alerts

### Home Page
- Navigation bar with profile dropdown (when logged in)
- Hero section with CTA buttons
- Features section (4 gym features)
- About section with image gallery
- Programs section (3 fitness programs)
- Weekly schedule table
- Trainer profiles (3 coaches)
- Pricing plans (3 tiers)
- Testimonials section
- Image gallery
- Contact form
- Footer

### User Profile
- First name initial avatar (gradient background)
- Name display
- Dropdown menu on hover
- Profile link
- Logout link

---

## 🔐 Authentication Flow

```
1. User visits index.html
   ↓
2. Check localStorage for 'forgefit_user'
   ├─ If found → Show profile dropdown
   └─ If not found → Show Login button
   ↓
3. User clicks Login → Redirect to login.html
   ↓
4. User enters credentials → Submit to /api/auth/login
   ↓
5. Server validates credentials
   ├─ Valid → Return user data + Set session cookie
   └─ Invalid → Return error message
   ↓
6. Client stores user data in localStorage
   ↓
7. Redirect to index.html
   ↓
8. Profile dropdown displays user's first name
   ↓
9. User clicks Logout
   ↓
10. Clear localStorage + Call /api/auth/logout
    ↓
11. Session invalidated, redirect to index.html
```

---

## 🧪 Testing

### Manual Testing
```bash
# Register new user
Email: testuser@gmail.com
Password: TestPassword123!

# Login with registered credentials
Email: testuser@gmail.com
Password: TestPassword123!

# Verify redirect to index.html with profile
# Verify dropdown menu on profile hover
# Test logout functionality
```

### Testing H2 Database
Access the H2 Console:
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:forgefitdb
User: sa
Password: (leave empty)
```

---

## 📝 Configuration

### application.properties
```properties
# Server
server.port=8080

# Database (H2 In-Memory)
spring.datasource.url=jdbc:h2:mem:forgefitdb
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

# Security
spring.security.user.name=admin
spring.security.user.password=admin
```

---

## 🚨 Known Issues & Limitations

### Current Limitations
1. **H2 In-Memory Database** - Data is lost on server restart
2. **No Email Verification** - Emails not validated for delivery
3. **No Password Recovery** - No forgot password functionality
4. **Single Session** - One session per browser (not enforced across devices)
5. **CSRF Protection Disabled** - Can be enabled with token handling

### Planned Features (Phase 2+)
- [ ] JWT token authentication
- [ ] Email verification
- [ ] Password reset functionality
- [ ] Multi-device session management
- [ ] Admin dashboard
- [ ] Payment integration
- [ ] Booking system
- [ ] Performance metrics

---

## 🔧 Troubleshooting

### Issue: Port 8080 already in use
```bash
# Find and kill process on port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Issue: Maven not recognized
```bash
# Verify Maven installation
mvn -v

# If not found, add to PATH or use full path
C:\Users\ANKIT\Downloads\apache-maven-3.10.0\bin\mvn clean install
```

### Issue: H2 Console shows "No data"
- Server was restarted (H2 in-memory database resets)
- Solution: Re-register and login to populate data

### Issue: Login button doesn't work
- Check browser console for JavaScript errors
- Verify server is running on port 8080
- Clear browser cache and localStorage

---

## 📚 Dependencies

### Maven Dependencies Summary
```
Spring Boot Parent: 3.3.2
├── spring-boot-starter-web
├── spring-boot-starter-data-jpa
├── spring-boot-starter-security
├── spring-boot-starter-validation
├── h2 (runtime)
├── lombok (optional)
└── spring-boot-starter-test (test scope)
```

---

## 📞 Support & Contact

**Project:** ForgeFit Gym Management System  
**Version:** 1.0.0  
**Java Version:** 17+  
**Spring Boot:** 3.3.2  
**Last Updated:** October 9, 2026

For issues or feature requests, please contact the development team.

---

## 📄 License

This project is proprietary software. All rights reserved.

---

## 🙏 Acknowledgments

- Spring Framework for excellent backend framework
- Bootstrap for responsive design inspiration
- Unsplash for stock photography

---

## 📌 Quick Reference

| Command | Purpose |
|---------|---------|
| `mvn clean install -DskipTests` | Build the project |
| `java -jar target/forgefit-gym-1.0.0.jar` | Run the application |
| `mvn clean` | Delete target directory |
| `mvn test` | Run all tests |
| `http://localhost:8080` | Access application |

---

**Happy training with ForgeFit! 💪**
