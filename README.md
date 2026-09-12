# Hotel Booking System

A full-stack hotel booking and management web application built with Spring Boot.

## Features

- User authentication & authorization (Spring Security)
- Room search, booking, and reservation management
- Booking confirmation, checkout, and payment status tracking
- Food & room service ordering
- Guest dashboard with bookings, favorites, and notifications
- Admin dashboard for managing rooms, bookings, and receipts
- Image uploads via Cloudinary
- Email notifications (booking confirmations, alerts)
- Responsive UI built with Thymeleaf

## Tech Stack

- **Backend:** Java, Spring Boot (Web, Data JPA, Security, Validation, Mail, Actuator)
- **Frontend:** Thymeleaf, HTML, CSS, JavaScript
- **Database:** PostgreSQL (hosted on Supabase)
- **Image Storage:** Cloudinary
- **Build Tool:** Maven
- **Deployment:** Docker, Render

## Getting Started

### Prerequisites

- Java 25
- Maven
- PostgreSQL database
- Cloudinary account
- Gmail (or SMTP) account for email service

### Environment Variables

This project reads sensitive configuration from environment variables. Set the following before running:

```
DATASOURCE_URL=jdbc:postgresql://<host>:5432/<db>
DATASOURCE_USERNAME=<db-username>
DATASOURCE_PASSWORD=<db-password>

CLOUDINARY_CLOUD_NAME=<cloud-name>
CLOUDINARY_API_KEY=<api-key>
CLOUDINARY_API_SECRET=<api-secret>

MAIL_USERNAME=<email>
MAIL_PASSWORD=<app-password>
```

### Running Locally

```bash
git clone https://github.com/rabbi1067/hotel_bookingFinal.git
cd hotel_bookingFinal/hotel_bookingv5
mvn clean install
mvn spring-boot:run
```

The app will start on `http://localhost:1011` (or the port set via the `PORT` environment variable).

### Running with Docker

```bash
docker build -t hotel-booking .
docker run -p 8080:8080 --env-file .env hotel-booking
```

## Deployment

This project is configured for deployment on [Render](https://render.com) using Docker.

- **Root Directory:** `hotel_bookingv5`
- **Dockerfile Path:** `Dockerfile`
- Set the environment variables listed above in the Render dashboard.

## Project Structure

```
hotel_bookingv5/
├── src/
│   ├── main/
│   │   ├── java/          # Application source code
│   │   └── resources/     # Templates, static files, configs
│   └── test/               # Unit and integration tests
├── Dockerfile
├── pom.xml
└── README.md
```

## Author

**Fazle Rabbi**
GitHub: [@rabbi1067](https://github.com/rabbi1067)

## License

This project is for educational purposes.
