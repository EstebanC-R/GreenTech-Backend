# 🌱 GreenTech — Backend

Backend de **GreenTech**, desarrollado con **Java 21 y Spring Boot**, encargado de proporcionar los servicios de la aplicación web para la gestión agrícola, autenticación de usuarios, procesamiento de información de sensores y comunicación en tiempo real.

El proyecto está organizado mediante una arquitectura por capas, separando controladores, servicios, repositorios, modelos, DTOs y componentes de configuración.

## 🛠️ Tecnologías

* **Java 21**
* **Spring Boot 3.3.4**
* Spring Web
* Spring Data JPA
* Spring Security
* Spring WebSocket
* Spring Validation
* **MySQL**
* **JWT**
* Maven
* Lombok
* Resend Java
* Docker
* Docker Compose

## 📋 Funcionalidades

El backend proporciona servicios para diferentes módulos de GreenTech:

* 🔐 Autenticación y autorización
* 👤 Gestión de usuarios
* 🌱 Gestión de cultivos
* 📦 Gestión de insumos
* 👥 Gestión de empleados
* 📝 Gestión de observaciones
* 📅 Gestión de eventos de agenda
* 📊 Datos y dispositivos de sensores
* 📄 Gestión de archivos asociados a empleados
* 🔑 Recuperación y restablecimiento de contraseñas
* 📧 Envío de correos electrónicos
* 🔄 Comunicación mediante WebSocket

## 🏗️ Arquitectura por capas

El proyecto separa las responsabilidades principales de la aplicación:

```text
                    HTTP Request
                         │
                         ▼
                ┌─────────────────┐
                │   Controllers   │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │    Services     │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │   Repositories  │
                └────────┬────────┘
                         │
                         ▼
                    ┌─────────┐
                    │  MySQL  │
                    └─────────┘
```

Los DTOs se utilizan para representar diferentes estructuras de solicitud y respuesta entre la API y los clientes.

## 📁 Organización del código

```text
com/api/cruds/
├── Configuration/
├── controllers/
├── dto/
├── exceptions/
├── models/
├── repositories/
├── services/
└── utils/
```

### Controllers

Contiene los controladores REST y de comunicación:

* `AuthController`
* `CultivoController`
* `InsumosController`
* `EmpleadoController`
* `ObservationController`
* `AgendaEventoController`
* `SensorDeviceController`
* `EmployeeFilesController`
* `WebSocketController`

### Services

Contiene la lógica de aplicación para los diferentes módulos:

* `AuthService`
* `CultivoService`
* `InsumosService`
* `EmpleadoService`
* `ObservationService`
* `AgendaEventoService`
* `PasswordResetService`
* `EmailService`
* `FileStorageServiceImpl`

### Repositories

Contiene los componentes responsables del acceso a los datos mediante Spring Data JPA.

Entre ellos:

* Usuarios
* Cultivos
* Insumos
* Empleados
* Observaciones
* Eventos de agenda
* Dispositivos
* Datos de sensores
* Archivos
* Tokens de recuperación de contraseña

### DTOs

El proyecto utiliza DTOs para separar los objetos utilizados en las peticiones y respuestas de los modelos persistidos.

Algunos ejemplos:

```text
LoginRequest
LoginResponse
CultivoDTO
SensorDataRequest
SensorDataResponse
DeviceStatusResponse
UserProfileResponse
ResetPasswordRequest
ForgotPasswordRequest
AgendaEventoDTO
```

## 🔐 Seguridad

La aplicación utiliza **Spring Security** junto con **JSON Web Tokens (JWT)** para gestionar la autenticación.

El proyecto incluye una utilidad específica:

```text
JwtUtil.java
```

y una configuración de seguridad:

```text
SecurityConfig.java
```

El flujo general de autenticación puede representarse como:

```text
Cliente
   │
   │ Credenciales
   ▼
AuthController
   │
   ▼
AuthService
   │
   ▼
Autenticación
   │
   ▼
JWT
   │
   ▼
Solicitudes protegidas
```

## 🔄 WebSocket

El backend incorpora comunicación mediante **WebSocket**.

La configuración correspondiente se encuentra en:

```text
WebSocketConfig.java
```

y existe un controlador dedicado:

```text
WebSocketController.java
```

El frontend utiliza STOMP.js y SockJS para comunicarse con esta funcionalidad.

## 🌡️ Sensores y dispositivos

El backend contempla estructuras específicas para trabajar con dispositivos y datos de sensores:

```text
Device.java
SensorData.java
```

junto con:

```text
SensorDeviceController.java
SensorDataRepository.java
SensorDataRequest.java
SensorDataResponse.java
DeviceStatusResponse.java
DeviceRegistrationRequest.java
LinkDeviceRequest.java
```

Esto permite separar la gestión de dispositivos de los datos generados por los sensores.

## 📧 Recuperación de contraseña y correo

El backend implementa funcionalidades relacionadas con recuperación de contraseña mediante:

```text
PasswordResetService.java
PasswordResetToken.java
PasswordResetTokenRepository.java
ForgotPasswordRequest.java
ResetPasswordRequest.java
```

También cuenta con:

```text
EmailService.java
```

para las operaciones relacionadas con el envío de correos.

## 📂 Gestión de archivos

El proyecto incorpora almacenamiento y consulta de archivos asociados a empleados.

Entre los componentes relacionados se encuentran:

```text
EmployeeFilesController.java
FileStorageService.java
FileStorageServiceImpl.java
FileInfoDto.java
EpsFile.java
StudiesFile.java
EpsFileRepository.java
StudiesFileRepository.java
```

## ⚠️ Manejo de excepciones

El backend cuenta con un manejador global:

```text
GlobalExceptionHandler.java
```

para centralizar el tratamiento de excepciones de la aplicación.

## 🗄️ Persistencia

La aplicación utiliza **Spring Data JPA** para la interacción con una base de datos **MySQL**.

Los modelos de dominio se encuentran separados de los repositorios y servicios:

```text
models/
repositories/
services/
```

## 🐳 Docker

El proyecto incluye configuración para ejecución mediante Docker:

```text
Dockerfile
docker-compose.yml
.dockerignore
application-docker.yml
```

Esto permite definir un entorno de ejecución separado de la configuración utilizada durante el desarrollo local.

## 🧪 Testing

El proyecto incluye una estructura de pruebas mediante Spring Boot:

```text
src/test/
└── java/
    └── com/api/cruds/
        └── CrudObservacionesApplicationTests.java
```

La configuración de testing se encuentra integrada con el proyecto Maven y Spring Boot.

## ⚙️ Instalación

### Requisitos

* Java 21
* Maven
* MySQL
* Docker *(opcional)*

### Clonar el repositorio

```bash
git clone https://github.com/EstebanC-R/GreenTech-Backend.git
```

### Entrar al proyecto

```bash
cd GreenTech-Backend/todos_los_cruds
```

### Ejecutar con Maven

En Windows:

```bash
mvnw.cmd spring-boot:run
```

En Linux/macOS:

```bash
./mvnw spring-boot:run
```

### Ejecutar con Docker Compose

```bash
docker compose up --build
```

## 🔗 Frontend

El frontend correspondiente se encuentra en:

[GreenTech Frontend](https://github.com/EstebanC-R/GreenTech)

## 📌 Proyecto

GreenTech fue desarrollado como una solución tecnológica para la gestión y monitoreo de información agrícola, integrando una aplicación web Angular con servicios backend desarrollados en Java/Spring Boot, persistencia en MySQL, autenticación mediante JWT y comunicación en tiempo real.
