package pl.nowakowski.infrastructure.database.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.nowakowski.infrastructure.database.entity.MechanicEntity;

import java.util.Optional;

@Repository
public interface MechanicJpaRepository extends JpaRepository<MechanicEntity, Integer> {

    Optional<MechanicEntity> findByPesel(String pesel);

    Optional<MechanicEntity> findByUserId(Integer userId);

    void deleteByUserId(Integer userId);
}