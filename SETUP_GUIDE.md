# Setup Guide - Recipe Management System

This guide will help you set up and run the modernized Recipe Management System with Spring Boot backend and Angular frontend.

## Prerequisites

### Backend Requirements
- **Java 17 or higher**
- **Maven 3.6+**
- **PostgreSQL 12+**

### Frontend Requirements
- **Node.js 18+**
- **npm 9+**
- **Angular CLI 17+**

## Database Setup

### 1. Ensure PostgreSQL is Running

Make sure your PostgreSQL server is running and accessible.

### 2. Create Database

Your existing database should work perfectly. The system expects a database named `receitas_db` with the following tables:

- `usuarios` - User management
- `receitas` - Recipe information  
- `ingredientes` - Recipe ingredients
- `passos` - Recipe steps

### 3. Update Database Configuration

Edit `backend/src/main/resources/application.yml` and update the database connection:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/receitas_db
    username: your_username
    password: your_password
```

## Backend Setup

### 1. Navigate to Backend Directory

```bash
cd backend
```

### 2. Start the Backend

**Option A: Using the startup script (recommended)**
```bash
./start.sh
```

**Option B: Using Maven directly**
```bash
mvn clean compile
mvn spring-boot:run
```

### 3. Verify Backend is Running

The API should be available at: `http://localhost:8080/api`

You can test it with:
```bash
curl http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## Frontend Setup

### 1. Navigate to Frontend Directory

```bash
cd frontend
```

### 2. Install Dependencies

```bash
npm install
```

### 3. Install Angular CLI (if not already installed)

```bash
npm install -g @angular/cli@17
```

### 4. Start the Frontend

**Option A: Using the startup script (recommended)**
```bash
./start.sh
```

**Option B: Using npm directly**
```bash
npm start
```

### 5. Access the Application

Open your browser and go to: `http://localhost:4200`

## Default Login Credentials

- **Username:** `admin`
- **Password:** `admin123`

## Testing the Migration

### 1. Login Test
1. Open `http://localhost:4200`
2. Use the credentials: `admin` / `admin123`
3. You should be redirected to the recipes page

### 2. Recipe Management Test
1. View existing recipes (if any from your original system)
2. Create a new recipe with ingredients and steps
3. Edit an existing recipe
4. Delete a recipe
5. Use the search functionality

### 3. User Profile Test
1. Click on the user menu (top right)
2. Go to "Perfil"
3. Update your profile information

## Troubleshooting

### Backend Issues

**Database Connection Error:**
```
Error: Connection refused
```
- Ensure PostgreSQL is running
- Check database credentials in `application.yml`
- Verify database exists and is accessible

**Port Already in Use:**
```
Error: Port 8080 is already in use
```
- Stop any other applications using port 8080
- Or change the port in `application.yml`:
```yaml
server:
  port: 8081
```

**Schema Validation Error:**
```
Schema-validation: wrong column type
```
- This should be fixed with the Integer ID types
- If it persists, check that your database schema matches the expected structure

### Frontend Issues

**Node.js Version Error:**
```
Error: Unsupported Node.js version
```
- Ensure you're using Node.js 18 or higher
- Update Node.js if necessary

**Angular CLI Not Found:**
```
Error: ng command not found
```
- Install Angular CLI globally: `npm install -g @angular/cli@17`

**CORS Error:**
```
Access to XMLHttpRequest blocked by CORS policy
```
- Ensure the backend is running on port 8080
- Check that the API URL in `src/environments/environment.ts` is correct

**Build Errors:**
```
Error: Cannot resolve dependencies
```
- Delete `node_modules` and `package-lock.json`
- Run `npm install` again

## API Endpoints

### Authentication
- `POST /api/auth/signin` - Login
- `POST /api/auth/signup` - Register
- `POST /api/auth/refresh` - Refresh token

### Recipes
- `GET /api/receitas` - List user's recipes
- `GET /api/receitas/{id}` - Get recipe details
- `POST /api/receitas` - Create recipe
- `PUT /api/receitas/{id}` - Update recipe
- `DELETE /api/receitas/{id}` - Delete recipe

### Users
- `GET /api/usuarios/me` - Get current user
- `PUT /api/usuarios/{id}` - Update user profile

## Development Tips

### Backend Development
- Use `mvn spring-boot:run` for hot reload
- Check logs in the console for debugging
- Use `application-dev.yml` for development-specific settings

### Frontend Development
- Use `ng serve` for hot reload
- Open browser developer tools for debugging
- Use Angular DevTools extension for better debugging

### Database Development
- Use a PostgreSQL client like pgAdmin or DBeaver
- Monitor database queries with `show-sql: true` in application.yml
- Use database migrations for schema changes

## Production Deployment

### Backend
1. Build the JAR: `mvn clean package`
2. Run: `java -jar target/receitas-api-1.0.0.jar`

### Frontend
1. Build: `ng build --configuration production`
2. Deploy the `dist/receitas-frontend` folder to a web server

## Migration from Legacy System

Your existing data should work seamlessly with the new system:

1. **Users:** All existing users can log in with their current credentials
2. **Recipes:** All recipes, ingredients, and steps are preserved
3. **Database:** No schema changes required

The new system provides:
- Modern, responsive UI
- Better security with JWT tokens
- RESTful API for future integrations
- Mobile-friendly design
- Real-time search and filtering

## Support

If you encounter issues:

1. Check the console logs (both backend and frontend)
2. Verify database connectivity
3. Ensure all prerequisites are installed
4. Check the troubleshooting section above
5. Review the individual README files in backend and frontend directories

## Next Steps

After successful setup:

1. **Customize the UI:** Modify Angular components and styles
2. **Add Features:** Extend the API and frontend with new functionality
3. **Security:** Review and enhance security settings
4. **Performance:** Optimize database queries and frontend bundle size
5. **Testing:** Add unit and integration tests
6. **Monitoring:** Set up logging and monitoring for production
