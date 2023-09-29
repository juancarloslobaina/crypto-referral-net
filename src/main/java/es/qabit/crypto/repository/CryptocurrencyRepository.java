package es.qabit.crypto.repository;

import es.qabit.crypto.domain.Cryptocurrency;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Cryptocurrency entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CryptocurrencyRepository extends JpaRepository<Cryptocurrency, Long>, JpaSpecificationExecutor<Cryptocurrency> {}
