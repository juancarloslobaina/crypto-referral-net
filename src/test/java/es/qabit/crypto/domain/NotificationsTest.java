package es.qabit.crypto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import es.qabit.crypto.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notifications.class);
        Notifications notifications1 = new Notifications();
        notifications1.setId(1L);
        Notifications notifications2 = new Notifications();
        notifications2.setId(notifications1.getId());
        assertThat(notifications1).isEqualTo(notifications2);
        notifications2.setId(2L);
        assertThat(notifications1).isNotEqualTo(notifications2);
        notifications1.setId(null);
        assertThat(notifications1).isNotEqualTo(notifications2);
    }
}
