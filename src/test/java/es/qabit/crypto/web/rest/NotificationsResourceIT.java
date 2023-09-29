package es.qabit.crypto.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.qabit.crypto.IntegrationTest;
import es.qabit.crypto.domain.Notifications;
import es.qabit.crypto.domain.User;
import es.qabit.crypto.domain.enumeration.NotificationStatus;
import es.qabit.crypto.repository.NotificationsRepository;
import es.qabit.crypto.service.NotificationsService;
import es.qabit.crypto.service.dto.NotificationsDTO;
import es.qabit.crypto.service.mapper.NotificationsMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link NotificationsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class NotificationsResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final NotificationStatus DEFAULT_STATUS = NotificationStatus.TRANSACCIONEXITOSA;
    private static final NotificationStatus UPDATED_STATUS = NotificationStatus.TRANSACCIONFALLIDA;

    private static final String ENTITY_API_URL = "/api/notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Mock
    private NotificationsRepository notificationsRepositoryMock;

    @Autowired
    private NotificationsMapper notificationsMapper;

    @Mock
    private NotificationsService notificationsServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationsMockMvc;

    private Notifications notifications;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notifications createEntity(EntityManager em) {
        Notifications notifications = new Notifications().date(DEFAULT_DATE).message(DEFAULT_MESSAGE).status(DEFAULT_STATUS);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        notifications.setUser(user);
        return notifications;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notifications createUpdatedEntity(EntityManager em) {
        Notifications notifications = new Notifications().date(UPDATED_DATE).message(UPDATED_MESSAGE).status(UPDATED_STATUS);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        notifications.setUser(user);
        return notifications;
    }

    @BeforeEach
    public void initTest() {
        notifications = createEntity(em);
    }

    @Test
    @Transactional
    void createNotifications() throws Exception {
        int databaseSizeBeforeCreate = notificationsRepository.findAll().size();
        // Create the Notifications
        NotificationsDTO notificationsDTO = notificationsMapper.toDto(notifications);
        restNotificationsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeCreate + 1);
        Notifications testNotifications = notificationsList.get(notificationsList.size() - 1);
        assertThat(testNotifications.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testNotifications.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testNotifications.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createNotificationsWithExistingId() throws Exception {
        // Create the Notifications with an existing ID
        notifications.setId(1L);
        NotificationsDTO notificationsDTO = notificationsMapper.toDto(notifications);

        int databaseSizeBeforeCreate = notificationsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllNotifications() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList
        restNotificationsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notifications.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllNotificationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(notificationsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNotificationsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(notificationsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllNotificationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(notificationsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restNotificationsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(notificationsRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getNotifications() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get the notifications
        restNotificationsMockMvc
            .perform(get(ENTITY_API_URL_ID, notifications.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notifications.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNotificationsByIdFiltering() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        Long id = notifications.getId();

        defaultNotificationsShouldBeFound("id.equals=" + id);
        defaultNotificationsShouldNotBeFound("id.notEquals=" + id);

        defaultNotificationsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultNotificationsShouldNotBeFound("id.greaterThan=" + id);

        defaultNotificationsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultNotificationsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNotificationsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where date equals to DEFAULT_DATE
        defaultNotificationsShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the notificationsList where date equals to UPDATED_DATE
        defaultNotificationsShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where date in DEFAULT_DATE or UPDATED_DATE
        defaultNotificationsShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the notificationsList where date equals to UPDATED_DATE
        defaultNotificationsShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllNotificationsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where date is not null
        defaultNotificationsShouldBeFound("date.specified=true");

        // Get all the notificationsList where date is null
        defaultNotificationsShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where message equals to DEFAULT_MESSAGE
        defaultNotificationsShouldBeFound("message.equals=" + DEFAULT_MESSAGE);

        // Get all the notificationsList where message equals to UPDATED_MESSAGE
        defaultNotificationsShouldNotBeFound("message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where message in DEFAULT_MESSAGE or UPDATED_MESSAGE
        defaultNotificationsShouldBeFound("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE);

        // Get all the notificationsList where message equals to UPDATED_MESSAGE
        defaultNotificationsShouldNotBeFound("message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where message is not null
        defaultNotificationsShouldBeFound("message.specified=true");

        // Get all the notificationsList where message is null
        defaultNotificationsShouldNotBeFound("message.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageContainsSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where message contains DEFAULT_MESSAGE
        defaultNotificationsShouldBeFound("message.contains=" + DEFAULT_MESSAGE);

        // Get all the notificationsList where message contains UPDATED_MESSAGE
        defaultNotificationsShouldNotBeFound("message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where message does not contain DEFAULT_MESSAGE
        defaultNotificationsShouldNotBeFound("message.doesNotContain=" + DEFAULT_MESSAGE);

        // Get all the notificationsList where message does not contain UPDATED_MESSAGE
        defaultNotificationsShouldBeFound("message.doesNotContain=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllNotificationsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where status equals to DEFAULT_STATUS
        defaultNotificationsShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the notificationsList where status equals to UPDATED_STATUS
        defaultNotificationsShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllNotificationsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultNotificationsShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the notificationsList where status equals to UPDATED_STATUS
        defaultNotificationsShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllNotificationsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where status is not null
        defaultNotificationsShouldBeFound("status.specified=true");

        // Get all the notificationsList where status is null
        defaultNotificationsShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            notificationsRepository.saveAndFlush(notifications);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        notifications.setUser(user);
        notificationsRepository.saveAndFlush(notifications);
        Long userId = user.getId();
        // Get all the notificationsList where user equals to userId
        defaultNotificationsShouldBeFound("userId.equals=" + userId);

        // Get all the notificationsList where user equals to (userId + 1)
        defaultNotificationsShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationsShouldBeFound(String filter) throws Exception {
        restNotificationsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notifications.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restNotificationsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationsShouldNotBeFound(String filter) throws Exception {
        restNotificationsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNotifications() throws Exception {
        // Get the notifications
        restNotificationsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotifications() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        int databaseSizeBeforeUpdate = notificationsRepository.findAll().size();

        // Update the notifications
        Notifications updatedNotifications = notificationsRepository.findById(notifications.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNotifications are not directly saved in db
        em.detach(updatedNotifications);
        updatedNotifications.date(UPDATED_DATE).message(UPDATED_MESSAGE).status(UPDATED_STATUS);
        NotificationsDTO notificationsDTO = notificationsMapper.toDto(updatedNotifications);

        restNotificationsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeUpdate);
        Notifications testNotifications = notificationsList.get(notificationsList.size() - 1);
        assertThat(testNotifications.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testNotifications.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testNotifications.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingNotifications() throws Exception {
        int databaseSizeBeforeUpdate = notificationsRepository.findAll().size();
        notifications.setId(count.incrementAndGet());

        // Create the Notifications
        NotificationsDTO notificationsDTO = notificationsMapper.toDto(notifications);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotifications() throws Exception {
        int databaseSizeBeforeUpdate = notificationsRepository.findAll().size();
        notifications.setId(count.incrementAndGet());

        // Create the Notifications
        NotificationsDTO notificationsDTO = notificationsMapper.toDto(notifications);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotifications() throws Exception {
        int databaseSizeBeforeUpdate = notificationsRepository.findAll().size();
        notifications.setId(count.incrementAndGet());

        // Create the Notifications
        NotificationsDTO notificationsDTO = notificationsMapper.toDto(notifications);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationsMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNotificationsWithPatch() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        int databaseSizeBeforeUpdate = notificationsRepository.findAll().size();

        // Update the notifications using partial update
        Notifications partialUpdatedNotifications = new Notifications();
        partialUpdatedNotifications.setId(notifications.getId());

        partialUpdatedNotifications.date(UPDATED_DATE).message(UPDATED_MESSAGE).status(UPDATED_STATUS);

        restNotificationsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotifications.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotifications))
            )
            .andExpect(status().isOk());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeUpdate);
        Notifications testNotifications = notificationsList.get(notificationsList.size() - 1);
        assertThat(testNotifications.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testNotifications.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testNotifications.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateNotificationsWithPatch() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        int databaseSizeBeforeUpdate = notificationsRepository.findAll().size();

        // Update the notifications using partial update
        Notifications partialUpdatedNotifications = new Notifications();
        partialUpdatedNotifications.setId(notifications.getId());

        partialUpdatedNotifications.date(UPDATED_DATE).message(UPDATED_MESSAGE).status(UPDATED_STATUS);

        restNotificationsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotifications.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotifications))
            )
            .andExpect(status().isOk());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeUpdate);
        Notifications testNotifications = notificationsList.get(notificationsList.size() - 1);
        assertThat(testNotifications.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testNotifications.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testNotifications.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingNotifications() throws Exception {
        int databaseSizeBeforeUpdate = notificationsRepository.findAll().size();
        notifications.setId(count.incrementAndGet());

        // Create the Notifications
        NotificationsDTO notificationsDTO = notificationsMapper.toDto(notifications);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotifications() throws Exception {
        int databaseSizeBeforeUpdate = notificationsRepository.findAll().size();
        notifications.setId(count.incrementAndGet());

        // Create the Notifications
        NotificationsDTO notificationsDTO = notificationsMapper.toDto(notifications);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotifications() throws Exception {
        int databaseSizeBeforeUpdate = notificationsRepository.findAll().size();
        notifications.setId(count.incrementAndGet());

        // Create the Notifications
        NotificationsDTO notificationsDTO = notificationsMapper.toDto(notifications);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotifications() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        int databaseSizeBeforeDelete = notificationsRepository.findAll().size();

        // Delete the notifications
        restNotificationsMockMvc
            .perform(delete(ENTITY_API_URL_ID, notifications.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
