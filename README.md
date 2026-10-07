# 🛒 NovaMarket - Enterprise E-Commerce Platform

A robust, scalable, and multi-lingual e-commerce web application built with **Java Spring Boot**. This project demonstrates enterprise-level software architecture, focusing on high availability, data integrity, and performance optimization.

## 🚀 Key Architectural Features

* **Master-Replica Database Architecture:** Fully containerized PostgreSQL environment using Docker. Write operations are routed to the Master DB, while read operations are distributed to the Replica DB to prevent bottlenecks.
* **Smart Transaction Routing:** Implemented intelligent data routing using Spring's `@Transactional(readOnly = true)` to seamlessly split GET and POST traffic across the database cluster.
* **In-Memory Data Processing:** Replaced expensive SQL sorting and filtering queries with **Java 8 Stream API** for lightning-fast product search, price sorting, and daily revenue calculations.
* **Internationalization (i18n):** Complete multi-language support ensuring a localized user experience in **English (EN), German (DE), and Turkish (TR)** without compromising database structural integrity.
* **Soft Deletion Strategy:** Safeguarded data consistency in order histories by utilizing logical deletion (`aktifMi = false`) instead of hard database drops.

## 💻 Tech Stack

* **Backend:** Java 17, Spring Boot, Spring Security, Spring Data JPA / Hibernate
* **Database:** PostgreSQL (Master-Replica Cluster via Docker)
* **Frontend:** Thymeleaf, Bootstrap 5, HTML5/CSS3
* **Analytics:** Chart.js for real-time dashboard visualizations
* **Tools:** Maven, Docker, Docker Compose, Git

## ⚙️ Core Modules

1. **Admin Dashboard:** Real-time analytics, daily revenue tracking, and order management.
2. **Product Management (CRUD):** Secure product creation, dynamic multi-language editing, and archive management.
3. **Smart Shopping Cart:** User-specific cart management with real-time stock validation.
4. **Order Processing:** Secure checkout and order tracking with role-based access control.

## 🛠️ How to Run Locally

1. **Start the Database Cluster:**
   Ensure Docker is running, then spin up the Master and Replica PostgreSQL containers:
   ```bash
   docker-compose up -d
