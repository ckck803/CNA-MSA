package theater;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="movieSeats", path="movieSeats")
public interface MovieSeatRepository extends PagingAndSortingRepository<MovieSeat, Long>{

    MovieSeat findByBookId(String bookId);
}
