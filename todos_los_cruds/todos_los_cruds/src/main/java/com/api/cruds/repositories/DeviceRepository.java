package com.api.cruds.repositories;

import com.api.cruds.models.Device;
import com.api.cruds.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceCode(String deviceCode);
    List<Device> findByUser(UserModel user);
    List<Device> findByUserEmail(String email);
    Optional<Device> findByDeviceCodeAndUser(String deviceCode, UserModel user);
    List<Device> findByUserIsNull(); // Dispositivos no vinculados

    @Query("SELECT d FROM Device d WHERE d.deviceCode = ?1 AND d.user.email = ?2")
    Optional<Device> findByDeviceCodeAndUserEmail(String deviceCode, String userEmail);
}
