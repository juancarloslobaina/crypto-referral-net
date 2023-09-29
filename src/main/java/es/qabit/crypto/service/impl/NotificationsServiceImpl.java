package es.qabit.crypto.service.impl;

import es.qabit.crypto.domain.Notifications;
import es.qabit.crypto.repository.NotificationsRepository;
import es.qabit.crypto.service.NotificationsService;
import es.qabit.crypto.service.dto.NotificationsDTO;
import es.qabit.crypto.service.mapper.NotificationsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link es.qabit.crypto.domain.Notifications}.
 */
@Service
@Transactional
public class NotificationsServiceImpl implements NotificationsService {

    private final Logger log = LoggerFactory.getLogger(NotificationsServiceImpl.class);

    private final NotificationsRepository notificationsRepository;

    private final NotificationsMapper notificationsMapper;

    public NotificationsServiceImpl(NotificationsRepository notificationsRepository, NotificationsMapper notificationsMapper) {
        this.notificationsRepository = notificationsRepository;
        this.notificationsMapper = notificationsMapper;
    }

    @Override
    public NotificationsDTO save(NotificationsDTO notificationsDTO) {
        log.debug("Request to save Notifications : {}", notificationsDTO);
        Notifications notifications = notificationsMapper.toEntity(notificationsDTO);
        notifications = notificationsRepository.save(notifications);
        return notificationsMapper.toDto(notifications);
    }

    @Override
    public NotificationsDTO update(NotificationsDTO notificationsDTO) {
        log.debug("Request to update Notifications : {}", notificationsDTO);
        Notifications notifications = notificationsMapper.toEntity(notificationsDTO);
        notifications = notificationsRepository.save(notifications);
        return notificationsMapper.toDto(notifications);
    }

    @Override
    public Optional<NotificationsDTO> partialUpdate(NotificationsDTO notificationsDTO) {
        log.debug("Request to partially update Notifications : {}", notificationsDTO);

        return notificationsRepository
            .findById(notificationsDTO.getId())
            .map(existingNotifications -> {
                notificationsMapper.partialUpdate(existingNotifications, notificationsDTO);

                return existingNotifications;
            })
            .map(notificationsRepository::save)
            .map(notificationsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Notifications");
        return notificationsRepository.findAll(pageable).map(notificationsMapper::toDto);
    }

    public Page<NotificationsDTO> findAllWithEagerRelationships(Pageable pageable) {
        return notificationsRepository.findAllWithEagerRelationships(pageable).map(notificationsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationsDTO> findOne(Long id) {
        log.debug("Request to get Notifications : {}", id);
        return notificationsRepository.findOneWithEagerRelationships(id).map(notificationsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Notifications : {}", id);
        notificationsRepository.deleteById(id);
    }
}
