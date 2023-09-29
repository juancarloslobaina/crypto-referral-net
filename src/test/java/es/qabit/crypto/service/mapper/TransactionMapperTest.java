package es.qabit.crypto.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class TransactionMapperTest {

    private TransactionMapper transactionMapper;

    @BeforeEach
    public void setUp() {
        transactionMapper = new TransactionMapperImpl();
    }
}
