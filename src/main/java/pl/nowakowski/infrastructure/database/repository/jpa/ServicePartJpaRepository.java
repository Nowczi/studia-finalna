package pl.nowakowski.infrastructure.database.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.nowakowski.infrastructure.database.entity.ServicePartEntity;

@Repository
public interface ServicePartJpaRepository extends JpaRepository<ServicePartEntity, Integer> {

}
