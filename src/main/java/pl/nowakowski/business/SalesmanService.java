package pl.nowakowski.business;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.nowakowski.business.dao.SalesmanDAO;
import pl.nowakowski.business.dao.UserDAO;
import pl.nowakowski.domain.Salesman;
import pl.nowakowski.domain.User;
import pl.nowakowski.domain.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SalesmanService {

    private final SalesmanDAO salesmanDAO;
    private final UserDAO userDAO;

    @Transactional
    public List<Salesman> findAvailable() {
        List<Salesman> availableSalesmen = salesmanDAO.findAvailable().stream()
                .map(this::enrichWithUserName)
                .toList();
        log.info("Available salesmen: [{}]", availableSalesmen.size());
        return availableSalesmen;
    }

    @Transactional
    public Salesman findSalesman(String pesel) {
        Optional<Salesman> salesman = salesmanDAO.findByPesel(pesel);
        if (salesman.isEmpty()) {
            throw new NotFoundException("Could not find salesman by pesel: [%s]".formatted(pesel));
        }
        return enrichWithUserName(salesman.get());
    }
    
    private Salesman enrichWithUserName(Salesman salesman) {
        if (salesman.getUserId() != null) {
            Optional<User> user = userDAO.findById(salesman.getUserId());
            if (user.isPresent()) {
                return salesman.withUserName(user.get().getUserName());
            }
        }
        return salesman;
    }
}
