package es.qabit.crypto.web.rest;

import static es.qabit.crypto.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.qabit.crypto.IntegrationTest;
import es.qabit.crypto.domain.Cryptocurrency;
import es.qabit.crypto.repository.CryptocurrencyRepository;
import es.qabit.crypto.service.dto.CryptocurrencyDTO;
import es.qabit.crypto.service.mapper.CryptocurrencyMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CryptocurrencyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CryptocurrencyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_EXCHANGE_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_EXCHANGE_RATE = new BigDecimal(2);
    private static final BigDecimal SMALLER_EXCHANGE_RATE = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/cryptocurrencies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CryptocurrencyRepository cryptocurrencyRepository;

    @Autowired
    private CryptocurrencyMapper cryptocurrencyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCryptocurrencyMockMvc;

    private Cryptocurrency cryptocurrency;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cryptocurrency createEntity(EntityManager em) {
        Cryptocurrency cryptocurrency = new Cryptocurrency().name(DEFAULT_NAME).symbol(DEFAULT_SYMBOL).exchangeRate(DEFAULT_EXCHANGE_RATE);
        return cryptocurrency;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cryptocurrency createUpdatedEntity(EntityManager em) {
        Cryptocurrency cryptocurrency = new Cryptocurrency().name(UPDATED_NAME).symbol(UPDATED_SYMBOL).exchangeRate(UPDATED_EXCHANGE_RATE);
        return cryptocurrency;
    }

    @BeforeEach
    public void initTest() {
        cryptocurrency = createEntity(em);
    }

    @Test
    @Transactional
    void createCryptocurrency() throws Exception {
        int databaseSizeBeforeCreate = cryptocurrencyRepository.findAll().size();
        // Create the Cryptocurrency
        CryptocurrencyDTO cryptocurrencyDTO = cryptocurrencyMapper.toDto(cryptocurrency);
        restCryptocurrencyMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cryptocurrencyDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Cryptocurrency in the database
        List<Cryptocurrency> cryptocurrencyList = cryptocurrencyRepository.findAll();
        assertThat(cryptocurrencyList).hasSize(databaseSizeBeforeCreate + 1);
        Cryptocurrency testCryptocurrency = cryptocurrencyList.get(cryptocurrencyList.size() - 1);
        assertThat(testCryptocurrency.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCryptocurrency.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testCryptocurrency.getExchangeRate()).isEqualByComparingTo(DEFAULT_EXCHANGE_RATE);
    }

    @Test
    @Transactional
    void createCryptocurrencyWithExistingId() throws Exception {
        // Create the Cryptocurrency with an existing ID
        cryptocurrency.setId(1L);
        CryptocurrencyDTO cryptocurrencyDTO = cryptocurrencyMapper.toDto(cryptocurrency);

        int databaseSizeBeforeCreate = cryptocurrencyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCryptocurrencyMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cryptocurrencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cryptocurrency in the database
        List<Cryptocurrency> cryptocurrencyList = cryptocurrencyRepository.findAll();
        assertThat(cryptocurrencyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCryptocurrencies() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList
        restCryptocurrencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cryptocurrency.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].exchangeRate").value(hasItem(sameNumber(DEFAULT_EXCHANGE_RATE))));
    }

    @Test
    @Transactional
    void getCryptocurrency() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get the cryptocurrency
        restCryptocurrencyMockMvc
            .perform(get(ENTITY_API_URL_ID, cryptocurrency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cryptocurrency.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL))
            .andExpect(jsonPath("$.exchangeRate").value(sameNumber(DEFAULT_EXCHANGE_RATE)));
    }

    @Test
    @Transactional
    void getCryptocurrenciesByIdFiltering() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        Long id = cryptocurrency.getId();

        defaultCryptocurrencyShouldBeFound("id.equals=" + id);
        defaultCryptocurrencyShouldNotBeFound("id.notEquals=" + id);

        defaultCryptocurrencyShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCryptocurrencyShouldNotBeFound("id.greaterThan=" + id);

        defaultCryptocurrencyShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCryptocurrencyShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where name equals to DEFAULT_NAME
        defaultCryptocurrencyShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the cryptocurrencyList where name equals to UPDATED_NAME
        defaultCryptocurrencyShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCryptocurrencyShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the cryptocurrencyList where name equals to UPDATED_NAME
        defaultCryptocurrencyShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where name is not null
        defaultCryptocurrencyShouldBeFound("name.specified=true");

        // Get all the cryptocurrencyList where name is null
        defaultCryptocurrencyShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesByNameContainsSomething() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where name contains DEFAULT_NAME
        defaultCryptocurrencyShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the cryptocurrencyList where name contains UPDATED_NAME
        defaultCryptocurrencyShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where name does not contain DEFAULT_NAME
        defaultCryptocurrencyShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the cryptocurrencyList where name does not contain UPDATED_NAME
        defaultCryptocurrencyShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesBySymbolIsEqualToSomething() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where symbol equals to DEFAULT_SYMBOL
        defaultCryptocurrencyShouldBeFound("symbol.equals=" + DEFAULT_SYMBOL);

        // Get all the cryptocurrencyList where symbol equals to UPDATED_SYMBOL
        defaultCryptocurrencyShouldNotBeFound("symbol.equals=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesBySymbolIsInShouldWork() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where symbol in DEFAULT_SYMBOL or UPDATED_SYMBOL
        defaultCryptocurrencyShouldBeFound("symbol.in=" + DEFAULT_SYMBOL + "," + UPDATED_SYMBOL);

        // Get all the cryptocurrencyList where symbol equals to UPDATED_SYMBOL
        defaultCryptocurrencyShouldNotBeFound("symbol.in=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesBySymbolIsNullOrNotNull() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where symbol is not null
        defaultCryptocurrencyShouldBeFound("symbol.specified=true");

        // Get all the cryptocurrencyList where symbol is null
        defaultCryptocurrencyShouldNotBeFound("symbol.specified=false");
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesBySymbolContainsSomething() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where symbol contains DEFAULT_SYMBOL
        defaultCryptocurrencyShouldBeFound("symbol.contains=" + DEFAULT_SYMBOL);

        // Get all the cryptocurrencyList where symbol contains UPDATED_SYMBOL
        defaultCryptocurrencyShouldNotBeFound("symbol.contains=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesBySymbolNotContainsSomething() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where symbol does not contain DEFAULT_SYMBOL
        defaultCryptocurrencyShouldNotBeFound("symbol.doesNotContain=" + DEFAULT_SYMBOL);

        // Get all the cryptocurrencyList where symbol does not contain UPDATED_SYMBOL
        defaultCryptocurrencyShouldBeFound("symbol.doesNotContain=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesByExchangeRateIsEqualToSomething() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where exchangeRate equals to DEFAULT_EXCHANGE_RATE
        defaultCryptocurrencyShouldBeFound("exchangeRate.equals=" + DEFAULT_EXCHANGE_RATE);

        // Get all the cryptocurrencyList where exchangeRate equals to UPDATED_EXCHANGE_RATE
        defaultCryptocurrencyShouldNotBeFound("exchangeRate.equals=" + UPDATED_EXCHANGE_RATE);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesByExchangeRateIsInShouldWork() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where exchangeRate in DEFAULT_EXCHANGE_RATE or UPDATED_EXCHANGE_RATE
        defaultCryptocurrencyShouldBeFound("exchangeRate.in=" + DEFAULT_EXCHANGE_RATE + "," + UPDATED_EXCHANGE_RATE);

        // Get all the cryptocurrencyList where exchangeRate equals to UPDATED_EXCHANGE_RATE
        defaultCryptocurrencyShouldNotBeFound("exchangeRate.in=" + UPDATED_EXCHANGE_RATE);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesByExchangeRateIsNullOrNotNull() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where exchangeRate is not null
        defaultCryptocurrencyShouldBeFound("exchangeRate.specified=true");

        // Get all the cryptocurrencyList where exchangeRate is null
        defaultCryptocurrencyShouldNotBeFound("exchangeRate.specified=false");
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesByExchangeRateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where exchangeRate is greater than or equal to DEFAULT_EXCHANGE_RATE
        defaultCryptocurrencyShouldBeFound("exchangeRate.greaterThanOrEqual=" + DEFAULT_EXCHANGE_RATE);

        // Get all the cryptocurrencyList where exchangeRate is greater than or equal to UPDATED_EXCHANGE_RATE
        defaultCryptocurrencyShouldNotBeFound("exchangeRate.greaterThanOrEqual=" + UPDATED_EXCHANGE_RATE);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesByExchangeRateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where exchangeRate is less than or equal to DEFAULT_EXCHANGE_RATE
        defaultCryptocurrencyShouldBeFound("exchangeRate.lessThanOrEqual=" + DEFAULT_EXCHANGE_RATE);

        // Get all the cryptocurrencyList where exchangeRate is less than or equal to SMALLER_EXCHANGE_RATE
        defaultCryptocurrencyShouldNotBeFound("exchangeRate.lessThanOrEqual=" + SMALLER_EXCHANGE_RATE);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesByExchangeRateIsLessThanSomething() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where exchangeRate is less than DEFAULT_EXCHANGE_RATE
        defaultCryptocurrencyShouldNotBeFound("exchangeRate.lessThan=" + DEFAULT_EXCHANGE_RATE);

        // Get all the cryptocurrencyList where exchangeRate is less than UPDATED_EXCHANGE_RATE
        defaultCryptocurrencyShouldBeFound("exchangeRate.lessThan=" + UPDATED_EXCHANGE_RATE);
    }

    @Test
    @Transactional
    void getAllCryptocurrenciesByExchangeRateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        // Get all the cryptocurrencyList where exchangeRate is greater than DEFAULT_EXCHANGE_RATE
        defaultCryptocurrencyShouldNotBeFound("exchangeRate.greaterThan=" + DEFAULT_EXCHANGE_RATE);

        // Get all the cryptocurrencyList where exchangeRate is greater than SMALLER_EXCHANGE_RATE
        defaultCryptocurrencyShouldBeFound("exchangeRate.greaterThan=" + SMALLER_EXCHANGE_RATE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCryptocurrencyShouldBeFound(String filter) throws Exception {
        restCryptocurrencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cryptocurrency.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].exchangeRate").value(hasItem(sameNumber(DEFAULT_EXCHANGE_RATE))));

        // Check, that the count call also returns 1
        restCryptocurrencyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCryptocurrencyShouldNotBeFound(String filter) throws Exception {
        restCryptocurrencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCryptocurrencyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCryptocurrency() throws Exception {
        // Get the cryptocurrency
        restCryptocurrencyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCryptocurrency() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        int databaseSizeBeforeUpdate = cryptocurrencyRepository.findAll().size();

        // Update the cryptocurrency
        Cryptocurrency updatedCryptocurrency = cryptocurrencyRepository.findById(cryptocurrency.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCryptocurrency are not directly saved in db
        em.detach(updatedCryptocurrency);
        updatedCryptocurrency.name(UPDATED_NAME).symbol(UPDATED_SYMBOL).exchangeRate(UPDATED_EXCHANGE_RATE);
        CryptocurrencyDTO cryptocurrencyDTO = cryptocurrencyMapper.toDto(updatedCryptocurrency);

        restCryptocurrencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cryptocurrencyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cryptocurrencyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Cryptocurrency in the database
        List<Cryptocurrency> cryptocurrencyList = cryptocurrencyRepository.findAll();
        assertThat(cryptocurrencyList).hasSize(databaseSizeBeforeUpdate);
        Cryptocurrency testCryptocurrency = cryptocurrencyList.get(cryptocurrencyList.size() - 1);
        assertThat(testCryptocurrency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCryptocurrency.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testCryptocurrency.getExchangeRate()).isEqualByComparingTo(UPDATED_EXCHANGE_RATE);
    }

    @Test
    @Transactional
    void putNonExistingCryptocurrency() throws Exception {
        int databaseSizeBeforeUpdate = cryptocurrencyRepository.findAll().size();
        cryptocurrency.setId(count.incrementAndGet());

        // Create the Cryptocurrency
        CryptocurrencyDTO cryptocurrencyDTO = cryptocurrencyMapper.toDto(cryptocurrency);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCryptocurrencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cryptocurrencyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cryptocurrencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cryptocurrency in the database
        List<Cryptocurrency> cryptocurrencyList = cryptocurrencyRepository.findAll();
        assertThat(cryptocurrencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCryptocurrency() throws Exception {
        int databaseSizeBeforeUpdate = cryptocurrencyRepository.findAll().size();
        cryptocurrency.setId(count.incrementAndGet());

        // Create the Cryptocurrency
        CryptocurrencyDTO cryptocurrencyDTO = cryptocurrencyMapper.toDto(cryptocurrency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCryptocurrencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cryptocurrencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cryptocurrency in the database
        List<Cryptocurrency> cryptocurrencyList = cryptocurrencyRepository.findAll();
        assertThat(cryptocurrencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCryptocurrency() throws Exception {
        int databaseSizeBeforeUpdate = cryptocurrencyRepository.findAll().size();
        cryptocurrency.setId(count.incrementAndGet());

        // Create the Cryptocurrency
        CryptocurrencyDTO cryptocurrencyDTO = cryptocurrencyMapper.toDto(cryptocurrency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCryptocurrencyMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cryptocurrencyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cryptocurrency in the database
        List<Cryptocurrency> cryptocurrencyList = cryptocurrencyRepository.findAll();
        assertThat(cryptocurrencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCryptocurrencyWithPatch() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        int databaseSizeBeforeUpdate = cryptocurrencyRepository.findAll().size();

        // Update the cryptocurrency using partial update
        Cryptocurrency partialUpdatedCryptocurrency = new Cryptocurrency();
        partialUpdatedCryptocurrency.setId(cryptocurrency.getId());

        partialUpdatedCryptocurrency.name(UPDATED_NAME);

        restCryptocurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCryptocurrency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCryptocurrency))
            )
            .andExpect(status().isOk());

        // Validate the Cryptocurrency in the database
        List<Cryptocurrency> cryptocurrencyList = cryptocurrencyRepository.findAll();
        assertThat(cryptocurrencyList).hasSize(databaseSizeBeforeUpdate);
        Cryptocurrency testCryptocurrency = cryptocurrencyList.get(cryptocurrencyList.size() - 1);
        assertThat(testCryptocurrency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCryptocurrency.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testCryptocurrency.getExchangeRate()).isEqualByComparingTo(DEFAULT_EXCHANGE_RATE);
    }

    @Test
    @Transactional
    void fullUpdateCryptocurrencyWithPatch() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        int databaseSizeBeforeUpdate = cryptocurrencyRepository.findAll().size();

        // Update the cryptocurrency using partial update
        Cryptocurrency partialUpdatedCryptocurrency = new Cryptocurrency();
        partialUpdatedCryptocurrency.setId(cryptocurrency.getId());

        partialUpdatedCryptocurrency.name(UPDATED_NAME).symbol(UPDATED_SYMBOL).exchangeRate(UPDATED_EXCHANGE_RATE);

        restCryptocurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCryptocurrency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCryptocurrency))
            )
            .andExpect(status().isOk());

        // Validate the Cryptocurrency in the database
        List<Cryptocurrency> cryptocurrencyList = cryptocurrencyRepository.findAll();
        assertThat(cryptocurrencyList).hasSize(databaseSizeBeforeUpdate);
        Cryptocurrency testCryptocurrency = cryptocurrencyList.get(cryptocurrencyList.size() - 1);
        assertThat(testCryptocurrency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCryptocurrency.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testCryptocurrency.getExchangeRate()).isEqualByComparingTo(UPDATED_EXCHANGE_RATE);
    }

    @Test
    @Transactional
    void patchNonExistingCryptocurrency() throws Exception {
        int databaseSizeBeforeUpdate = cryptocurrencyRepository.findAll().size();
        cryptocurrency.setId(count.incrementAndGet());

        // Create the Cryptocurrency
        CryptocurrencyDTO cryptocurrencyDTO = cryptocurrencyMapper.toDto(cryptocurrency);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCryptocurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cryptocurrencyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cryptocurrencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cryptocurrency in the database
        List<Cryptocurrency> cryptocurrencyList = cryptocurrencyRepository.findAll();
        assertThat(cryptocurrencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCryptocurrency() throws Exception {
        int databaseSizeBeforeUpdate = cryptocurrencyRepository.findAll().size();
        cryptocurrency.setId(count.incrementAndGet());

        // Create the Cryptocurrency
        CryptocurrencyDTO cryptocurrencyDTO = cryptocurrencyMapper.toDto(cryptocurrency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCryptocurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cryptocurrencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cryptocurrency in the database
        List<Cryptocurrency> cryptocurrencyList = cryptocurrencyRepository.findAll();
        assertThat(cryptocurrencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCryptocurrency() throws Exception {
        int databaseSizeBeforeUpdate = cryptocurrencyRepository.findAll().size();
        cryptocurrency.setId(count.incrementAndGet());

        // Create the Cryptocurrency
        CryptocurrencyDTO cryptocurrencyDTO = cryptocurrencyMapper.toDto(cryptocurrency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCryptocurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cryptocurrencyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cryptocurrency in the database
        List<Cryptocurrency> cryptocurrencyList = cryptocurrencyRepository.findAll();
        assertThat(cryptocurrencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCryptocurrency() throws Exception {
        // Initialize the database
        cryptocurrencyRepository.saveAndFlush(cryptocurrency);

        int databaseSizeBeforeDelete = cryptocurrencyRepository.findAll().size();

        // Delete the cryptocurrency
        restCryptocurrencyMockMvc
            .perform(delete(ENTITY_API_URL_ID, cryptocurrency.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Cryptocurrency> cryptocurrencyList = cryptocurrencyRepository.findAll();
        assertThat(cryptocurrencyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
