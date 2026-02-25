package pl.nowakowski.business.dao;

import pl.nowakowski.domain.Salesman;

import java.util.List;
import java.util.Optional;

public interface SalesmanDAO {

    Optional<Salesman> findByPesel(String pesel);

    List<Salesman> findAvailable();

    Optional<Salesman> findByUserId(Integer userId);

    void save(Salesman salesman);

    void deleteByUserId(Integer userId);
}
