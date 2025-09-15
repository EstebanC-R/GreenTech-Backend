package com.api.cruds.repositories;

import com.api.cruds.models.Device;
import com.api.cruds.models.SensorData;
import com.api.cruds.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findTop100ByOrderByTimestampDesc();
    List<SensorData> findByUserOrderByTimestampDesc(UserModel user);
    List<SensorData> findByUserEmailOrderByTimestampDesc(String email);
    List<SensorData> findByDeviceAndTimestampAfter(Device device, Date timestamp);
    List<SensorData> findByUserAndTimestampAfter(UserModel user, Date timestamp);


    List<SensorData> findByUserEmailAndTimestampAfter(String email, Date timestamp);

    @Query("SELECT s FROM SensorData s WHERE s.user.email = :email AND s.device.deviceCode = :deviceCode AND s.timestamp > :since ORDER BY s.timestamp DESC")
    List<SensorData> findByUserEmailAndDeviceCodeAndTimestampAfter(@Param("email") String email,
                                                                   @Param("deviceCode") String deviceCode,
                                                                   @Param("since") Date since);

    @Query("SELECT s FROM SensorData s WHERE s.user.email = :email AND s.device.deviceCode = :deviceCode AND s.timestamp > :since ORDER BY s.timestamp DESC")
    List<SensorData> findByUserEmailAndDeviceCodeSince(@Param("email") String email,
                                                       @Param("deviceCode") String deviceCode,
                                                       @Param("since") Date since);
    Optional<SensorData> findTopByDeviceDeviceCodeAndUserEmailOrderByTimestampDesc(
            String deviceCode, String email);
}
