package pl.nowakowski.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.nowakowski.business.dao.ServiceDAO;
import pl.nowakowski.domain.Service;
import pl.nowakowski.infrastructure.database.repository.jpa.ServiceJpaRepository;
import pl.nowakowski.infrastructure.database.repository.mapper.ServiceEntityMapper;

import java.util.List;
import java.util.Optional;


@Repository
@AllArgsConstructor
public class ServiceRepository implements ServiceDAO {

    private final ServiceJpaRepository serviceJpaRepository;
    private final ServiceEntityMapper mapper;

    @Override
    public List<Service> findAll() {
        return serviceJpaRepository.findAll().stream()
            .map(mapper::mapFromEntity)
            .toList();
    }

    @Override
    public Optional<Service> findByServiceCode(String serviceCode) {
        return serviceJpaRepository.findByServiceCode(serviceCode)
            .map(mapper::mapFromEntity);
    }
}
