package pl.nowakowski.business.dao;

import pl.nowakowski.domain.Mechanic;

import java.util.List;
import java.util.Optional;

public interface MechanicDAO {

    List<Mechanic> findAvailable();

    Optional<Mechanic> findByPesel(String pesel);

    Optional<Mechanic> findByUserId(Integer userId);

    void save(Mechanic mechanic);

    void deleteByUserId(Integer userId);
}
