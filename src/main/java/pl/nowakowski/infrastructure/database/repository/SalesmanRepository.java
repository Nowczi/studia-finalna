package pl.nowakowski.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.nowakowski.business.dao.SalesmanDAO;
import pl.nowakowski.domain.Salesman;
import pl.nowakowski.infrastructure.database.entity.SalesmanEntity;
import pl.nowakowski.infrastructure.database.repository.jpa.SalesmanJpaRepository;
import pl.nowakowski.infrastructure.database.repository.mapper.SalesmanEntityMapper;

import java.util.List;
import java.util.Optional;


@Repository
@AllArgsConstructor
public class SalesmanRepository implements SalesmanDAO {

    private final SalesmanJpaRepository salesmanJpaRepository;
    private final SalesmanEntityMapper salesmanEntityMapper;

    @Override
    public List<Salesman> findAvailable() {
        return salesmanJpaRepository.findAll().stream()
            .map(salesmanEntityMapper::mapFromEntity)
            .toList();
    }

    @Override
    public Optional<Salesman> findByPesel(String pesel) {
        return salesmanJpaRepository.findByPesel(pesel)
            .map(salesmanEntityMapper::mapFromEntity);
    }

    @Override
    public Optional<Salesman> findByUserId(Integer userId) {
        return salesmanJpaRepository.findByUserId(userId)
            .map(salesmanEntityMapper::mapFromEntity);
    }

    @Override
    public void save(Salesman salesman) {
        SalesmanEntity entity = salesmanEntityMapper.mapToEntity(salesman);
        salesmanJpaRepository.save(entity);
    }

    @Override
    public void deleteByUserId(Integer userId) {
        salesmanJpaRepository.deleteByUserId(userId);
    }
}
