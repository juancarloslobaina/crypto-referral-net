package es.qabit.crypto.service;

import es.qabit.crypto.domain.*; // for static metamodels
import es.qabit.crypto.domain.Wallet;
import es.qabit.crypto.repository.WalletRepository;
import es.qabit.crypto.service.criteria.WalletCriteria;
import es.qabit.crypto.service.dto.WalletDTO;
import es.qabit.crypto.service.mapper.WalletMapper;
import jakarta.persistence.criteria.JoinType;
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
 * Service for executing complex queries for {@link Wallet} entities in the database.
 * The main input is a {@link WalletCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WalletDTO} or a {@link Page} of {@link WalletDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WalletQueryService extends QueryService<Wallet> {

    private final Logger log = LoggerFactory.getLogger(WalletQueryService.class);

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    public WalletQueryService(WalletRepository walletRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
    }

    /**
     * Return a {@link List} of {@link WalletDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WalletDTO> findByCriteria(WalletCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Wallet> specification = createSpecification(criteria);
        return walletMapper.toDto(walletRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link WalletDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WalletDTO> findByCriteria(WalletCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Wallet> specification = createSpecification(criteria);
        return walletRepository.findAll(specification, page).map(walletMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WalletCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Wallet> specification = createSpecification(criteria);
        return walletRepository.count(specification);
    }

    /**
     * Function to convert {@link WalletCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Wallet> createSpecification(WalletCriteria criteria) {
        Specification<Wallet> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Wallet_.id));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Wallet_.address));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Wallet_.amount));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Wallet_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getCrytoId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCrytoId(), root -> root.join(Wallet_.cryto, JoinType.LEFT).get(Cryptocurrency_.id))
                    );
            }
        }
        return specification;
    }
}
