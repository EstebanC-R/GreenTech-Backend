# 🌱 GreenTech — Backend

Backend de la plataforma **GreenTech**, un sistema orientado al monitoreo ambiental y la gestión de información relacionada con cultivos.

Este repositorio contiene los servicios backend responsables de procesar la información utilizada por la plataforma web y gestionar las diferentes funcionalidades del sistema.

## 📋 Descripción

GreenTech busca integrar información ambiental y agrícola dentro de una plataforma web para facilitar el seguimiento de cultivos y apoyar la toma de decisiones.

El backend proporciona los servicios necesarios para la comunicación con el frontend y la gestión de la información del sistema.

Entre las funcionalidades desarrolladas se encuentran módulos relacionados con:

* 🌱 Cultivos
* 📦 Insumos
* 📊 Reportes
* 📝 Observaciones
* 💡 Recomendaciones
* 👥 Empleados y usuarios
* 📅 Agenda
* 🔐 Autenticación y autorización

## 🏗️ Arquitectura

El sistema sigue una arquitectura cliente-servidor en la que el backend expone servicios para ser consumidos por el frontend.

```text
┌─────────────────────┐
│       Angular       │
│      Frontend       │
└──────────┬──────────┘
           │
           │ HTTP / REST
           ▼
┌─────────────────────┐
│      Spring Boot    │
│       Backend       │
└──────────┬──────────┘
           │
           │ JPA / SQL
           ▼
┌─────────────────────┐
│        MySQL        │
│      Database       │
└─────────────────────┘
```

## 🛠️ Tecnologías

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* REST APIs
* JWT
* MySQL
* SQL
* Maven
* Git / GitHub

## 🔐 Seguridad

La aplicación implementa mecanismos de autenticación y autorización para controlar el acceso a los recursos del sistema.

Se utiliza:

* Spring Security
* JWT
* Control de acceso basado en roles (RBAC)

Los roles permiten diferenciar las funcionalidades disponibles para los diferentes usuarios de la plataforma.

## 📦 Módulos principales

### 🌱 Cultivos

Gestión de la información relacionada con los cultivos registrados en la plataforma.

### 📦 Insumos

Gestión de los insumos utilizados dentro de los procesos agrícolas.

### 📊 Reportes

Servicios destinados a la consulta y generación de información relacionada con el sistema.

### 📝 Observaciones

Registro y gestión de observaciones realizadas durante el seguimiento de los cultivos.

### 💡 Recomendaciones

Gestión de recomendaciones para apoyar el manejo y seguimiento de los cultivos.

### 👥 Usuarios y empleados

Gestión de usuarios y control de acceso mediante roles.

### 📅 Agenda

Gestión de actividades y eventos relacionados con los cultivos.

## 🗄️ Base de datos

El proyecto utiliza **MySQL** como sistema gestor de base de datos.

La estructura de la base de datos se encuentra incluida dentro del repositorio en:

```text
BD sistema_greentech
```

## 📁 Estructura del proyecto

El repositorio contiene los componentes relacionados con el backend y la estructura de base de datos utilizada por GreenTech.

```text
GreenTech-Backend/
│
├── BD sistema_greentech/
│
├── todos_los_cruds/
│
└── ...
```

## 🔗 Frontend

El frontend de la plataforma está desarrollado con Angular y se encuentra en:

https://github.com/EstebanC-R/GreenTech

## 🚀 Ejecución

Clonar el repositorio:

```bash
git clone https://github.com/EstebanC-R/GreenTech-Backend.git
cd GreenTech-Backend
```

Configurar la conexión a la base de datos MySQL según el entorno local.

Posteriormente ejecutar el proyecto Spring Boot desde el IDE o mediante Maven.

## 👨‍💻 Autor

**Esteban Castaño Ramirez**

GitHub: https://github.com/EstebanC-R
