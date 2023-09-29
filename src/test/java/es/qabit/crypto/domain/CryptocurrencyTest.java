package es.qabit.crypto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import es.qabit.crypto.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CryptocurrencyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cryptocurrency.class);
        Cryptocurrency cryptocurrency1 = new Cryptocurrency();
        cryptocurrency1.setId(1L);
        Cryptocurrency cryptocurrency2 = new Cryptocurrency();
        cryptocurrency2.setId(cryptocurrency1.getId());
        assertThat(cryptocurrency1).isEqualTo(cryptocurrency2);
        cryptocurrency2.setId(2L);
        assertThat(cryptocurrency1).isNotEqualTo(cryptocurrency2);
        cryptocurrency1.setId(null);
        assertThat(cryptocurrency1).isNotEqualTo(cryptocurrency2);
    }
}
