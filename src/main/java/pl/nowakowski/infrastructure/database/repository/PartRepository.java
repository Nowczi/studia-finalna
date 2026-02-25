package pl.nowakowski.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.nowakowski.business.dao.PartDAO;
import pl.nowakowski.domain.Part;
import pl.nowakowski.infrastructure.database.repository.jpa.PartJpaRepository;
import pl.nowakowski.infrastructure.database.repository.mapper.PartEntityMapper;

import java.util.List;
import java.util.Optional;


@Repository
@AllArgsConstructor
public class PartRepository implements PartDAO {

    private final PartJpaRepository partJpaRepository;
    private final PartEntityMapper mapper;

    @Override
    public List<Part> findAll() {
        return partJpaRepository.findAll().stream()
            .map(mapper::mapFromEntity)
            .toList();
    }

    @Override
    public Optional<Part> findBySerialNumber(String serialNumber) {
        return partJpaRepository.findBySerialNumber(serialNumber)
            .map(mapper::mapFromEntity);
    }
}
