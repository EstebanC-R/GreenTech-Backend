CREATE DATABASE sistema_greentech;
USE sistema_greentech;

CREATE TABLE IF NOT EXISTS datos_empleados (
    id_empleado       BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario        BIGINT,
    nombre            VARCHAR(50) NOT NULL,
    asignacion        VARCHAR(50),
    cedula            VARCHAR(10) NOT NULL UNIQUE,
    fecha_nacimiento  DATE,
    fecha_de_ingreso  DATE,
    celular           VARCHAR(10) NOT NULL UNIQUE, 
    correo            VARCHAR(100) NOT NULL UNIQUE,
    estado 			  ENUM('Activo', 'Inactivo', 'Suspendido') NOT NULL,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uq_empleado_cedula (cedula),
    UNIQUE KEY uq_empleado_correo (correo),
    UNIQUE KEY uq_empleado_celular (celular)
);

CREATE TABLE IF NOT EXISTS eps_archivos (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id  BIGINT NOT NULL UNIQUE,
    file_name    VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),                                    
    size         BIGINT NOT NULL CHECK (size >= 0),
    data         LONGBLOB NOT NULL,                               
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,   
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_eps_empleado
        FOREIGN KEY (empleado_id)
        REFERENCES datos_empleados(id_empleado)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS estudios_archivos (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id  BIGINT NOT NULL UNIQUE,
    file_name    VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),                                
    size         BIGINT NOT NULL CHECK (size >= 0),
    data         LONGBLOB NOT NULL,                               
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,   
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_estudios_empleado
        FOREIGN KEY (empleado_id)
        REFERENCES datos_empleados(id_empleado)
        ON DELETE CASCADE
);

CREATE TABLE datos_observaciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(125),
    nombre VARCHAR(125),
    descripcion VARCHAR(250),
    fecha_publicacion DATETIME DEFAULT NOW()
);

