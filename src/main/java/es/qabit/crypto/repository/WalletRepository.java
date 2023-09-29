package es.qabit.crypto.repository;

import es.qabit.crypto.domain.Wallet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Wallet entity.
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long>, JpaSpecificationExecutor<Wallet> {
    @Query("select wallet from Wallet wallet where wallet.user.login = ?#{principal.username}")
    List<Wallet> findByUserIsCurrentUser();

    default Optional<Wallet> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Wallet> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Wallet> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select wallet from Wallet wallet left join fetch wallet.user left join fetch wallet.cryto",
        countQuery = "select count(wallet) from Wallet wallet"
    )
    Page<Wallet> findAllWithToOneRelationships(Pageable pageable);

    @Query("select wallet from Wallet wallet left join fetch wallet.user left join fetch wallet.cryto")
    List<Wallet> findAllWithToOneRelationships();

    @Query("select wallet from Wallet wallet left join fetch wallet.user left join fetch wallet.cryto where wallet.id =:id")
    Optional<Wallet> findOneWithToOneRelationships(@Param("id") Long id);
}
