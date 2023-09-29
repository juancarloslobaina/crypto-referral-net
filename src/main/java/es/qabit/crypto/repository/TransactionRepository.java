package es.qabit.crypto.repository;

import es.qabit.crypto.domain.Transaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Transaction entity.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    @Query("select transaction from Transaction transaction where transaction.userFrom.login = ?#{principal.username}")
    List<Transaction> findByUserFromIsCurrentUser();

    @Query("select transaction from Transaction transaction where transaction.userTo.login = ?#{principal.username}")
    List<Transaction> findByUserToIsCurrentUser();

    default Optional<Transaction> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Transaction> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Transaction> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select transaction from Transaction transaction left join fetch transaction.userFrom left join fetch transaction.userTo left join fetch transaction.crypto",
        countQuery = "select count(transaction) from Transaction transaction"
    )
    Page<Transaction> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select transaction from Transaction transaction left join fetch transaction.userFrom left join fetch transaction.userTo left join fetch transaction.crypto"
    )
    List<Transaction> findAllWithToOneRelationships();

    @Query(
        "select transaction from Transaction transaction left join fetch transaction.userFrom left join fetch transaction.userTo left join fetch transaction.crypto where transaction.id =:id"
    )
    Optional<Transaction> findOneWithToOneRelationships(@Param("id") Long id);
}
