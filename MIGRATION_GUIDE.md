# Migration Guide: From Legacy Java/ExtJS/JSP to Spring Boot + Angular

This guide explains how to migrate your legacy Recipe Management System to a modern architecture with Spring Boot REST API backend and Angular frontend.

## Overview

### Before (Legacy Architecture)
- **Backend**: Java 8 Servlets + JSP
- **Frontend**: ExtJS 3.4 + JSP pages
- **Database**: PostgreSQL (direct JDBC)
- **Authentication**: Session-based
- **Deployment**: WAR file on Tomcat

### After (Modern Architecture)
- **Backend**: Spring Boot 3.x REST API
- **Frontend**: Angular 17 SPA
- **Database**: PostgreSQL (JPA/Hibernate)
- **Authentication**: JWT tokens
- **Deployment**: Separate JAR + Static files

## Migration Benefits

1. **Separation of Concerns**: Clear separation between frontend and backend
2. **Modern Technologies**: Latest versions of frameworks and libraries
3. **Better Security**: JWT-based authentication, CORS support
4. **Scalability**: Independent scaling of frontend and backend
5. **Developer Experience**: Better tooling, hot reload, TypeScript
6. **Mobile Ready**: Responsive design with Angular Material
7. **API First**: RESTful API can be consumed by multiple clients

## Step-by-Step Migration

### Phase 1: Database Migration

The database schema remains the same, so no migration is needed. Your existing PostgreSQL database will work with the new system.

**Existing Tables:**
- `usuarios` - User management
- `receitas` - Recipe information
- `ingredientes` - Recipe ingredients
- `passos` - Recipe steps

### Phase 2: Backend Migration

#### 2.1 Setup Spring Boot Project

1. **Navigate to backend directory:**
```bash
cd backend
```

2. **Install dependencies:**
```bash
mvn clean install
```

3. **Configure database connection in `application.yml`:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/receitas_db
    username: your_username
    password: your_password
```

#### 2.2 Key Changes from Legacy Code

| Legacy (Servlet) | Modern (Spring Boot) |
|------------------|---------------------|
| `HttpServlet` | `@RestController` |
| Manual JSON parsing | Automatic JSON serialization |
| JDBC connections | JPA/Hibernate entities |
| Session management | JWT tokens |
| Manual CORS | Spring Security CORS |

#### 2.3 API Endpoints Mapping

| Legacy Endpoint | Modern Endpoint | Method |
|----------------|-----------------|---------|
| `/LoginServlet` | `/auth/signin` | POST |
| `/LogoutServlet` | `/auth/logout` | POST |
| `/ReceitaServlet?action=list` | `/receitas` | GET |
| `/ReceitaServlet?action=create` | `/receitas` | POST |
| `/ReceitaServlet?action=update` | `/receitas/{id}` | PUT |
| `/ReceitaServlet?action=delete` | `/receitas/{id}` | DELETE |

#### 2.4 Start Backend Server

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api`

### Phase 3: Frontend Migration

#### 3.1 Setup Angular Project

1. **Navigate to frontend directory:**
```bash
cd frontend
```

2. **Install dependencies:**
```bash
npm install
```

3. **Install Angular CLI (if needed):**
```bash
npm install -g @angular/cli@17
```

#### 3.2 Key Changes from Legacy Frontend

| Legacy (ExtJS) | Modern (Angular) |
|----------------|------------------|
| `Ext.grid.GridPanel` | Angular Material Table |
| `Ext.form.FormPanel` | Reactive Forms |
| `Ext.Window` | Angular Material Dialog |
| `Ext.data.Store` | Angular Services + RxJS |
| Manual DOM manipulation | Component-based architecture |

#### 3.3 Component Mapping

| Legacy ExtJS | Modern Angular |
|-------------|----------------|
| `ReceitaGrid.js` | `RecipeListComponent` |
| `ReceitaWindow.js` | `RecipeFormComponent` |
| `UsuarioWindow.js` | `ProfileComponent` |
| `login.js` | `LoginComponent` |

#### 3.4 Start Frontend Server

```bash
npm start
```

The application will be available at `http://localhost:4200`

### Phase 4: Data Migration

Since the database schema is compatible, no data migration is required. The new system will work with your existing data.

**Verification Steps:**
1. Ensure existing users can log in
2. Verify all recipes are displayed correctly
3. Test CRUD operations on recipes
4. Confirm ingredients and steps are properly loaded

## Feature Comparison

### Authentication

