package me.kmuradoff.cloudnest.jpa.repository;

import me.kmuradoff.cloudnest.jpa.model.RefreshToken;
import me.kmuradoff.cloudnest.jpa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    List<RefreshToken> findRefreshTokenByUser_Uuid(UUID userUuid);

    void deleteAllByDeviceIdAndUser_Uuid(String deviceId, UUID userUuid);

    void deleteAllByUser(User user);

    void deleteAllByUser_Uuid(UUID userUuid);
}
