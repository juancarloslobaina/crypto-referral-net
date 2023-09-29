package es.qabit.crypto.service;

import es.qabit.crypto.service.dto.CryptocurrencyDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link es.qabit.crypto.domain.Cryptocurrency}.
 */
public interface CryptocurrencyService {
    /**
     * Save a cryptocurrency.
     *
     * @param cryptocurrencyDTO the entity to save.
     * @return the persisted entity.
     */
    CryptocurrencyDTO save(CryptocurrencyDTO cryptocurrencyDTO);

    /**
     * Updates a cryptocurrency.
     *
     * @param cryptocurrencyDTO the entity to update.
     * @return the persisted entity.
     */
    CryptocurrencyDTO update(CryptocurrencyDTO cryptocurrencyDTO);

    /**
     * Partially updates a cryptocurrency.
     *
     * @param cryptocurrencyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CryptocurrencyDTO> partialUpdate(CryptocurrencyDTO cryptocurrencyDTO);

    /**
     * Get all the cryptocurrencies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CryptocurrencyDTO> findAll(Pageable pageable);

    /**
     * Get the "id" cryptocurrency.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CryptocurrencyDTO> findOne(Long id);

    /**
     * Delete the "id" cryptocurrency.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
