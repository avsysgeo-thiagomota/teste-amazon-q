# Recipe Management Frontend - Angular 17

This is the frontend application for the Recipe Management System, built with Angular 17 and Angular Material.

## Features

- **Modern Angular 17**: Using standalone components and latest Angular features
- **Angular Material**: Beautiful Material Design UI components
- **JWT Authentication**: Secure authentication with token-based system
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Recipe Management**: Complete CRUD operations for recipes
- **User Profile**: User registration, login, and profile management
- **Real-time Search**: Search and filter recipes by name and difficulty
- **Form Validation**: Client-side validation with error messages

## Technology Stack

- **Angular 17**
- **Angular Material 17**
- **TypeScript 5.2**
- **RxJS 7.8**
- **SCSS** for styling

## Prerequisites

- Node.js 18+ 
- npm 9+
- Angular CLI 17+

## Installation

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Install Angular CLI globally (if not already installed):
```bash
npm install -g @angular/cli@17
```

## Configuration

Update the API URL in `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

## Running the Application

### Development Server
```bash
npm start
# or
ng serve
```

The application will be available at `http://localhost:4200`

### Production Build
```bash
npm run build
# or
ng build --configuration production
```

## Project Structure

```
src/
├── app/
│   ├── components/          # Angular components
│   │   ├── login/          # Login component
│   │   ├── register/       # Registration component
│   │   ├── recipes/        # Recipe components
│   │   └── profile/        # User profile component
│   ├── guards/             # Route guards
│   ├── interceptors/       # HTTP interceptors
│   ├── models/             # TypeScript interfaces
│   ├── services/           # Angular services
│   ├── app.component.ts    # Root component
│   └── app.routes.ts       # Route configuration
├── environments/           # Environment configurations
├── assets/                # Static assets
└── styles.scss            # Global styles
```

## Key Components

### Authentication
- **LoginComponent**: User login form
- **RegisterComponent**: User registration form
- **AuthService**: Authentication service with JWT handling
- **AuthGuard**: Route protection
- **AuthInterceptor**: Automatic JWT token injection

### Recipe Management
- **RecipeListComponent**: Display recipes with search and filters
- **RecipeFormComponent**: Create/edit recipe form
- **RecipeDetailComponent**: View recipe details
- **RecipeService**: Recipe API communication

### User Management
- **ProfileComponent**: User profile management
- **UserService**: User-related API calls

## Features Overview

### Authentication Flow
1. User registers or logs in
2. JWT token is stored in localStorage
3. Token is automatically included in API requests
4. User is redirected to login on token expiration

### Recipe Management
- **List View**: Grid layout with recipe cards
- **Search**: Real-time search by recipe name
- **Filter**: Filter by difficulty level
- **Create/Edit**: Dynamic form with ingredients and steps
- **Delete**: Confirmation dialog before deletion

### Responsive Design
- Mobile-first approach
- Adaptive layouts for different screen sizes
- Touch-friendly interface

## API Integration

The frontend communicates with the Spring Boot backend through:

- **Authentication endpoints**: `/auth/signin`, `/auth/signup`
- **Recipe endpoints**: `/receitas` (CRUD operations)
- **User endpoints**: `/usuarios` (profile management)

## Styling

- **Angular Material**: Primary UI framework
- **Custom SCSS**: Additional styling and responsive design
- **Material Icons**: Consistent iconography
- **Material Theme**: Indigo-Pink color scheme

## Development Guidelines

### Component Structure
- Use standalone components (Angular 17 feature)
- Implement OnInit for initialization logic
- Use reactive forms for form handling
- Include proper error handling and loading states

### Service Pattern
- Services handle all HTTP communication
- Use RxJS observables for async operations
- Implement proper error handling
- Cache data when appropriate

### Routing
- Lazy-loaded components for better performance
- Route guards for authentication
- Clean URL structure

## Testing

Run unit tests:
```bash
npm test
# or
ng test
```

## Building for Production

1. Build the application:
```bash
ng build --configuration production
```

2. The build artifacts will be stored in the `dist/` directory

3. Serve the built files using a web server like nginx or Apache

## Environment Variables

### Development (`environment.ts`)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### Production (`environment.prod.ts`)
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-api-domain.com/api'
};
```

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Contributing

1. Follow Angular style guide
2. Use TypeScript strict mode
3. Write unit tests for new features
4. Follow the existing code structure
5. Use meaningful commit messages

## Troubleshooting

### Common Issues

1. **CORS errors**: Ensure the backend CORS configuration allows the frontend origin
2. **Authentication issues**: Check if JWT token is properly stored and sent
3. **Build errors**: Ensure all dependencies are installed and versions are compatible

### Development Tips

- Use Angular DevTools browser extension for debugging
- Enable source maps for easier debugging
- Use the Angular CLI for generating components and services
- Follow the reactive programming patterns with RxJS