-- _______________________________CREACION DE USUARIOS______________________________ --
CREATE TABLE ROLES (
    ID_ROL INT PRIMARY KEY AUTO_INCREMENT,
    NOMBRE_ROL VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO ROLES (NOMBRE_ROL) VALUES ('ADMINISTRADOR');
INSERT INTO ROLES (NOMBRE_ROL) VALUES ('EMPLEADO');

CREATE TABLE USUARIOS (
	ID int auto_increment unique,
    ID_USUARIO VARCHAR(100) PRIMARY KEY, 
    FECHA_INGRESO DATE NOT NULL,
    ID_ROL INT NOT NULL,
    PASSWORD_HASH VARCHAR(255) NOT NULL, 
    ESTADO VARCHAR(20) DEFAULT 'ACTIVO',
    ROL VARCHAR(20) DEFAULT 'EMPLEADO',
    admin_asignado VARCHAR(100) NULL,
    fk_usuario_admin VARCHAR(100) NULL,     
    
    FOREIGN KEY (ID_ROL) REFERENCES ROLES(ID_ROL),
    FOREIGN KEY (admin_asignado) REFERENCES USUARIOS(ID_USUARIO),
    FOREIGN KEY (fk_usuario_admin) REFERENCES USUARIOS(ID_USUARIO)
);

DESC USUARIOS;

SELECT 
    CONSTRAINT_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'sistema_greentech' 
  AND TABLE_NAME = 'USUARIOS' 
  AND REFERENCED_TABLE_NAME IS NOT NULL;
  
-- Administrador (la clave es: admin123)
INSERT INTO USUARIOS (ID_USUARIO, FECHA_INGRESO, ID_ROL, PASSWORD_HASH, ROL, ESTADO) 
VALUES ('admin@gmail.com', NOW(), 1, '$2a$12$OfB3BEHLr4myaZ7CjlmO0eKKT1fCZHRpzbNrNu2IsQ8pf2UlmdpR6', 'ADMINISTRADOR','ACTIVO');

INSERT INTO USUARIOS (ID_USUARIO, FECHA_INGRESO, ID_ROL, PASSWORD_HASH, ROL, ESTADO) 
VALUES ('castanoramirezmilton@gmail.com', NOW(), 1, '$2a$12$MohqvFZD2iaG72L8TK3K5OioHnDLZx7xdAMdHvZiFEBH1EUNIV7De', 'ADMINISTRADOR', 'ACTIVO');

-- Empleado SIN asignar a ningún admin (NO VE NADA)
INSERT INTO USUARIOS (ID_USUARIO, FECHA_INGRESO, ID_ROL, PASSWORD_HASH, ROL, admin_asignado, fk_usuario_admin) 
VALUES ('empleado_sin_admin@gmail.com', NOW(), 2, '$2a$12$5sf7QYS9KipWGuRMlCj.fuzYY5PWKKgQzvFuWHfBMIsR/0Jww9eAe', 'EMPLEADO', NULL, NULL);

-- Empleado ASIGNADO al admin (VE TODO LO QUE VE EL ADMIN)
INSERT INTO USUARIOS (ID_USUARIO, FECHA_INGRESO, ID_ROL, PASSWORD_HASH, ROL, admin_asignado, fk_usuario_admin, ESTADO) 
VALUES ('empleado_asignado@gmail.com', NOW(), 2, '$2a$12$ZilYwZ6HVReMiEec3RO71eRwtAyf4442E78BTy1NJQl2ox/WI7/bi', 'EMPLEADO', 'admin@gmail.com', 'admin@gmail.com', 'ACTIVO');

-- Otro empleado también asignado al mismo admin
INSERT INTO USUARIOS (ID_USUARIO, FECHA_INGRESO, ID_ROL, PASSWORD_HASH, ROL, admin_asignado, fk_usuario_admin, ESTADO) 
VALUES ('empleado2_asignado@gmail.com', NOW(), 2, '$2a$12$aTYXy4KGPj49s.HaVTBzlOG6a.vXEXFiKhvoNoro1KqEuK9TtHsCK', 'EMPLEADO', 'admin@gmail.com', 'admin@gmail.com', 'ACTIVO');


CREATE VIEW vista_usuarios_con_admin AS
SELECT 
    u.ID_USUARIO as usuario_email,
    u.ROL as rol_usuario,
    u.ESTADO,
    u.admin_asignado,
    u.fk_usuario_admin,
    CASE 
        WHEN u.ROL = 'ADMINISTRADOR' THEN u.ID_USUARIO
        WHEN u.ROL = 'EMPLEADO' AND u.admin_asignado IS NOT NULL THEN u.admin_asignado
        ELSE NULL
    END as admin_efectivo,
    admin.ID_USUARIO as info_admin_email,
    admin.ROL as info_admin_rol
FROM USUARIOS u
LEFT JOIN USUARIOS admin ON u.admin_asignado = admin.ID_USUARIO
WHERE u.ESTADO = 'ACTIVO';

SELECT * FROM vista_usuarios_con_admin;


-- Consulta: ¿Qué observaciones puede ver empleado_asignado@gmail.com?
SELECT o.* 
FROM datos_observaciones o
WHERE EXISTS (
    SELECT 1 FROM vista_usuarios_con_admin v 
    WHERE v.usuario_email = 'empleado_asignado@gmail.com'
      AND v.admin_efectivo IS NOT NULL
);

SELECT * FROM vista_usuarios_con_admin;
SELECT * FROM USUARIOS WHERE ID_USUARIO LIKE '%admin%' OR ID_USUARIO LIKE '%empleado%';
SELECT 
    u.ID_USUARIO,
    u.ROL,
    u.ESTADO,
    v.admin_efectivo,
    v.usuario_email
FROM USUARIOS u
LEFT JOIN vista_usuarios_con_admin v ON u.ID_USUARIO = v.usuario_email
WHERE u.ID_USUARIO = 'admin@gmail.com';  -- Cambia por el email del token

SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END as tiene_acceso
FROM vista_usuarios_con_admin v 
WHERE v.usuario_email = 'admin@gmail.com'  -- Cambia por el email del token
  AND v.admin_efectivo IS NOT NULL;

SELECT COUNT(*) > 0 as tiene_acceso
FROM vista_usuarios_con_admin v 
WHERE v.usuario_email = 'admin@gmail.com' AND v.admin_efectivo IS NOT NULL;


CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date DATETIME NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_password_reset_usuario
        FOREIGN KEY (email) 
        REFERENCES USUARIOS(ID_USUARIO) 
        ON DELETE CASCADE,
    
    INDEX idx_token (token),
    INDEX idx_email (email),
    INDEX idx_expiry_date (expiry_date),
    INDEX idx_used (used)
);

