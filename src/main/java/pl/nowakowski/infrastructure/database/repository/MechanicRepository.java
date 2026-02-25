package pl.nowakowski.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.nowakowski.business.dao.MechanicDAO;
import pl.nowakowski.domain.Mechanic;
import pl.nowakowski.infrastructure.database.entity.MechanicEntity;
import pl.nowakowski.infrastructure.database.repository.jpa.MechanicJpaRepository;
import pl.nowakowski.infrastructure.database.repository.mapper.MechanicEntityMapper;

import java.util.List;
import java.util.Optional;


@Repository
@AllArgsConstructor
public class MechanicRepository implements MechanicDAO {

    private final MechanicJpaRepository mechanicJpaRepository;
    private final MechanicEntityMapper mechanicEntityMapper;

    @Override
    public List<Mechanic> findAvailable() {
        return mechanicJpaRepository.findAll().stream()
            .map(mechanicEntityMapper::mapFromEntity)
            .toList();
    }

    @Override
    public Optional<Mechanic> findByPesel(String pesel) {
        return mechanicJpaRepository.findByPesel(pesel)
            .map(mechanicEntityMapper::mapFromEntity);
    }

    @Override
    public Optional<Mechanic> findByUserId(Integer userId) {
        return mechanicJpaRepository.findByUserId(userId)
            .map(mechanicEntityMapper::mapFromEntity);
    }

    @Override
    public void save(Mechanic mechanic) {
        MechanicEntity entity = mechanicEntityMapper.mapToEntity(mechanic);
        mechanicJpaRepository.save(entity);
    }

    @Override
    public void deleteByUserId(Integer userId) {
        mechanicJpaRepository.deleteByUserId(userId);
    }
}
