package es.qabit.crypto.service;

import es.qabit.crypto.service.dto.NotificationsDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link es.qabit.crypto.domain.Notifications}.
 */
public interface NotificationsService {
    /**
     * Save a notifications.
     *
     * @param notificationsDTO the entity to save.
     * @return the persisted entity.
     */
    NotificationsDTO save(NotificationsDTO notificationsDTO);

    /**
     * Updates a notifications.
     *
     * @param notificationsDTO the entity to update.
     * @return the persisted entity.
     */
    NotificationsDTO update(NotificationsDTO notificationsDTO);

    /**
     * Partially updates a notifications.
     *
     * @param notificationsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NotificationsDTO> partialUpdate(NotificationsDTO notificationsDTO);

    /**
     * Get all the notifications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationsDTO> findAll(Pageable pageable);

    /**
     * Get all the notifications with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationsDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" notifications.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NotificationsDTO> findOne(Long id);

    /**
     * Delete the "id" notifications.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