DESCRIBE password_reset_tokens;

SELECT 
    CONSTRAINT_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'sistema_greentech' 
  AND TABLE_NAME = 'password_reset_tokens' 
  AND REFERENCED_TABLE_NAME IS NOT NULL;
  
  -- ---------------------------------------------------------SENSORES Y DISPOSITIVOS ------------------------------------------------------------------------------
-- NUEVA: Tabla para dispositivos ESP32
CREATE TABLE DEVICES (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_code VARCHAR(8) UNIQUE NOT NULL,        
    mac_address VARCHAR(17) UNIQUE,                   
    chip_id VARCHAR(16),                              
    user_email VARCHAR(100),                          
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    last_seen TIMESTAMP,                              
    active BOOLEAN DEFAULT TRUE,                      
    device_name VARCHAR(100),                         
    battery_level FLOAT,                            
    
    FOREIGN KEY (user_email) REFERENCES USUARIOS(ID_USUARIO),
    
    INDEX idx_device_code (device_code),
    INDEX idx_user_email (user_email),
    INDEX idx_last_seen (last_seen)
);

-- NUEVA: Tabla para datos de sensores
CREATE TABLE SENSOR_DATA (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL,                        
    user_email VARCHAR(100) NOT NULL,                
    temperatura_ambiente FLOAT,                       
    humedad_ambiente FLOAT,                          
    temperatura_suelo FLOAT,                         
    humedad_suelo FLOAT,                            
    battery_level FLOAT,                            
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  
    
    FOREIGN KEY (device_id) REFERENCES DEVICES(id) ON DELETE CASCADE,
    FOREIGN KEY (user_email) REFERENCES USUARIOS(ID_USUARIO),
    
    INDEX idx_device_timestamp (device_id, timestamp),
    INDEX idx_user_timestamp (user_email, timestamp),
    INDEX idx_timestamp (timestamp)
);

-- Vistas útiles para consultas rápidas
CREATE VIEW device_summary AS
SELECT 
    d.id,
    d.device_code,
    d.device_name,
    d.user_email,
    d.active,
    d.battery_level,
    d.last_seen,
    COUNT(sd.id) as total_readings,
    MAX(sd.timestamp) as last_reading
FROM DEVICES d 
LEFT JOIN SENSOR_DATA sd ON d.id = sd.device_id 
GROUP BY d.id;

CREATE VIEW latest_readings AS
SELECT DISTINCT
    d.device_code,
    d.device_name,
    d.user_email,
    FIRST_VALUE(sd.temperatura_ambiente) OVER (PARTITION BY d.id ORDER BY sd.timestamp DESC) as temp_ambiente,
    FIRST_VALUE(sd.humedad_ambiente) OVER (PARTITION BY d.id ORDER BY sd.timestamp DESC) as hum_ambiente,
    FIRST_VALUE(sd.temperatura_suelo) OVER (PARTITION BY d.id ORDER BY sd.timestamp DESC) as temp_suelo,
    FIRST_VALUE(sd.humedad_suelo) OVER (PARTITION BY d.id ORDER BY sd.timestamp DESC) as hum_suelo,
    FIRST_VALUE(sd.timestamp) OVER (PARTITION BY d.id ORDER BY sd.timestamp DESC) as ultimo_dato
FROM DEVICES d
LEFT JOIN SENSOR_DATA sd ON d.id = sd.device_id
WHERE d.active = TRUE;


