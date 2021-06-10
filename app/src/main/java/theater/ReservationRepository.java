package theater;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel="reservations", path="reservations")
public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long>{
    public Optional<Reservation> findByBookId(String bookId);
    public void deleteByBookId(String bookId);
    public Reservation save(Reservation reservation);
}
