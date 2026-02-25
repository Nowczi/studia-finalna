package pl.nowakowski.infrastructure.database.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.nowakowski.infrastructure.database.entity.SalesmanEntity;

import java.util.Optional;

@Repository
public interface SalesmanJpaRepository extends JpaRepository<SalesmanEntity, Integer> {

    Optional<SalesmanEntity> findByPesel(String pesel);

    Optional<SalesmanEntity> findByUserId(Integer userId);

    void deleteByUserId(Integer userId);
}