-- Consulta: ¿Qué dispositivos puede ver empleado_asignado@gmail.com?
SELECT d.*
FROM DEVICES d
JOIN vista_usuarios_con_admin v ON d.user_email = v.admin_efectivo
WHERE v.usuario_email = 'empleado_asignado@gmail.com';

-- Consulta: ¿Qué datos de sensores puede ver empleado_asignado@gmail.com?
SELECT sd.*
FROM SENSOR_DATA sd
JOIN vista_usuarios_con_admin v ON sd.user_email = v.admin_efectivo  
WHERE v.usuario_email = 'empleado_asignado@gmail.com';

-- Consulta: ¿Qué puede ver empleado_sin_admin@gmail.com? (Debería ser nada)
SELECT 'Observaciones' as tipo, COUNT(*) as cantidad
FROM datos_observaciones o
WHERE EXISTS (
    SELECT 1 FROM vista_usuarios_con_admin v 
    WHERE v.usuario_email = 'empleado_sin_admin@gmail.com'
      AND v.admin_efectivo IS NOT NULL
)
UNION ALL
SELECT 'Dispositivos' as tipo, COUNT(*) as cantidad  
FROM DEVICES d
JOIN vista_usuarios_con_admin v ON d.user_email = v.admin_efectivo
WHERE v.usuario_email = 'empleado_sin_admin@gmail.com'
UNION ALL
SELECT 'Datos Sensores' as tipo, COUNT(*) as cantidad
FROM SENSOR_DATA sd
JOIN vista_usuarios_con_admin v ON sd.user_email = v.admin_efectivo  
WHERE v.usuario_email = 'empleado_sin_admin@gmail.com';



-- --------------------------INSUMOS --------------------------->

CREATE TABLE datos_insumos (
    id_insumos INT AUTO_INCREMENT PRIMARY KEY,
    producto VARCHAR(50) NOT NULL,
    -- cultivo en el que se aplicó para futuro
    cantidad_usada DECIMAL(10, 2) NOT NULL,
    unidad_de_medida ENUM('unidades', 'kilogramos', 'gramos', 'litros', 'mililitros') NOT NULL DEFAULT 'litros',
    fecha_de_uso DATE NOT NULL,
    costo DECIMAL(10,2) NOT NULL,
    proveedor VARCHAR(50) NOT NULL,
    user_email VARCHAR(100) NOT NULL,                     -- Usuario propietario (admin)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_insumos_usuario
        FOREIGN KEY (user_email) 
        REFERENCES USUARIOS(ID_USUARIO)
        ON DELETE CASCADE,
    
    INDEX idx_insumos_user_email (user_email),
    INDEX idx_insumos_fecha (fecha_de_uso),
    INDEX idx_insumos_producto (producto),
    INDEX idx_insumos_proveedor (proveedor)
);


SELECT DISTINCT 
    i.id_insumos,
    i.producto,
    i.cantidad_usada,
    i.unidad_de_medida,
    i.fecha_de_uso,
    i.costo,
    i.proveedor,
    i.user_email,
    i.created_at,
    i.updated_at
FROM datos_insumos i
JOIN vista_usuarios_con_admin v ON (
    (v.rol_usuario = 'ADMINISTRADOR' AND i.user_email = v.usuario_email)
    OR
    (v.rol_usuario = 'EMPLEADO' AND i.user_email = v.admin_efectivo)
)
WHERE v.usuario_email = 'admin@gmail.com'
  AND v.admin_efectivo IS NOT NULL
ORDER BY i.fecha_de_uso DESC, i.created_at DESC;

SELECT DISTINCT 
    i.id_insumos,
    i.producto,
    i.cantidad_usada,
    i.unidad_de_medida,
    i.fecha_de_uso,
    i.costo,
    i.proveedor,
    i.user_email
