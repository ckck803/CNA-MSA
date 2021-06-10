package theater;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "approvals", path = "approvals")
public interface ApprovalRepository extends PagingAndSortingRepository<Approval, Long> {

    public void deleteByPayId(String payId);
    public void deleteByBookId(String bookId);

    Optional<Approval> findByBookId(String bookId);
    Optional<Approval> findByPayId(String payId);
}
