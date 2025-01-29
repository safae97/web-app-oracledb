# Oracle Database Administration Web Application

## 🌐 Introduction

This project presents the development of a web application for Oracle database administration, leveraging the power of modern web technologies and the J2EE ecosystem. The goal is to provide an intuitive, secure, and robust platform for efficiently managing Oracle databases. Built with **Spring Boot** for the backend and **Thymeleaf** for the frontend, the application interacts with an Oracle database running in a **Docker** container, ensuring flexibility and ease of deployment.

---

## 🎯 Objective

The primary objective of this project is to create a comprehensive web solution for Oracle database administration. The application facilitates various database operations, including:

- User management
- Backup and restoration
- Data security configuration
- Performance monitoring
- Optimization

Each functional module is designed to simplify database management for administrators while ensuring high standards of performance and security.

---

## 🛠️Technologies

### Backend:
- **Spring Boot**: A robust Java framework for building enterprise-level applications. It simplifies dependency management, application configuration, and integration of various components.
- **Hibernate**: An Object-Relational Mapping (ORM) tool used for seamless interaction with the Oracle database.
- **JDBC**: Direct SQL execution and database interaction for commands that may not be achievable via ORM.

### Frontend:
- **Thymeleaf**: A Java server-side template engine used to create dynamic and user-friendly HTML5 pages.
- **HTML5, CSS3, JavaScript**: Core web technologies for building responsive, interactive, and aesthetically pleasing user interfaces.

### Database:
- **Oracle Database**: A robust relational database management system (RDBMS) known for its performance, scalability, and security features.
- **Docker**: Used to containerize the Oracle database, ensuring a consistent and portable development and deployment environment.

### Additional Tools and Libraries:
- **RMAN (Recovery Manager)**: For managing database backups and restorations.
- **Chart.js**: For creating visually appealing performance dashboards.
- **Spring Security**: Implemented to provide robust authentication and authorization mechanisms, including form-based login, user role management, and CSRF protection. The security configuration ensures that all endpoints are secure and accessible only to authenticated users with appropriate permissions.

---

## 📥  Installation Documentation

1. **Install Docker Desktop**
2. **Install Oracle DB 23ai Image in Docker**
3. **Configure the `application.properties` File**: Ensure the necessary database connection details are correctly set.
4. **Enable ArchiveLog Mode and Create Backup Directory**: Configure the database to enable ArchiveLog mode and specify the backup storage directory.
5. **Create and Open Wallet for Security**

---

## 📄 Project Report
 Download the full project report here:  [Project_Report.docx](./PROJECT_RAPPORT.docx)

---

## 👥 Collaboration

This project was developed as a **binome** (team of two) by  [@fatimazahrae03](https://github.com/fatimazahrae03) and myself. We collaborated closely to design, implement, and test the application, ensuring it meets the highest standards of functionality and security the professor asked us for.


---

🚀 Feel free to explore the code and contribute to the project. Your feedback and suggestions are highly appreciated! 🚀

---


