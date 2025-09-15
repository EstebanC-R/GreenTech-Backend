package com.api.cruds.controllers;

import com.api.cruds.dto.DeviceRegistrationRequest;
import com.api.cruds.dto.LinkDeviceRequest;
import com.api.cruds.dto.SensorDataRequest;
import com.api.cruds.models.Device;
import com.api.cruds.models.SensorData;
import com.api.cruds.models.UserModel;
import com.api.cruds.repositories.DeviceRepository;
import com.api.cruds.repositories.IUserRepository;
import com.api.cruds.repositories.SensorDataRepository;
import com.api.cruds.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SensorDeviceController {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String getAdminEfectivo(String userEmail) {
        try {
            String sql = """
                SELECT admin_efectivo 
                FROM vista_usuarios_con_admin 
                WHERE usuario_email = ? AND admin_efectivo IS NOT NULL
                """;

            List<String> result = jdbcTemplate.queryForList(sql, String.class, userEmail);

            if (!result.isEmpty()) {
                String adminEfectivo = result.get(0);
                System.out.println("🔧 DEBUG: Usuario " + userEmail + " -> Admin efectivo: " + adminEfectivo);
                return adminEfectivo;
            } else {
                System.out.println("⚠️ DEBUG: Usuario " + userEmail + " sin admin efectivo");
                return null;
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR obteniendo admin efectivo: " + e.getMessage());
            return null;
        }
    }

    private boolean tieneAccesoADatos(String userEmail) {
        try {
            String sql = """
                SELECT COUNT(*) > 0 
                FROM vista_usuarios_con_admin 
                WHERE usuario_email = ? AND admin_efectivo IS NOT NULL
                """;

            Boolean tieneAcceso = jdbcTemplate.queryForObject(sql, Boolean.class, userEmail);
            System.out.println("🔧 DEBUG: Usuario " + userEmail + " tiene acceso: " + tieneAcceso);
            return tieneAcceso != null && tieneAcceso;
        } catch (Exception e) {
            System.out.println("❌ ERROR verificando acceso: " + e.getMessage());
            return false;
        }
    }

    // 🔧 ENDPOINT CORREGIDO: Registro inicial del dispositivo
    @PostMapping("/sensor-data/register")
    public ResponseEntity<Map<String, Object>> registerDevice(@RequestBody DeviceRegistrationRequest request) {
        System.out.println("=== REGISTER DEVICE DEBUG ===");
        System.out.println("Device Code: " + request.getDeviceCode());
        System.out.println("MAC Address: " + request.getMacAddress());
        System.out.println("Chip ID: " + request.getChipId());
        System.out.println("Device Name: " + request.getDeviceName());

        try {
            Optional<Device> existingDevice = deviceRepository.findByDeviceCode(request.getDeviceCode());
            Device device;

            if (existingDevice.isPresent()) {
                device = existingDevice.get();
                device.setLastSeen(new Date());

                // Actualizar información del dispositivo
                if (request.getMacAddress() != null) {
                    device.setMacAddress(request.getMacAddress());
                }
                if (request.getChipId() != null) {
                    device.setChipId(request.getChipId());
                }
                if (request.getDeviceName() != null) {
                    device.setDeviceName(request.getDeviceName());
                }

                System.out.println("✅ Dispositivo existente actualizado: " + device.getId());
            } else {
                // Crear nuevo dispositivo
                device = new Device();
                device.setDeviceCode(request.getDeviceCode());
                device.setMacAddress(request.getMacAddress());
                device.setChipId(request.getChipId());
                device.setDeviceName(request.getDeviceName() != null ? request.getDeviceName() : "ESP32-" + request.getDeviceCode());
                device.setLastSeen(new Date());
                System.out.println("✅ Creando nuevo dispositivo");
            }

            device = deviceRepository.save(device);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deviceId", device.getId());
            response.put("deviceCode", device.getDeviceCode());
            response.put("message", "Dispositivo registrado correctamente");

            // 🔧 FIX CRÍTICO: Verificar vinculación y devolver token si está vinculado
            if (device.getUser() != null) {
                String token = jwtUtil.generateToken(device.getUser().getEmail());
                response.put("userToken", token);
                response.put("linked", true);
                response.put("status", "LINKED");
                System.out.println("🎉 Dispositivo ya vinculado a: " + device.getUser().getEmail());
                System.out.println("🔑 Token generado y enviado");
            } else {
                response.put("userToken", null);
                response.put("linked", false);
                response.put("status", "PENDING");
                response.put("linkCode", device.getDeviceCode());
                System.out.println("⏳ Dispositivo no vinculado, código: " + device.getDeviceCode());
            }

            System.out.println("📡 Respuesta enviada: " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("❌ ERROR en register: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("linked", false);
            errorResponse.put("userToken", null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 🔧 ENDPOINT MEJORADO: Recibir datos de sensores
    @PostMapping("/sensor-data")
    public ResponseEntity<String> receiveSensorData(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SensorDataRequest request) {

        System.out.println("=== RECEIVING SENSOR DATA ===");
        System.out.println("Device Code: " + request.getDeviceCode());
        System.out.println("Auth Header: " + (authHeader != null ? "Present" : "Missing"));

        // ✅ DEBUG: Mostrar todos los datos recibidos
        System.out.println("=== DATOS RECIBIDOS ===");
        System.out.println("temperaturaAmbiente: " + request.getTemperaturaAmbiente());
        System.out.println("humedadAmbiente: " + request.getHumedadAmbiente());
        System.out.println("temperaturaSuelo: " + request.getTemperaturaSuelo());
        System.out.println("humedadSuelo: " + request.getHumedadSuelo());
        System.out.println("batteryLevel: " + request.getBatteryLevel());
        System.out.println("timestamp: " + request.getTimestamp());
        System.out.println("========================");

        try {
            // Validar token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("❌ Token inválido o faltante");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token requerido");
            }

            String token = authHeader.replace("Bearer ", "");
            String email;

            try {
                email = jwtUtil.getUsernameFromToken(token);
                System.out.println("Email from token: " + email);
            } catch (Exception e) {
                System.out.println("❌ Error extrayendo email del token: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
            }

            Optional<UserModel> userOpt = userRepository.findById(email);
            if (userOpt.isEmpty()) {
                System.out.println("❌ Usuario no encontrado: " + email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
            }

            UserModel user = userOpt.get();
            System.out.println("✅ Usuario encontrado: " + user.getEmail());

            // Verificar que el dispositivo pertenece al usuario
            Optional<Device> deviceOpt = deviceRepository.findByDeviceCodeAndUser(request.getDeviceCode(), user);
            if (deviceOpt.isEmpty()) {
                System.out.println("❌ Dispositivo no autorizado: " + request.getDeviceCode());

                // Debug adicional
                Optional<Device> deviceCheck = deviceRepository.findByDeviceCode(request.getDeviceCode());
                if (deviceCheck.isPresent()) {
                    Device d = deviceCheck.get();
                    System.out.println("Dispositivo existe pero pertenece a: " +
                            (d.getUser() != null ? d.getUser().getEmail() : "Nadie"));
                } else {
                    System.out.println("Dispositivo no existe en la base de datos");
                }

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Dispositivo no autorizado");
            }

            Device device = deviceOpt.get();
            System.out.println("✅ Dispositivo autorizado: " + device.getDeviceName());

            // ✅ VALIDACIÓN DE DATOS MEJORADA
            if (request.getTemperaturaAmbiente() == null) {
                System.out.println("⚠️ temperaturaAmbiente es null, usando valor por defecto");
                request.setTemperaturaAmbiente(25.0f);
            }

            if (request.getHumedadAmbiente() == null) {
                System.out.println("⚠️ humedadAmbiente es null, usando valor por defecto");
                request.setHumedadAmbiente(50.0f);
            }

            if (request.getTemperaturaSuelo() == null) {
                System.out.println("⚠️ temperaturaSuelo es null, usando valor por defecto");
                request.setTemperaturaSuelo(20.0f);
            }

            if (request.getHumedadSuelo() == null) {
                System.out.println("⚠️ humedadSuelo es null, usando valor por defecto");
                request.setHumedadSuelo(30.0f);
            }

            if (request.getBatteryLevel() == null) {
                System.out.println("⚠️ batteryLevel es null, usando valor por defecto");
                request.setBatteryLevel(85.0f);
            }

            // ✅ CREAR DATOS DEL SENSOR CON VALIDACIÓN
            SensorData data = new SensorData();
            data.setDevice(device);
            data.setUser(user);

            // Asegurar que los valores no sean infinitos o NaN
            data.setTemperaturaAmbiente(validateSensorValue(request.getTemperaturaAmbiente(), 25.0f));
            data.setHumedadAmbiente(validateSensorValue(request.getHumedadAmbiente(), 50.0f));
            data.setTemperaturaSuelo(validateSensorValue(request.getTemperaturaSuelo(), 20.0f));
            data.setHumedadSuelo(validateSensorValue(request.getHumedadSuelo(), 30.0f));
            data.setBatteryLevel(validateSensorValue(request.getBatteryLevel(), 85.0f));

            // Establecer timestamp actual
            data.setTimestamp(new Date());

            System.out.println("=== DATOS A GUARDAR ===");
            System.out.println("temperaturaAmbiente: " + data.getTemperaturaAmbiente());
            System.out.println("humedadAmbiente: " + data.getHumedadAmbiente());
            System.out.println("temperaturaSuelo: " + data.getTemperaturaSuelo());
            System.out.println("humedadSuelo: " + data.getHumedadSuelo());
            System.out.println("batteryLevel: " + data.getBatteryLevel());
            System.out.println("========================");

            // Guardar en base de datos
            try {
                data = sensorDataRepository.save(data);
                System.out.println("✅ Datos guardados con ID: " + data.getId());
            } catch (Exception e) {
                System.out.println("❌ Error guardando en BD: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error guardando datos: " + e.getMessage());
            }

            // Actualizar última conexión del dispositivo
            device.setLastSeen(new Date());
            device.setBatteryLevel(data.getBatteryLevel());
            deviceRepository.save(device);

            // Enviar datos en tiempo real solo al usuario propietario
            try {
                messagingTemplate.convertAndSend("/topic/sensor-data/" + user.getEmail(), data);
                System.out.println("📡 Datos enviados por WebSocket a: " + user.getEmail());
            } catch (Exception wsError) {
                System.out.println("⚠️ Error enviando WebSocket: " + wsError.getMessage());
            }

            return ResponseEntity.ok("Datos recibidos y guardados correctamente");

        } catch (Exception e) {
            System.out.println("❌ ERROR procesando datos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }

    private Float validateSensorValue(Float value, Float defaultValue) {
        if (value == null || Float.isNaN(value) || Float.isInfinite(value)) {
            System.out.println("⚠️ Valor de sensor inválido, usando valor por defecto: " + defaultValue);
            return defaultValue;
        }

        // Validar rangos razonables
        if (value < -50 || value > 150) {
            System.out.println("⚠️ Valor fuera de rango (" + value + "), usando valor por defecto: " + defaultValue);
            return defaultValue;
        }

        return value;
    }

    // 🔧 ENDPOINT ACTUALIZADO: Verificar estado del dispositivo
    @GetMapping("/sensor-data/status/{deviceCode}")
    public ResponseEntity<Map<String, Object>> getDeviceStatus(@PathVariable String deviceCode) {
        System.out.println("=== STATUS CHECK DEBUG ===");
        System.out.println("🔧 Checking device: " + deviceCode);

        try {
            Optional<Device> deviceOpt = deviceRepository.findByDeviceCode(deviceCode);

            if (deviceOpt.isEmpty()) {
                System.out.println("❌ Dispositivo no encontrado: " + deviceCode);

                Map<String, Object> notFoundResponse = new HashMap<>();
                notFoundResponse.put("message", "Dispositivo no encontrado");
                notFoundResponse.put("linked", false);
                notFoundResponse.put("userToken", null);
                notFoundResponse.put("status", "NOT_FOUND");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
            }

            Device device = deviceOpt.get();
            Map<String, Object> response = new HashMap<>();

            // Actualizar última actividad
            device.setLastSeen(new Date());
            deviceRepository.save(device);

            if (device.getUser() != null) {
                String token = jwtUtil.generateToken(device.getUser().getEmail());
                response.put("message", "Dispositivo vinculado");
                response.put("linked", true);
                response.put("userToken", token);
                response.put("status", "LINKED");
                response.put("userName", device.getUser().getEmail());
                response.put("deviceName", device.getDeviceName());

                System.out.println("✅ Dispositivo vinculado a: " + device.getUser().getEmail());
                System.out.println("🔑 Token generado: " + (token != null ? "Si" : "No"));
            } else {
                response.put("message", "Dispositivo no vinculado");
                response.put("linked", false);
                response.put("userToken", null);
                response.put("status", "PENDING");
                response.put("deviceCode", device.getDeviceCode());

                System.out.println("⏳ Dispositivo no vinculado");
            }

            System.out.println("📡 Respuesta de status: " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("❌ ERROR en status check: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error interno del servidor");
            errorResponse.put("linked", false);
            errorResponse.put("userToken", null);
            errorResponse.put("status", "ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Vincular dispositivo por código
    @PostMapping("/link-device")
    public ResponseEntity<Map<String, Object>> linkDevice(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> requestBody) {

        System.out.println("=== LINK DEVICE DEBUG ===");
        System.out.println("Authorization header: " + (authHeader != null ? "Present" : "Missing"));
        System.out.println("Request body: " + requestBody);

        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.getUsernameFromToken(token);
            System.out.println("🔧 Usuario extraído del token: " + email);

            Optional<UserModel> userOpt = userRepository.findById(email);
            if (userOpt.isEmpty()) {
                System.out.println("❌ Usuario no encontrado: " + email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            UserModel user = userOpt.get();
            System.out.println("✅ Usuario encontrado: " + user.getEmail());

            String deviceCode = requestBody.get("deviceCode");
            String deviceName = requestBody.get("deviceName");

            System.out.println("🔧 Device Code: " + deviceCode);
            System.out.println("🔧 Device Name: " + deviceName);

            if (deviceCode == null || deviceCode.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Código de dispositivo requerido"));
            }

            if (deviceName == null || deviceName.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Nombre de dispositivo requerido"));
            }

            // 🔧 NUEVO: Buscar en todos los dispositivos para debug
            System.out.println("🔧 DEBUG: Buscando todos los dispositivos en la BD...");
            List<Device> allDevices = deviceRepository.findAll();
            System.out.println("🔧 DEBUG: Total dispositivos en BD: " + allDevices.size());
            for (Device d : allDevices) {
                System.out.println("🔧 DEBUG: Dispositivo encontrado: " + d.getDeviceCode() + " | " + d.getDeviceName());
            }

            // Buscar dispositivo por código (tanto mayúsculas como minúsculas)
            System.out.println("🔧 DEBUG: Buscando dispositivo con código: " + deviceCode.toUpperCase());
            Optional<Device> deviceOpt = deviceRepository.findByDeviceCode(deviceCode.toUpperCase());

            if (deviceOpt.isEmpty()) {
                System.out.println("❌ Dispositivo no encontrado: " + deviceCode);

                // 🔧 NUEVO: Intentar crear el dispositivo si no existe
                System.out.println("🔧 DEBUG: Intentando crear dispositivo automáticamente...");

                Device newDevice = new Device();
                newDevice.setDeviceCode(deviceCode.toUpperCase());
                newDevice.setDeviceName(deviceName);
                newDevice.setUser(user);
                newDevice.setLastSeen(new Date());

                try {
                    newDevice = deviceRepository.save(newDevice);
                    System.out.println("✅ Dispositivo creado automáticamente: " + newDevice.getId());

                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "message", "Dispositivo creado y vinculado correctamente",
                            "device", Map.of(
                                    "id", newDevice.getId(),
                                    "deviceCode", newDevice.getDeviceCode(),
                                    "deviceName", newDevice.getDeviceName(),
                                    "lastSeen", newDevice.getLastSeen()
                            )
                    ));

                } catch (Exception e) {
                    System.out.println("❌ Error creando dispositivo: " + e.getMessage());
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Error creando dispositivo: " + e.getMessage()));
                }
            }

            Device device = deviceOpt.get();
            System.out.println("✅ Dispositivo encontrado: " + device.getId());

            // Verificar que no esté ya vinculado
            if (device.getUser() != null) {
                System.out.println("⚠️ Dispositivo ya vinculado a: " + device.getUser().getEmail());
                if (device.getUser().getEmail().equals(user.getEmail())) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Este dispositivo ya está vinculado a tu cuenta"));
                } else {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Este dispositivo ya está vinculado a otro usuario"));
                }
            }

            // Vincular dispositivo
            device.setUser(user);
            device.setDeviceName(deviceName);
            device.setLastSeen(new Date());
            device = deviceRepository.save(device);

            System.out.println("🎉 Dispositivo vinculado exitosamente");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Dispositivo vinculado correctamente",
                    "device", Map.of(
                            "id", device.getId(),
                            "deviceCode", device.getDeviceCode(),
                            "deviceName", device.getDeviceName(),
                            "lastSeen", device.getLastSeen()
                    )
            ));

        } catch (Exception e) {
            System.out.println("❌ ERROR en link device: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @GetMapping("/user/sensor-data")
    public ResponseEntity<List<SensorData>> getUserSensorData(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "24") int hours,
            @RequestParam(required = false) String deviceCode) {

        System.out.println("=== GET USER SENSOR DATA ===");

        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtUtil.getUsernameFromToken(token);
            System.out.println("🔧 DEBUG: Usuario del token: " + userEmail);

            // ✅ NUEVA LÓGICA: Verificar acceso y obtener admin efectivo
            if (!tieneAccesoADatos(userEmail)) {
                System.out.println("❌ Usuario sin acceso a datos: " + userEmail);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            String adminEfectivo = getAdminEfectivo(userEmail);
            if (adminEfectivo == null) {
                System.out.println("❌ No se pudo obtener admin efectivo para: " + userEmail);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            System.out.println("✅ Buscando datos para admin efectivo: " + adminEfectivo);

            Date since = new Date(System.currentTimeMillis() - (hours * 60 * 60 * 1000L));

            List<SensorData> data;
            if (deviceCode != null) {
                // Buscar por admin efectivo, no por usuario actual
                data = sensorDataRepository.findByUserEmailAndDeviceCodeAndTimestampAfter(
                        adminEfectivo, deviceCode, since);
                System.out.println("🔧 DEBUG: Datos para device " + deviceCode + ": " + data.size());
            } else {
                // Buscar todos los datos del admin efectivo
                data = sensorDataRepository.findByUserEmailAndTimestampAfter(adminEfectivo, since);
                System.out.println("🔧 DEBUG: Todos los datos del admin: " + data.size());
            }

            System.out.println("✅ Retornando " + data.size() + " registros de datos");
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            System.out.println("❌ ERROR en getUserSensorData: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener dispositivos del usuario
    @GetMapping("/user/devices")
    public ResponseEntity<List<Device>> getUserDevices(@RequestHeader("Authorization") String authHeader) {
        System.out.println("=== GET USER DEVICES ===");

        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtUtil.getUsernameFromToken(token);
            System.out.println("🔧 DEBUG: Usuario del token: " + userEmail);

            // ✅ NUEVA LÓGICA: Verificar acceso y obtener admin efectivo
            if (!tieneAccesoADatos(userEmail)) {
                System.out.println("❌ Usuario sin acceso a dispositivos: " + userEmail);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            String adminEfectivo = getAdminEfectivo(userEmail);
            if (adminEfectivo == null) {
                System.out.println("❌ No se pudo obtener admin efectivo para: " + userEmail);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            System.out.println("✅ Buscando dispositivos para admin efectivo: " + adminEfectivo);

            // Buscar dispositivos del admin efectivo, no del usuario actual
            List<Device> devices = deviceRepository.findByUserEmail(adminEfectivo);
            System.out.println("✅ Dispositivos encontrados: " + devices.size());

            return ResponseEntity.ok(devices);

        } catch (Exception e) {
            System.out.println("❌ ERROR en getUserDevices: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🔧 NUEVO ENDPOINT: Obtener últimos datos de un dispositivo específico
    @GetMapping("/sensor-data/latest/{deviceCode}")
    public ResponseEntity<SensorData> getLatestSensorData(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String deviceCode) {

        System.out.println("=== GETTING LATEST DATA ===");
        System.out.println("🔧 Device Code: " + deviceCode);

        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtUtil.getUsernameFromToken(token);
            System.out.println("🔧 DEBUG: Usuario del token: " + userEmail);

            // ✅ NUEVA LÓGICA: Verificar acceso y obtener admin efectivo
            if (!tieneAccesoADatos(userEmail)) {
                System.out.println("❌ Usuario sin acceso: " + userEmail);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            String adminEfectivo = getAdminEfectivo(userEmail);
            if (adminEfectivo == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Verificar que el dispositivo pertenece al admin efectivo
            Optional<Device> deviceOpt = deviceRepository.findByDeviceCodeAndUserEmail(deviceCode, adminEfectivo);
            if (deviceOpt.isEmpty()) {
                System.out.println("❌ Dispositivo no encontrado para admin: " + adminEfectivo);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Obtener el último dato del sensor para el admin efectivo
            Optional<SensorData> latestData = sensorDataRepository
                    .findTopByDeviceDeviceCodeAndUserEmailOrderByTimestampDesc(deviceCode, adminEfectivo);

            if (latestData.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(latestData.get());

        } catch (Exception e) {
            System.out.println("❌ ERROR obteniendo últimos datos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/debug/user-permissions")
    public ResponseEntity<Map<String, Object>> debugUserPermissions(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userEmail = jwtUtil.getUsernameFromToken(token);

            Map<String, Object> debug = new HashMap<>();
            debug.put("userEmail", userEmail);
            debug.put("tieneAcceso", tieneAccesoADatos(userEmail));
            debug.put("adminEfectivo", getAdminEfectivo(userEmail));

            // Consultar datos de la vista directamente
            String sql = "SELECT * FROM vista_usuarios_con_admin WHERE usuario_email = ?";
            List<Map<String, Object>> vistaData = jdbcTemplate.queryForList(sql, userEmail);
            debug.put("datosVista", vistaData);

            return ResponseEntity.ok(debug);

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }



    // Agregar este endpoint temporal para debug
    @GetMapping("/debug/devices")
    public ResponseEntity<Map<String, Object>> debugDevices() {
        try {
            List<Device> allDevices = deviceRepository.findAll();

            Map<String, Object> debug = new HashMap<>();
            debug.put("totalDevices", allDevices.size());
            debug.put("devices", allDevices.stream().map(device -> Map.of(
                    "id", device.getId(),
                    "deviceCode", device.getDeviceCode(),
                    "deviceName", device.getDeviceName(),
                    "userEmail", device.getUser() != null ? device.getUser().getEmail() : "Sin vincular",
                    "lastSeen", device.getLastSeen(),
                    "registeredAt", device.getRegisteredAt()
            )).collect(Collectors.toList()));

            // También verificar usuarios
            List<UserModel> allUsers = userRepository.findAll();
            debug.put("totalUsers", allUsers.size());
            debug.put("users", allUsers.stream().map(user -> Map.of(
                    "email", user.getEmail(),
                    "devicesCount", deviceRepository.findByUserEmail(user.getEmail()).size()
            )).collect(Collectors.toList()));

            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint para forzar registro de dispositivo (temporal)
    @PostMapping("/debug/force-register/{deviceCode}")
    public ResponseEntity<Map<String, Object>> forceRegister(@PathVariable String deviceCode) {
        try {
            Device device = new Device();
            device.setDeviceCode(deviceCode.toUpperCase());
            device.setDeviceName("ESP32-" + deviceCode);
            device.setMacAddress("00:00:00:00:00:00");
            device.setChipId("DEBUG");
            device.setLastSeen(new Date());

            device = deviceRepository.save(device);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Dispositivo registrado forzosamente",
                    "device", Map.of(
                            "id", device.getId(),
                            "deviceCode", device.getDeviceCode(),
                            "deviceName", device.getDeviceName()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }
}