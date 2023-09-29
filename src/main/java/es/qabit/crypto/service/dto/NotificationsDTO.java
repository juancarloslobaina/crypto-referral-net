package es.qabit.crypto.service.dto;

import es.qabit.crypto.domain.enumeration.NotificationStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link es.qabit.crypto.domain.Notifications} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationsDTO implements Serializable {

    private Long id;

    private Instant date;

    private String message;

    private NotificationStatus status;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationsDTO)) {
            return false;
        }

        NotificationsDTO notificationsDTO = (NotificationsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationsDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", message='" + getMessage() + "'" +
            ", status='" + getStatus() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