FROM datos_insumos i
JOIN vista_usuarios_con_admin v ON (
    (v.rol_usuario = 'ADMINISTRADOR' AND i.user_email = v.usuario_email)
    OR
    (v.rol_usuario = 'EMPLEADO' AND i.user_email = v.admin_efectivo)
)
WHERE v.usuario_email = 'empleado_asignado@gmail.com'
  AND v.admin_efectivo IS NOT NULL
ORDER BY i.fecha_de_uso DESC;

SELECT 
    v.usuario_email,
    v.rol_usuario,
    v.admin_efectivo,
    CASE 
        WHEN v.admin_efectivo IS NOT NULL THEN 'TIENE PERMISOS'
        ELSE 'SIN PERMISOS'
    END as estado_permisos
FROM vista_usuarios_con_admin v
ORDER BY v.rol_usuario, v.usuario_email;

-- -------------------------------------------------------------------------- TABLA DE CULTIVOS ------------------------------------------------- --


CREATE TABLE CULTIVOS (
    ID_CULTIVO BIGINT AUTO_INCREMENT PRIMARY KEY,
    DEVICE_ID_FK BIGINT,
    USUARIO_RESPONSABLE VARCHAR(100) NOT NULL,
    NOMBRE_CULTIVO VARCHAR(100) NOT NULL,
    TIPO_DE_CULTIVO VARCHAR(50) NOT NULL,
    FECHA_REGISTRO DATE DEFAULT (CURRENT_DATE),
    FECHA_PLANTACION DATE,
    PRODUCCION_ESTIMADA DECIMAL(8,2),
    
    HUMEDAD_SUELO_MIN DECIMAL(5,2),
    HUMEDAD_SUELO_MAX DECIMAL(5,2),
    TEMPERATURA_MIN DECIMAL(5,2),
    TEMPERATURA_MAX DECIMAL(5,2),
    
    ESTADO_CULTIVO ENUM('EN_PROGRESO', 'COSECHADO', 'TERMINADO') DEFAULT 'EN_PROGRESO',
    DESCRIPCION TEXT,
    
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_cultivo_device 
        FOREIGN KEY (DEVICE_ID_FK) 
        REFERENCES DEVICES(id) 
        ON DELETE SET NULL,
        
    CONSTRAINT fk_cultivo_usuario 
        FOREIGN KEY (USUARIO_RESPONSABLE) 
        REFERENCES USUARIOS(ID_USUARIO) 
        ON DELETE CASCADE,
    

    INDEX idx_cultivo_usuario (USUARIO_RESPONSABLE),
    INDEX idx_cultivo_device (DEVICE_ID_FK),
    INDEX idx_cultivo_estado (ESTADO_CULTIVO),
    INDEX idx_cultivo_fechas (FECHA_REGISTRO, FECHA_PLANTACION)
);

-- --------------------------------------------------------------------------CALENDARIO EN PRUEBAAA ------------------------------------------------- --
CREATE TABLE agenda_eventos (
    id_evento BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(100) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_evento DATETIME NOT NULL,
    tipo ENUM('RIEGO', 'FERTILIZACION', 'COSECHA', 'MANTENIMIENTO', 'REUNION', 'OTRO') DEFAULT 'OTRO',
    completado BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_agenda_usuario
        FOREIGN KEY (user_email) 
        REFERENCES USUARIOS(ID_USUARIO)
        ON DELETE CASCADE,
        
    INDEX idx_agenda_usuario (user_email),
    INDEX idx_agenda_fecha (fecha_evento)
);


CREATE VIEW vista_agenda_permisos AS
SELECT DISTINCT 
    ae.id_evento,
    ae.titulo,
    ae.descripcion,
    ae.fecha_evento,
    ae.tipo,
    ae.completado,
    ae.user_email as propietario,
    ae.created_at,
    v.usuario_email as quien_puede_ver
FROM agenda_eventos ae
JOIN vista_usuarios_con_admin v ON (
    (v.rol_usuario = 'ADMINISTRADOR' AND ae.user_email = v.usuario_email)
    OR
    (v.rol_usuario = 'EMPLEADO' AND ae.user_email = v.admin_efectivo)
)
WHERE v.admin_efectivo IS NOT NULL;