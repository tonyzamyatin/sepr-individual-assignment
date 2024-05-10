# Backend Application (Spring Boot)

This directory contains the backend server application for the SE PR Einzelbeispiel.

## Technologies Used
- **Programming Language**: Java OpenJDK 21
- **Backend Framework**: Spring Boot 3.2
- **Relational Database**: H2 2.2.x
- **Testing Framework**: JUnit 5.x, AssertJ
- **Build & Dependency Management**: Maven 3

## Getting Started
### Prerequisites
- **Java JDK 21**
- **Maven 3**

### Setup Instructions
1. **Install dependencies**:
    ```bash
    mvn clean install
    ```

2. **Run the server**:
    ```bash
    mvn spring-boot:run
    ```

## Testing
To run the tests, use:
```bash
mvn test
```

## API Endpoints
#### Breeds
- `GET /api/breeds`: List all breeds
- `POST /api/breeds`: Create a new breed
- `PUT /api/breeds/{id}`: Update breed details
- `DELETE /api/breeds/{id}`: Delete a breed
  
#### Horses
- `GET /api/horses`: List all horses
- `POST /api/horses`: Create a new horse
- `PUT /api/horses/{id}`: Update horse details
- `DELETE /api/horses/{id}`: Delete a horse

#### Tournaments
- `GET /api/tournaments`: List all tournaments
- `POST /api/tournaments`: Create a new tournament
- `PUT /api/tournaments/{id}`: Update tournament details
- `DELETE /api/tournaments/{id}`: Delete a tournament
  
#### Tournament Standings
- `GET /api/tournaments/{id}/standings`: Get standings for a specific tournament
- `PUT /api/tournaments/{id}/standings`: Update tournament standings
- `DELETE /api/tournaments/{id}/standings`: Delete tournament standings
