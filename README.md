# Requisition and Purchase Order Management System

This is a Spring Boot application that provides a comprehensive system for managing requisitions and purchase orders within an organization. It is designed to streamline the procurement process, from initial request to final purchase order, with role-based access control for different user types.

## How to Run the Application

To run the application, you will need to have Java 17 and Maven installed.

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd <repository-directory>
    ```

2.  **Run the application:**
    You can run the application using the Maven wrapper included in the project:
    ```bash
    ./mvnw spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

## User Roles and Credentials

The system is pre-configured with a set of default users for testing purposes. The password for all users is `Password1!`.

| Username  | Role       | Department | Email                |
|-----------|------------|------------|----------------------|
| `hod1`    | HOD        | IT         | hod1@example.com     |
| `buyer1`  | BUYER      | N/A        | buyer1@example.com   |
| `approver1`| APPROVER   | N/A        | approver1@example.com|
| `user1`   | USER       | IT         | user1@example.com    |

You can use these credentials to log in and test the application from the perspective of different user roles.
