# 🌱 GreenTech — Backend

Backend de la plataforma **GreenTech**, desarrollada para apoyar el monitoreo ambiental y la gestión de información relacionada con cultivos.

Este proyecto contiene los servicios backend encargados de procesar solicitudes, gestionar la lógica de negocio, persistir información en MySQL y proporcionar comunicación con el frontend mediante APIs y WebSockets.

---

## 📋 Descripción

GreenTech es una plataforma orientada al seguimiento y gestión de información agrícola y ambiental.

El backend proporciona la estructura necesaria para gestionar diferentes módulos de la plataforma, incluyendo información relacionada con:

* 🌱 Cultivos
* 📦 Insumos
* 📊 Reportes
* 📝 Observaciones
* 💡 Recomendaciones
* 👥 Usuarios
* 📅 Agenda

El proyecto también incorpora mecanismos de autenticación, autorización y validación de información.

---

## 🏗️ Arquitectura

El backend está organizado siguiendo una separación por responsabilidades, utilizando diferentes capas para facilitar el mantenimiento y evolución del código.

```text
src/main/java/com/api/cruds/

├── Configuration
├── controllers
├── dto
├── exceptions
├── models
├── repositories
├── services
└── utils
```

### Principales responsabilidades

**Controllers**

Reciben y gestionan las solicitudes HTTP provenientes del cliente y exponen los endpoints de la aplicación.

**Services**

Contienen la lógica de negocio y coordinan las operaciones realizadas por la aplicación.

**Repositories**

Gestionan el acceso y persistencia de la información mediante Spring Data JPA.

**Models**

Representan las entidades y estructuras principales utilizadas por el sistema.

**DTO**

Permiten definir los objetos utilizados para el intercambio de información entre las diferentes capas de la aplicación.

**Exceptions**

Centralizan el manejo de excepciones y errores de la aplicación.

**Configuration**

Contiene las configuraciones necesarias para el funcionamiento de diferentes componentes del sistema.

**Utils**

Contiene clases y utilidades auxiliares utilizadas por la aplicación.

---

## 🔄 Flujo general

```text
┌─────────────────────┐
│      Angular        │
│      Frontend       │
└──────────┬──────────┘
           │
           │ HTTP / REST
           ▼
┌─────────────────────┐
│     Controllers     │
└──────────┬──────────┘
           ▼
┌─────────────────────┐
│      Services       │
│    Lógica negocio   │
└──────────┬──────────┘
           ▼
┌─────────────────────┐
│    Repositories     │
│    Spring Data JPA  │
└──────────┬──────────┘
           ▼
┌─────────────────────┐
│       MySQL         │
└─────────────────────┘
```

---

## 🛠️ Tecnologías

### Backend

* Java 21
* Spring Boot 3.3.4
* Spring Web
* Spring Data JPA
* Spring Security
* Spring WebSocket
* Spring Validation

### Seguridad

* Spring Security
* JSON Web Tokens (JWT)
* JJWT 0.12.3

### Base de datos

* MySQL
* Hibernate / JPA

### Otras tecnologías y herramientas

* Maven
* Lombok
* Resend Java
* Spring Boot DevTools
* JUnit / Spring Boot Test
* Docker configuration

---

## 🔐 Seguridad y autenticación

El backend utiliza **Spring Security** junto con **JSON Web Tokens (JWT)** para implementar mecanismos de autenticación y autorización.

La integración de JWT se realiza mediante:

```text
jjwt-api
jjwt-impl
jjwt-jackson
```

Esto permite proteger los recursos de la aplicación y controlar el acceso a las funcionalidades que requieren autenticación.

---

## 📡 Comunicación en tiempo real

El proyecto incorpora **Spring WebSocket** para permitir comunicación bidireccional en tiempo real entre el backend y los clientes conectados.

Esto permite utilizar el backend para escenarios donde la información necesita actualizarse sin depender exclusivamente de solicitudes HTTP tradicionales.

---

## 🗄️ Persistencia

La aplicación utiliza **Spring Data JPA** para gestionar la persistencia de información y comunicarse con una base de datos **MySQL**.

La estructura se encuentra organizada mediante:

```text
Models
   ↓
Repositories
   ↓
MySQL
```

Hibernate se utiliza como implementación ORM para facilitar el mapeo entre las entidades Java y las tablas de la base de datos.

---

## ✅ Validación

El proyecto incorpora **Spring Boot Validation** para realizar validaciones sobre los datos recibidos por la aplicación.

Esto permite validar la información antes de procesarla dentro de la lógica de negocio.

---

## 📧 Comunicación por correo

El proyecto incorpora la dependencia **Resend Java**, utilizada para integrar funcionalidades relacionadas con el envío de correos electrónicos desde la aplicación.

---

## 🧪 Pruebas

El proyecto incluye configuración para pruebas mediante:

* Spring Boot Test
* JUnit

Las pruebas se encuentran dentro de:

```text
src/test/java/com/api/cruds
```

---

## 🐳 Docker

El proyecto incluye configuración específica para diferentes entornos mediante:

```text
application.yml
application-docker.yml
.dockerignore
```

Esto permite separar la configuración utilizada durante el desarrollo de aquella destinada a un entorno basado en Docker.

---

## 📁 Estructura del proyecto

```text
todos_los_cruds/
│
├── todos_los_cruds/
│   │
│   ├── .mvn/
│   │   └── wrapper/
│   │
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/api/cruds/
│   │   │   │   ├── Configuration/
│   │   │   │   ├── controllers/
│   │   │   │   ├── dto/
│   │   │   │   ├── exceptions/
│   │   │   │   ├── models/
│   │   │   │   ├── repositories/
│   │   │   │   ├── services/
│   │   │   │   └── utils/
│   │   │   │
│   │   │   └── CrudObservacionesApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-docker.yml
│   │
│   ├── src/test/
│   │
│   ├── .dockerignore
│   └── pom.xml
```

---

## 🚀 Ejecución local

### Requisitos

Antes de ejecutar el proyecto necesitas tener instalado:

* Java 21
* Maven
* MySQL
* Git

### Clonar el repositorio

```bash
git clone https://github.com/EstebanC-R/GreenTech-Backend.git
```

Ingresar al proyecto:

```bash
cd GreenTech-Backend/todos_los_cruds/todos_los_cruds
```

### Configurar la base de datos

Configura las credenciales y parámetros de conexión a MySQL de acuerdo con tu entorno local.

La configuración principal se encuentra en:

```text
src/main/resources/application.yml
```

### Ejecutar

Mediante Maven:

```bash
./mvnw spring-boot:run
```

En Windows:

```bash
mvnw.cmd spring-boot:run
```

---

## 🔗 Frontend

El frontend de GreenTech está desarrollado con Angular y se encuentra en:

https://github.com/EstebanC-R/GreenTech

---

## 👨‍💻 Autor

**Esteban Castaño Ramirez**

GitHub: https://github.com/EstebanC-R
