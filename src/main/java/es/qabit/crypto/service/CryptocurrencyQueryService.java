package es.qabit.crypto.service;

import es.qabit.crypto.domain.*; // for static metamodels
import es.qabit.crypto.domain.Cryptocurrency;
import es.qabit.crypto.repository.CryptocurrencyRepository;
import es.qabit.crypto.service.criteria.CryptocurrencyCriteria;
import es.qabit.crypto.service.dto.CryptocurrencyDTO;
import es.qabit.crypto.service.mapper.CryptocurrencyMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Cryptocurrency} entities in the database.
 * The main input is a {@link CryptocurrencyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CryptocurrencyDTO} or a {@link Page} of {@link CryptocurrencyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CryptocurrencyQueryService extends QueryService<Cryptocurrency> {

    private final Logger log = LoggerFactory.getLogger(CryptocurrencyQueryService.class);

    private final CryptocurrencyRepository cryptocurrencyRepository;

    private final CryptocurrencyMapper cryptocurrencyMapper;

    public CryptocurrencyQueryService(CryptocurrencyRepository cryptocurrencyRepository, CryptocurrencyMapper cryptocurrencyMapper) {
        this.cryptocurrencyRepository = cryptocurrencyRepository;
        this.cryptocurrencyMapper = cryptocurrencyMapper;
    }

    /**
     * Return a {@link List} of {@link CryptocurrencyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CryptocurrencyDTO> findByCriteria(CryptocurrencyCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Cryptocurrency> specification = createSpecification(criteria);
        return cryptocurrencyMapper.toDto(cryptocurrencyRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CryptocurrencyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CryptocurrencyDTO> findByCriteria(CryptocurrencyCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Cryptocurrency> specification = createSpecification(criteria);
        return cryptocurrencyRepository.findAll(specification, page).map(cryptocurrencyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CryptocurrencyCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Cryptocurrency> specification = createSpecification(criteria);
        return cryptocurrencyRepository.count(specification);
    }

    /**
     * Function to convert {@link CryptocurrencyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Cryptocurrency> createSpecification(CryptocurrencyCriteria criteria) {
        Specification<Cryptocurrency> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Cryptocurrency_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Cryptocurrency_.name));
            }
            if (criteria.getSymbol() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSymbol(), Cryptocurrency_.symbol));
            }
            if (criteria.getExchangeRate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExchangeRate(), Cryptocurrency_.exchangeRate));
            }
        }
        return specification;
    }
}
