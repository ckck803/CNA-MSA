package theater;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="movies", path="movies")
public interface MovieRepository extends PagingAndSortingRepository<Movie, Long>{

    Movie findByMovieId(String movieId);
}
