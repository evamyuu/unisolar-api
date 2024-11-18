package unisolar.api.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unisolar.api.domain.entity.Installation;

import java.util.List;

public interface InstallationRepository extends JpaRepository<Installation, Long> {
    List<Installation> findByUserId(Long userId);
    List<Installation> findByStatus(String status);
}