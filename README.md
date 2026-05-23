# CoolLib Server

[![Kotlin CI with Gradle](https://github.com/susui888/CoolLeaf/actions/workflows/ci.yml/badge.svg)](https://github.com/susui888/CoolLeaf/actions/workflows/ci.yml)

<p>
  <!-- Core Engine -->
  <img src="https://img.shields.io/badge/Java-21-ED8B00"/>&nbsp;
  <img src="https://img.shields.io/badge/Kotlin-2.2.21-purple"/>&nbsp;
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.5-green"/>&nbsp;
  <img src="https://img.shields.io/badge/Actuator-Metrics-85EA2D"/>
</p>
<p>
  <!-- Security, Cloud & Storage -->
  <img src="https://img.shields.io/badge/JWT-0.13.0-000000"/>&nbsp;
  <img src="https://img.shields.io/badge/AWS-S3-FF9900"/>&nbsp;
  <img src="https://img.shields.io/badge/PostgreSQL-Runtime-blue"/>&nbsp;
  <img src="https://img.shields.io/badge/SQLite-Runtime-003B57"/>
</p>

The backend foundation of the CoolLib ecosystem — a scalable, stateless RESTful API built with Spring Boot and Kotlin. Designed with Clean Architecture principles, the server powers cross-platform library management, distributed synchronization, authentication, and real-time operational monitoring.

## Ecosystem Links
* [CoolLib Android](https://github.com/susui888/coollib-android) - Jetpack Compose Client
* [CoolLib iOS](https://github.com/susui888/coollib-ios) - SwiftUI & SwiftData Client

## Tech Stack

### Backend

* Language: Kotlin
* Framework: Spring Boot
    * Spring Web
    * Spring Security
    * Spring Data JPA
    * Spring Actuator
* Architecture: Clean Architecture
* Build Tool: Gradle

### Database & Infrastructure

* Database: PostgreSQL
* Containerization: Docker & Docker Compose
* Reverse Proxy: Nginx
* Cloud Edge Layer: Cloudflare Workers
* Edge Storage & Analytics: Cloudflare D1
* Object Storage: Cloudflare R2 (S3-Compatible)

### Authentication

* JWT (JSON Web Tokens)
* Stateless authentication & authorization flow

## Features

### Core System

* Distributed Library Management: Centralized backend serving Android and iOS clients.
* Clean Architecture: Layered separation of domain, application, and infrastructure logic.
* Stateless REST API: JWT-secured endpoints with scalable request handling.
* DTO Projection Optimization: Reduced payload size for efficient mobile communication.
* ISBN Metadata Integration: Automatic retrieval of external book information.
* Cross-Platform Synchronization: Shared backend logic across multiple client platforms.

### Infrastructure & Monitoring

* Real-Time System Monitoring: Live operational metrics exposed through Spring Boot Actuator.
* Edge Metric Replication: Cloudflare Workers aggregate and replicate monitoring data into Cloudflare D1 for lightweight analytics dashboards.
* Containerized Deployment: Fully Dockerized environment with isolated services.
* Reverse Proxy Networking: Nginx-powered internal routing and traffic management.
* CI/CD Integration: Automated GitHub Actions workflow for continuous integration.

## Architecture Highlights

* Stateless backend optimized for horizontal scalability
* Production-oriented layered architecture
* Lightweight edge analytics pipeline using Workers + D1
* Mobile-first API design with optimized serialization
* Docker-based local and production deployment support