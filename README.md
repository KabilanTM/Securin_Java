# Recipes Application

A Spring Boot application for managing and searching recipes with a MySQL database and a responsive web UI.

## Features

- **Data Import**: Automatically imports recipes from JSON file on startup
- **RESTful API**: Full CRUD operations with pagination and filtering
- **Advanced Search**: Filter by title, cuisine, rating, total time, and calories
- **Responsive UI**: Modern web interface with real-time filtering
- **Statistics**: Get insights about recipes (count by cuisine, average rating, etc.)

## Prerequisites

- Java 21
- MySQL 9.2
- Maven 3.9.9

## Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE recipesdb;
```

2. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/recipesdb
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Installation & Running

1. Clone the repository
2. Navigate to the project directory
3. Build the project:
```bash
mvn clean package
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Get All Recipes (Paginated)
```
GET /api/recipes?page=1&limit=10
```

### Get Recipe by ID
```
GET /api/recipes/{id}
```

### Search Recipes
```
GET /api/recipes/search?title=pie&cuisine=Southern&rating=>=4.5&total_time=<=120&calories=<=400
```

### Get Statistics
```
GET /api/recipes/stats
```

## Testing

### Database Import Test
1. Start the application
2. Check console logs for import status
3. Verify data is loaded by visiting `http://localhost:8080`

### API Testing
Use curl or Postman to test endpoints:

```bash
# Get all recipes (first page)
curl "http://localhost:8080/api/recipes?page=1&limit=10"

# Search for recipes
curl "http://localhost:8080/api/recipes/search?title=pie&rating=>=4.5"

# Get statistics
curl "http://localhost:8080/api/recipes/stats"
```

### Frontend Testing
1. Open `http://localhost:8080` in browser
2. Test pagination controls
3. Use filter inputs to search recipes
4. Click on recipes to view details in drawer
5. Test error handling by stopping the backend


## Technologies Used

- **Backend**: Spring Boot 3.5.5, Spring Data JPA, Hibernate
- **Database**: MySQL with JSON support
- **Frontend**: Vanilla JavaScript, HTML5, CSS3
- **Build Tool**: Maven
- **Caching**: Spring Cache

## Data Schema

The application uses the following database schema:
- Recipes table with JSON column for nutrients
- Generated column for numeric calories extraction
- Indexes for performance optimization


