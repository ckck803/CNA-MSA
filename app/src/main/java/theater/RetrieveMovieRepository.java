package theater;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RetrieveMovieRepository extends CrudRepository<RetrieveMovie, Long> {


}