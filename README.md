# SE PR Individual Assignment (summer semester 2024)
This repository contains the solution for the individual assignment in Software Engineering Project (SE PR) course.

## Project Overview
This project demonstrates an end-to-end web application featuring a backend server and a frontend client. It's designed to meet the requirements set forth in the SE PR course, covering essential software engineering principles.

### Technologies Used
- **Programming Language**: Java OpenJDK 21
- **Backend Framework**: Spring Boot 3.2
- **JavaScript Runtime**: Node.js 20.11.1
- **Frontend Framework**: Angular 17
- **Relational Database**: H2 2.2.x
- **Testing Framework**: JUnit 5.x, AssertJ
- **Build & Dependency Management**:
  - Backend: Maven 3
  - Frontend: NPM 10.2.4
- **Version Control**: Git 2.x

## Project Structure
- **`backend/`**: Contains the server-side application
- **`frontend/`**: Contains the client-side application

### Prerequisites
To run or develop this project, you'll need to have the following installed:
- **Java JDK 21**
- **Node.js 20.x**
- **Maven 3**
- **NPM 10.x**
- **Git 2.x**

### Getting Started
1. **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/your-repo.git
    cd your-repo
    ```
2. **Set up the backend**:
    ```bash
    cd backend
    mvn clean install
    ```
3. **Set up the frontend**:
    ```bash
    cd ../frontend
    npm install
    ```

### Building and Running the Application
#### Backend
Compile backend, add test data to the database and start the program
```bash
cd backend
mvn clean package
java -Dspring.profiles.active=datagen -jar target/e01234567-0.0.1-SNAPSHOT.jar
```

#### Frontend
```bash
cd backend
mvn test
```

### Testing
#### Backend:
```bash
cd backend
mvn test
```
  
### Documentation
- Backend-specific documentation is in the `backend/` directory.
- Frontend-specific documentation is in the `frontend/` directory.

### License
This project is licensed under the MIT License - see the `LICENSE.md` file for details.

### Acknowledgements
Thanks to the SE PR course team for their guidance and support.
