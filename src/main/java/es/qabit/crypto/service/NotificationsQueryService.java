package es.qabit.crypto.service;

import es.qabit.crypto.domain.*; // for static metamodels
import es.qabit.crypto.domain.Notifications;
import es.qabit.crypto.repository.NotificationsRepository;
import es.qabit.crypto.service.criteria.NotificationsCriteria;
import es.qabit.crypto.service.dto.NotificationsDTO;
import es.qabit.crypto.service.mapper.NotificationsMapper;
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
 * Service for executing complex queries for {@link Notifications} entities in the database.
 * The main input is a {@link NotificationsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link NotificationsDTO} or a {@link Page} of {@link NotificationsDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NotificationsQueryService extends QueryService<Notifications> {

    private final Logger log = LoggerFactory.getLogger(NotificationsQueryService.class);

    private final NotificationsRepository notificationsRepository;

    private final NotificationsMapper notificationsMapper;

    public NotificationsQueryService(NotificationsRepository notificationsRepository, NotificationsMapper notificationsMapper) {
        this.notificationsRepository = notificationsRepository;
        this.notificationsMapper = notificationsMapper;
    }

    /**
     * Return a {@link List} of {@link NotificationsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<NotificationsDTO> findByCriteria(NotificationsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Notifications> specification = createSpecification(criteria);
        return notificationsMapper.toDto(notificationsRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link NotificationsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<NotificationsDTO> findByCriteria(NotificationsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Notifications> specification = createSpecification(criteria);
        return notificationsRepository.findAll(specification, page).map(notificationsMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NotificationsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Notifications> specification = createSpecification(criteria);
        return notificationsRepository.count(specification);
    }

    /**
     * Function to convert {@link NotificationsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Notifications> createSpecification(NotificationsCriteria criteria) {
        Specification<Notifications> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Notifications_.id));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Notifications_.date));
            }
            if (criteria.getMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMessage(), Notifications_.message));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Notifications_.status));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Notifications_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
