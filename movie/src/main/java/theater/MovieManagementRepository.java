package theater;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="movieManagements", path="movieManagements")
public interface MovieManagementRepository extends PagingAndSortingRepository<MovieManagement, Long>{


}
