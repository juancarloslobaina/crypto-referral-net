package es.qabit.crypto.repository;

import es.qabit.crypto.domain.Notifications;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Notifications entity.
 */
@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long>, JpaSpecificationExecutor<Notifications> {
    @Query("select notifications from Notifications notifications where notifications.user.login = ?#{principal.username}")
    List<Notifications> findByUserIsCurrentUser();

    default Optional<Notifications> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Notifications> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Notifications> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select notifications from Notifications notifications left join fetch notifications.user",
        countQuery = "select count(notifications) from Notifications notifications"
    )
    Page<Notifications> findAllWithToOneRelationships(Pageable pageable);

    @Query("select notifications from Notifications notifications left join fetch notifications.user")
    List<Notifications> findAllWithToOneRelationships();

    @Query("select notifications from Notifications notifications left join fetch notifications.user where notifications.id =:id")
    Optional<Notifications> findOneWithToOneRelationships(@Param("id") Long id);
}
