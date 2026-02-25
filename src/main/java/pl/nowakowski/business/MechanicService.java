package pl.nowakowski.business;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.nowakowski.business.dao.MechanicDAO;
import pl.nowakowski.business.dao.UserDAO;
import pl.nowakowski.domain.Mechanic;
import pl.nowakowski.domain.User;
import pl.nowakowski.domain.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class MechanicService {

    private final MechanicDAO mechanicDAO;
    private final UserDAO userDAO;

    @Transactional
    public List<Mechanic> findAvailable() {
        List<Mechanic> availableMechanics = mechanicDAO.findAvailable().stream()
                .map(this::enrichWithUserName)
                .toList();
        log.info("Available mechanics: [{}]", availableMechanics.size());
        return availableMechanics;
    }

    @Transactional
    public Mechanic findMechanic(String pesel) {
        Optional<Mechanic> mechanic = mechanicDAO.findByPesel(pesel);
        if (mechanic.isEmpty()) {
            throw new NotFoundException("Could not find mechanic by pesel: [%s]".formatted(pesel));
        }
        return enrichWithUserName(mechanic.get());
    }
    
    private Mechanic enrichWithUserName(Mechanic mechanic) {
        if (mechanic.getUserId() != null) {
            Optional<User> user = userDAO.findById(mechanic.getUserId());
            if (user.isPresent()) {
                return mechanic.withUserName(user.get().getUserName());
            }
        }
        return mechanic;
    }
}
