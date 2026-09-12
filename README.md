<div align="center">

# 🏨 Hotel Booking System

### A modern, full-stack hotel booking and management platform

[![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Render](https://img.shields.io/badge/Deployed%20on-Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)](https://render.com/)
[![License](https://img.shields.io/badge/License-Educational-blue?style=for-the-badge)]()

</div>

---

## 📖 Table of Contents

- [About the Project](#-about-the-project)
- [Features](#-features)
- [Tech Stack](#️-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Environment Variables](#environment-variables)
  - [Running Locally](#running-locally)
  - [Running with Docker](#running-with-docker)
- [Deployment](#️-deployment)
- [Architecture Overview](#️-architecture-overview)
- [Screenshots](#-screenshots)
- [Roadmap](#️-roadmap)
- [Author](#-author)
- [License](#-license)

---

## 🎯 About the Project

**Hotel Booking System** is a complete web application that lets guests browse rooms, make reservations, manage bookings, and handle payments — while giving admins full control over rooms, bookings, and hotel operations. Built on **Spring Boot** with a clean **Thymeleaf** frontend, it demonstrates a production-style architecture including secure authentication, cloud image storage, email notifications, and containerized deployment.

> Grand Meridian Resort — *"The sea, the sky, and a room that makes time stand still."*

---

## ✨ Features

### 👤 Guest Experience
- 🔍 Search and filter available rooms
- 📅 Book rooms with real-time availability
- 🧾 View booking confirmations and details
- 💳 Track payment and checkout status
- 🍽️ Order food and room services
- ❤️ Save favorite rooms
- 🔔 Receive booking notifications
- 🧑‍💼 Manage personal profile

### 🛠️ Admin / Management
- 📊 Centralized dashboard for hotel operations
- 🛏️ Manage room listings and availability
- 💰 View and manage money receipts
- 📦 Manage food & service orders
- 📧 Automated email notifications

### 🔒 Security & Infrastructure
- Spring Security-based authentication & authorization
- Environment-based secret management (no hardcoded credentials)
- Cloud-based image storage via Cloudinary
- Dockerized for consistent deployment across environments

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 25 |
| **Framework** | Spring Boot 4.1.0 |
| **Web Layer** | Spring Web MVC |
| **Persistence** | Spring Data JPA |
| **Security** | Spring Security + Thymeleaf Security Extras |
| **Validation** | Spring Boot Validation |
| **Templating** | Thymeleaf |
| **Database** | PostgreSQL (hosted on Supabase) |
| **Image Storage** | Cloudinary |
| **Email Service** | Spring Mail (SMTP / Gmail) |
| **Monitoring** | Spring Boot Actuator |
| **Build Tool** | Maven |
| **Containerization** | Docker (multi-stage build) |
| **Hosting** | Render |

---

## 📁 Project Structure

```
hotel_bookingv5/
├── src/
│   ├── main/
│   │   ├── java/bd/hotel_booking/
│   │   │   ├── controller/     # REST & MVC controllers
│   │   │   ├── service/        # Business logic
│   │   │   ├── repository/     # JPA repositories
│   │   │   ├── model/          # Entity classes
│   │   │   ├── config/         # Security & app configuration
│   │   │   └── dto/            # Data transfer objects
│   │   └── resources/
│   │       ├── templates/      # Thymeleaf HTML pages
│   │       ├── static/         # CSS, JS, images
│   │       └── application.properties
│   └── test/
│       └── java/bd/hotel_booking/   # Unit & integration tests
├── Dockerfile
├── pom.xml
├── HELP.md
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed:

- ☕ **Java 25**
- 📦 **Maven 3.9+**
- 🐘 **PostgreSQL** database (or a Supabase project)
- ☁️ **Cloudinary** account
- 📧 **Gmail** (or any SMTP provider) for email service
- 🐳 **Docker** (optional, for containerized runs)

### Environment Variables

This project never stores secrets in code. Configure the following environment variables before running:

```env
# Database
DATASOURCE_URL=jdbc:postgresql://<host>:5432/<database>
DATASOURCE_USERNAME=<db-username>
DATASOURCE_PASSWORD=<db-password>

# Cloudinary
CLOUDINARY_CLOUD_NAME=<cloud-name>
CLOUDINARY_API_KEY=<api-key>
CLOUDINARY_API_SECRET=<api-secret>

# Email (SMTP)
MAIL_USERNAME=<your-email>
MAIL_PASSWORD=<app-password>

# Server (optional, defaults to 1011)
PORT=1011
```

> 💡 Tip: Create a local `.env` file for development and never commit it to version control.

### Running Locally

```bash
# 1. Clone the repository
git clone https://github.com/rabbi1067/hotel_bookingFinal.git
cd hotel_bookingFinal/hotel_bookingv5

# 2. Set your environment variables (see above)

# 3. Build the project
mvn clean install

# 4. Run the application
mvn spring-boot:run
```

The app will be available at:
```
http://localhost:1011
```

### Running with Docker

```bash
# Build the image
docker build -t hotel-booking .

# Run the container
docker run -p 8080:8080 \
  -e DATASOURCE_URL=<your-db-url> \
  -e DATASOURCE_USERNAME=<username> \
  -e DATASOURCE_PASSWORD=<password> \
  -e CLOUDINARY_CLOUD_NAME=<cloud-name> \
  -e CLOUDINARY_API_KEY=<api-key> \
  -e CLOUDINARY_API_SECRET=<api-secret> \
  -e MAIL_USERNAME=<email> \
  -e MAIL_PASSWORD=<app-password> \
  hotel-booking
```

---

## ☁️ Deployment

This project is configured for one-click container deployment on **[Render](https://render.com)**.

| Setting | Value |
|---|---|
| **Environment** | Docker |
| **Root Directory** | `hotel_bookingv5` |
| **Dockerfile Path** | `Dockerfile` |
| **Branch** | `main` |

Steps:
1. Connect your GitHub repository to Render
2. Set the Root Directory and Dockerfile Path as shown above
3. Add all required environment variables in the Render dashboard
4. Deploy 🚀

---

## 🏗️ Architecture Overview

```
┌─────────────┐      ┌──────────────────┐      ┌──────────────────┐
│   Browser   │ ───▶ │  Spring Boot App  │ ───▶ │  PostgreSQL (DB)  │
│ (Thymeleaf) │ ◀─── │  (Docker Container)│ ◀─── │    (Supabase)     │
└─────────────┘      └──────────────────┘      └──────────────────┘
                              │
                              ├──────▶ Cloudinary (Image Storage)
                              │
                              └──────▶ SMTP (Email Notifications)
```

---

## 📸 Screenshots

> Add screenshots of your login page, dashboard, and booking flow here once available.

| Login Page | Dashboard | Booking Flow |
|---|---|---|
| *coming soon* | *coming soon* | *coming soon* |

---

## 🗺️ Roadmap

- [ ] Add unit test coverage report
- [ ] Add CI/CD pipeline (GitHub Actions)
- [ ] Add role-based admin analytics dashboard
- [ ] Add multi-language support
- [ ] Add payment gateway integration

---

## 👨‍💻 Author

**Fazle Rabbi**

[![GitHub](https://img.shields.io/badge/GitHub-rabbi1067-181717?style=flat-square&logo=github)](https://github.com/rabbi1067)

---

## 📄 License

This project is developed for **educational purposes**.

---

<div align="center">

⭐ If you find this project useful, consider giving it a star!

</div>
