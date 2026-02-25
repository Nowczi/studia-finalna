package pl.nowakowski.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.nowakowski.business.dao.CarToBuyDAO;
import pl.nowakowski.domain.CarToBuy;
import pl.nowakowski.infrastructure.database.repository.jpa.CarToBuyJpaRepository;
import pl.nowakowski.infrastructure.database.repository.mapper.CarToBuyEntityMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CarToBuyRepository implements CarToBuyDAO {

    private final CarToBuyJpaRepository carToBuyJpaRepository;
    private final CarToBuyEntityMapper carToBuyEntityMapper;

    @Override
    public List<CarToBuy> findAvailable() {
        return carToBuyJpaRepository.findAvailableCars().stream()
            .map(carToBuyEntityMapper::mapFromEntity)
            .toList();
    }

    @Override
    public Optional<CarToBuy> findCarToBuyByVin(String vin) {
        return carToBuyJpaRepository.findByVin(vin)
            .map(carToBuyEntityMapper::mapFromEntity);
    }
    
    @Override
    public CarToBuy saveCarToBuy(CarToBuy carToBuy) {
        var entityToSave = carToBuyEntityMapper.mapToEntity(carToBuy);
        var savedEntity = carToBuyJpaRepository.save(entityToSave);
        return carToBuyEntityMapper.mapFromEntity(savedEntity);
    }
    
    @Override
    public void deleteCarToBuy(String vin) {
        carToBuyJpaRepository.findByVin(vin).ifPresent(carToBuyJpaRepository::delete);
    }

    @Override
    public List<CarToBuy> searchAvailableCars(String brand, String model, Integer yearFrom, Integer yearTo,
                                               String color, BigDecimal priceFrom, BigDecimal priceTo) {
        return carToBuyJpaRepository.searchAvailableCars(brand, model, yearFrom, yearTo, color, priceFrom, priceTo)
            .stream()
            .map(carToBuyEntityMapper::mapFromEntity)
            .toList();
    }
}
