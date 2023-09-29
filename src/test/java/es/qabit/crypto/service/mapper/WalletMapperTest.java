package es.qabit.crypto.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class WalletMapperTest {

    private WalletMapper walletMapper;

    @BeforeEach
    public void setUp() {
        walletMapper = new WalletMapperImpl();
    }
}
