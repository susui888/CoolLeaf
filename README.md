# CoolLib Server &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[![Live Infrastructure Monitor](https://img.shields.io/badge/Live_Infrastructure-Monitor-dc3545)](https://ryansu.uk/analytics/)&nbsp;&nbsp;&nbsp;&nbsp;[![Kotlin CI with Gradle](https://github.com/susui888/CoolLeaf/actions/workflows/ci.yml/badge.svg)](https://github.com/susui888/CoolLeaf/actions/workflows/ci.yml)

<p>
  <img src="https://img.shields.io/badge/Java-21-ED8B00"/>&nbsp;
  <img src="https://img.shields.io/badge/Kotlin-2.x-7F52FF"/>&nbsp;
  <img src="https://img.shields.io/badge/Spring_Boot-4.0-6DB33F"/>&nbsp;
  <img src="https://img.shields.io/badge/JWT-Stateless-000000"/>&nbsp;
</p>

<p>
  <img src="https://img.shields.io/badge/PostgreSQL-18.4-336791"/>&nbsp;
  <img src="https://img.shields.io/badge/Docker-Containerized-2496ED"/>&nbsp;
  <img src="https://img.shields.io/badge/Actuator-Live_Metrics-85EA2D"/>
</p>

Distributed backend platform powering the CoolLib ecosystem — a stateless, edge-aware REST API built with Spring Boot and Kotlin. Designed for cross-platform mobile clients, globally accelerated delivery, JWT-secured authentication, and lightweight telemetry pipelines through Cloudflare infrastructure.

## LOGS
<p><a href="https://ryansu.uk/analytics/explorer/"><img src="https://telemetry-svg.susui888.workers.dev/api/telemetry-logs.svg" alt="System Logs" width="600" /></a></p>

## Ecosystem

* [CoolLib Android](https://github.com/susui888/coollib-android) — Jetpack Compose Client
* [CoolLib iOS](https://github.com/susui888/coollib-ios) — SwiftUI & SwiftData Client

## Tech Stack

### Backend

* Kotlin + Spring Boot
* Spring Security + JWT Authentication
* Spring Data JPA + PostgreSQL
* Spring Boot Actuator
* Clean Architecture
* Gradle Build System

### Infrastructure

* Docker & Docker Compose
* Nginx Reverse Proxy
* Cloudflare Workers
* Cloudflare D1 Analytics
* Cloudflare R2 Object Storage

## Platform Capabilities

* Stateless JWT-secured REST API serving Android and iOS clients
* Edge-accelerated API delivery and telemetry replication through Cloudflare infrastructure
* Cleanly layered backend architecture optimized for scalable deployment
* DTO-based mobile serialization for efficient synchronization and reduced payload size
* ISBN metadata federation for external book information retrieval
* Real-time operational monitoring through Spring Boot Actuator and Workers pipelines

## Architecture Highlights

* Stateless backend optimized for horizontal scalability
* Edge-aware request delivery and lightweight distributed telemetry aggregation
* Mobile-first API design focused on efficient serialization and synchronization
* Fully containerized local and production deployment workflow
