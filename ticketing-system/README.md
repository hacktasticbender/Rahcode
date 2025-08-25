# TicketPro - Full-Stack Ticketing System

A comprehensive IT support ticketing system built with Spring Boot (Java) backend and Next.js frontend. This system provides role-based access control, ticket management, email notifications, and admin functionality.

## 🚀 Features

### Must-Have Features (Implemented)
- ✅ **Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control (User, Support Agent, Admin)
  - Secure login/logout functionality

- ✅ **User Dashboard**
  - Create new tickets with subject, description, and priority
  - View personal tickets and their status
  - Add comments to tickets
  - Track ticket lifecycle (Open → In Progress → Resolved → Closed)
  - View ticket history with comments

- ✅ **Ticket Management**
  - Complete ticket lifecycle management
  - Ticket assignment to support agents
  - Comment threads with timestamps and user information
  - Priority levels (Low, Medium, High, Urgent)

- ✅ **Admin Panel**
  - User management (add/remove users, assign roles)
  - System-wide ticket management
  - Force reassign or resolve/close tickets
  - Monitor ticket statuses across all users

- ✅ **Access Control**
  - Admins can manage users and override tickets
  - Support agents can manage assigned tickets
  - Users can only access their own tickets

### Good-to-Have Features (Implemented)
- ✅ **Email Notifications**
  - Ticket creation notifications
  - Assignment notifications
  - Status change notifications
  - Comment notifications

### Additional Features (Ready for Implementation)
- 🔄 **Search & Filter** (Backend ready, frontend pending)
- 🔄 **File Attachments** (Backend ready, frontend pending)
- 🔄 **Rating System** (Backend ready, frontend pending)

## 🛠 Tech Stack

### Backend
- **Java 17** with **Spring Boot 3.2.1**
- **PostgreSQL** database
- **Spring Security** with JWT authentication
- **Spring Data JPA** for data persistence
- **Spring Mail** for email notifications
- **Gradle** for build management

### Frontend
- **Next.js 14** with **React 18**
- **TypeScript** for type safety
- **Tailwind CSS** for styling
- **Headless UI** for accessible components
- **React Hook Form** for form management
- **Axios** for API communication
- **React Hot Toast** for notifications

## 📋 Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- PostgreSQL 12 or higher
- npm or yarn

## 🚀 Getting Started

### 1. Database Setup

Create a PostgreSQL database:
```sql
CREATE DATABASE ticketing_db;
CREATE USER ticketing_user WITH PASSWORD 'ticketing_password';
GRANT ALL PRIVILEGES ON DATABASE ticketing_db TO ticketing_user;
```

### 2. Backend Setup

Navigate to the backend directory:
```bash
cd backend
```

Configure application properties:
```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ticketing_db
    username: ticketing_user
    password: ticketing_password
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
```

Run the backend:
```bash
./gradlew bootRun
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup

Navigate to the frontend directory:
```bash
cd frontend
```

Install dependencies:
```bash
npm install
```

Run the frontend:
```bash
npm run dev
```

The frontend will start on `http://localhost:3000`

## 👥 Default Users

The system comes with pre-configured demo accounts:

| Role | Username | Password | Description |
|------|----------|----------|-------------|
| Admin | `admin` | `admin123` | Full system access |
| Support Agent | `support` | `support123` | Can manage assigned tickets |
| User | `user` | `user123` | Can create and manage own tickets |

## 🎯 Usage

### For Regular Users
1. **Login** with your credentials
2. **Dashboard** - View ticket statistics and recent tickets
3. **Create Ticket** - Submit new support requests
4. **My Tickets** - View and manage your tickets
5. **Add Comments** - Communicate about ticket progress

### For Support Agents
- All user functionality plus:
- **Assigned Tickets** - View tickets assigned to you
- **Ticket Assignment** - Assign tickets to team members
- **Status Management** - Update ticket status and progress

### For Administrators
- All previous functionality plus:
- **User Management** - Create, modify, and disable users
- **Role Assignment** - Assign roles to users
- **System Oversight** - View and manage all tickets
- **Force Actions** - Override ticket assignments and status

## 🔧 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout

### Tickets
- `GET /api/tickets` - Get tickets (filtered by role)
- `POST /api/tickets` - Create new ticket
- `GET /api/tickets/{id}` - Get ticket details
- `PUT /api/tickets/{id}` - Update ticket
- `POST /api/tickets/{id}/assign` - Assign ticket
- `POST /api/tickets/{id}/comments` - Add comment
- `POST /api/tickets/{id}/rating` - Rate ticket

### Admin
- `GET /api/admin/users` - Get all users
- `PUT /api/admin/users/{id}/role` - Update user role
- `DELETE /api/admin/users/{id}` - Delete user

## 🔒 Security Features

- **JWT Authentication** - Secure token-based authentication
- **Role-Based Access Control** - Granular permissions system
- **CORS Configuration** - Secure cross-origin requests
- **Input Validation** - Server-side validation for all inputs
- **SQL Injection Protection** - JPA prevents SQL injection
- **XSS Protection** - React's built-in XSS protection

## 📧 Email Configuration

To enable email notifications, configure SMTP settings in `application.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

For Gmail, use an App Password instead of your regular password.

## 🚀 Deployment

### Backend Deployment
1. Build the application: `./gradlew build`
2. Run the JAR file: `java -jar build/libs/ticketing-backend-0.0.1-SNAPSHOT.jar`

### Frontend Deployment
1. Build the application: `npm run build`
2. Start the production server: `npm start`

### Environment Variables
- `JWT_SECRET` - Secret key for JWT tokens
- `MAIL_USERNAME` - SMTP username
- `MAIL_PASSWORD` - SMTP password
- `NEXT_PUBLIC_API_URL` - Backend API URL for frontend

## 📈 Future Enhancements

- **File Attachments** - Upload and manage ticket attachments
- **Advanced Search** - Full-text search across tickets
- **Reporting Dashboard** - Analytics and reporting features
- **Mobile App** - React Native mobile application
- **Real-time Updates** - WebSocket-based live updates
- **Integration APIs** - Third-party service integrations

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

---

**TicketPro** - Streamlining IT support and customer service with modern technology.