| Feature | Legacy | Modern |
|---------|--------|--------|
| Login Method | Session-based | JWT tokens |
| Security | Basic | Enhanced with Spring Security |
| Session Management | Server-side | Stateless |
| Password Encryption | BCrypt | BCrypt (same) |

### Recipe Management

| Feature | Legacy | Modern |
|---------|--------|--------|
| List Recipes | ExtJS Grid | Angular Material Cards |
| Search | Server-side | Real-time client-side |
| Create/Edit | ExtJS Window | Angular Material Form |
| Validation | Server-side only | Client + Server |
| Responsive | No | Yes |

### User Experience

| Feature | Legacy | Modern |
|---------|--------|--------|
| UI Framework | ExtJS 3.4 | Angular Material 17 |
| Mobile Support | No | Yes |
| Loading States | Basic | Enhanced with spinners |
| Error Handling | Basic alerts | Snackbar notifications |
| Navigation | Page-based | SPA routing |

## Configuration Guide

### Backend Configuration

**Database Connection (`application.yml`):**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/receitas_db
    username: postgres
    password: postgres
```

**JWT Configuration:**
```yaml
jwt:
  secret: mySecretKey123456789012345678901234567890
  expiration: 86400000 # 24 hours
```

**CORS Configuration:**
```yaml
cors:
  allowed-origins: http://localhost:4200
```

### Frontend Configuration

**API URL (`environment.ts`):**
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

## Testing the Migration

### 1. Backend API Testing

Test the REST endpoints using curl or Postman:

```bash
# Login
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Get recipes (with JWT token)
curl -X GET http://localhost:8080/api/receitas \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. Frontend Testing

1. Open `http://localhost:4200`
2. Test user registration and login
3. Create, edit, and delete recipes
4. Test search and filtering
5. Verify responsive design on mobile

### 3. Integration Testing

1. Ensure frontend can communicate with backend
2. Test authentication flow
3. Verify CRUD operations work end-to-end
4. Test error handling

## Deployment Guide

### Backend Deployment

1. **Build the JAR:**
```bash
mvn clean package
```

2. **Run the JAR:**
```bash
java -jar target/receitas-api-1.0.0.jar
```

### Frontend Deployment

1. **Build for production:**
```bash
ng build --configuration production
```

2. **Deploy static files:**
   - Copy `dist/receitas-frontend/*` to your web server
   - Configure web server to serve `index.html` for all routes

### Docker Deployment (Optional)

**Backend Dockerfile:**
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/receitas-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

**Frontend Dockerfile:**
```dockerfile
FROM nginx:alpine
COPY dist/receitas-frontend /usr/share/nginx/html
EXPOSE 80
```

## Troubleshooting

### Common Issues

1. **CORS Errors:**
   - Ensure backend CORS configuration includes frontend URL
   - Check that preflight requests are handled

2. **Authentication Issues:**
   - Verify JWT secret is consistent
   - Check token expiration settings
   - Ensure Authorization header is properly set

3. **Database Connection:**
   - Verify PostgreSQL is running
   - Check connection string and credentials
   - Ensure database exists and has proper schema

4. **Build Issues:**
   - Ensure Java 17+ for backend
   - Ensure Node.js 18+ for frontend
   - Clear Maven/npm caches if needed

### Performance Considerations

1. **Backend:**
   - Enable JPA query optimization
   - Configure connection pooling
   - Add caching for frequently accessed data

2. **Frontend:**
   - Enable lazy loading for routes
   - Optimize bundle size
   - Use OnPush change detection strategy

## Rollback Plan

If issues arise during migration:

1. **Keep legacy system running** during migration
2. **Use feature flags** to gradually migrate users
3. **Database backup** before any schema changes
4. **Gradual migration** - migrate one feature at a time

## Next Steps

After successful migration:

1. **Add unit tests** for both backend and frontend
2. **Set up CI/CD pipeline** for automated deployment
3. **Add monitoring** and logging
4. **Performance optimization**
5. **Security audit**
6. **User training** on new interface

## Support

For issues during migration:

1. Check the README files in backend and frontend directories
2. Review the API documentation
3. Test individual components in isolation
4. Use browser developer tools for frontend debugging
5. Check application logs for backend issues

## Conclusion

This migration transforms your legacy application into a modern, scalable, and maintainable system. The new architecture provides:

- Better separation of concerns
- Modern development experience
- Enhanced security
- Mobile-ready interface
- API-first approach for future integrations

The migration preserves all existing functionality while providing a foundation for future enhancements.
