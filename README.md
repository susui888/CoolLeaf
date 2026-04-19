# CoolLib Server

[![Kotlin CI with Gradle](https://github.com/susui888/CoolLeaf/actions/workflows/ci.yml/badge.svg)](https://github.com/susui888/CoolLeaf/actions/workflows/ci.yml)

The central brain of the CoolLib ecosystem. A high-performance, stateless RESTful API built with **Spring Boot** and **Kotlin**, providing a robust backbone for cross-platform library management.

## Ecosystem Links
* [CoolLib Android](https://github.com/susui888/coollib-android) - Jetpack Compose Client
* [CoolLib iOS](https://github.com/susui888/coollib-ios) - SwiftUI & SwiftData Client

## Tech Stack
* **Language:** Kotlin
* **Framework:** Spring Boot (Web, Security, Data JPA)
* **Database:** PostgreSQL
* **Auth:** JWT (JSON Web Tokens)
* **Documentation:** Swagger / OpenAPI

## Features
* **Distributed Architecture:** Optimized for scalability and reliability.
* **DTO Projection:** Efficient data transfer with minimal payload size.
* **Stateless REST:** Full JWT-based authentication flow.
* **ISBN Integration:** Core logic for book metadata retrieval.

## Setup
```bash
./gradlew bootRun
