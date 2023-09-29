package es.qabit.crypto.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class CryptocurrencyMapperTest {

    private CryptocurrencyMapper cryptocurrencyMapper;

    @BeforeEach
    public void setUp() {
        cryptocurrencyMapper = new CryptocurrencyMapperImpl();
    }
}
