package pl.nowakowski.infrastructure.database.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.nowakowski.infrastructure.database.entity.CarToBuyEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarToBuyJpaRepository extends JpaRepository<CarToBuyEntity, Integer> {

    @Query("""
        SELECT car FROM CarToBuyEntity car
        LEFT JOIN FETCH car.invoice invoice
        WHERE invoice.car.carToBuyId IS NULL
        """)
    List<CarToBuyEntity> findAvailableCars();

    Optional<CarToBuyEntity> findByVin(String vin);

    @Query("""
        SELECT car FROM CarToBuyEntity car
        LEFT JOIN FETCH car.invoice invoice
        WHERE invoice.car.carToBuyId IS NULL
        AND (:brand IS NULL OR LOWER(car.brand) LIKE LOWER(CONCAT('%', :brand, '%')))
        AND (:model IS NULL OR LOWER(car.model) LIKE LOWER(CONCAT('%', :model, '%')))
        AND (:yearFrom IS NULL OR car.year >= :yearFrom)
        AND (:yearTo IS NULL OR car.year <= :yearTo)
        AND (:color IS NULL OR LOWER(car.color) LIKE LOWER(CONCAT('%', :color, '%')))
        AND (:priceFrom IS NULL OR car.price >= :priceFrom)
        AND (:priceTo IS NULL OR car.price <= :priceTo)
        """)
    List<CarToBuyEntity> searchAvailableCars(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("yearFrom") Integer yearFrom,
            @Param("yearTo") Integer yearTo,
            @Param("color") String color,
            @Param("priceFrom") BigDecimal priceFrom,
            @Param("priceTo") BigDecimal priceTo);
}
