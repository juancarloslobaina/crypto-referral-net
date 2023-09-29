package es.qabit.crypto.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import es.qabit.crypto.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CryptocurrencyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CryptocurrencyDTO.class);
        CryptocurrencyDTO cryptocurrencyDTO1 = new CryptocurrencyDTO();
        cryptocurrencyDTO1.setId(1L);
        CryptocurrencyDTO cryptocurrencyDTO2 = new CryptocurrencyDTO();
        assertThat(cryptocurrencyDTO1).isNotEqualTo(cryptocurrencyDTO2);
        cryptocurrencyDTO2.setId(cryptocurrencyDTO1.getId());
        assertThat(cryptocurrencyDTO1).isEqualTo(cryptocurrencyDTO2);
        cryptocurrencyDTO2.setId(2L);
        assertThat(cryptocurrencyDTO1).isNotEqualTo(cryptocurrencyDTO2);
        cryptocurrencyDTO1.setId(null);
        assertThat(cryptocurrencyDTO1).isNotEqualTo(cryptocurrencyDTO2);
    }
}
