package es.qabit.crypto.web.rest;

import es.qabit.crypto.repository.CryptocurrencyRepository;
import es.qabit.crypto.service.CryptocurrencyQueryService;
import es.qabit.crypto.service.CryptocurrencyService;
import es.qabit.crypto.service.criteria.CryptocurrencyCriteria;
import es.qabit.crypto.service.dto.CryptocurrencyDTO;
import es.qabit.crypto.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link es.qabit.crypto.domain.Cryptocurrency}.
 */
@RestController
@RequestMapping("/api")
public class CryptocurrencyResource {

    private final Logger log = LoggerFactory.getLogger(CryptocurrencyResource.class);

    private static final String ENTITY_NAME = "cryptocurrency";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CryptocurrencyService cryptocurrencyService;

    private final CryptocurrencyRepository cryptocurrencyRepository;

    private final CryptocurrencyQueryService cryptocurrencyQueryService;

    public CryptocurrencyResource(
        CryptocurrencyService cryptocurrencyService,
        CryptocurrencyRepository cryptocurrencyRepository,
        CryptocurrencyQueryService cryptocurrencyQueryService
    ) {
        this.cryptocurrencyService = cryptocurrencyService;
        this.cryptocurrencyRepository = cryptocurrencyRepository;
        this.cryptocurrencyQueryService = cryptocurrencyQueryService;
    }

    /**
     * {@code POST  /cryptocurrencies} : Create a new cryptocurrency.
     *
     * @param cryptocurrencyDTO the cryptocurrencyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cryptocurrencyDTO, or with status {@code 400 (Bad Request)} if the cryptocurrency has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cryptocurrencies")
    public ResponseEntity<CryptocurrencyDTO> createCryptocurrency(@RequestBody CryptocurrencyDTO cryptocurrencyDTO)
        throws URISyntaxException {
        log.debug("REST request to save Cryptocurrency : {}", cryptocurrencyDTO);
        if (cryptocurrencyDTO.getId() != null) {
            throw new BadRequestAlertException("A new cryptocurrency cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CryptocurrencyDTO result = cryptocurrencyService.save(cryptocurrencyDTO);
        return ResponseEntity
            .created(new URI("/api/cryptocurrencies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cryptocurrencies/:id} : Updates an existing cryptocurrency.
     *
     * @param id the id of the cryptocurrencyDTO to save.
     * @param cryptocurrencyDTO the cryptocurrencyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cryptocurrencyDTO,
     * or with status {@code 400 (Bad Request)} if the cryptocurrencyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cryptocurrencyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cryptocurrencies/{id}")
    public ResponseEntity<CryptocurrencyDTO> updateCryptocurrency(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CryptocurrencyDTO cryptocurrencyDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Cryptocurrency : {}, {}", id, cryptocurrencyDTO);
        if (cryptocurrencyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cryptocurrencyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cryptocurrencyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CryptocurrencyDTO result = cryptocurrencyService.update(cryptocurrencyDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cryptocurrencyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /cryptocurrencies/:id} : Partial updates given fields of an existing cryptocurrency, field will ignore if it is null
     *
     * @param id the id of the cryptocurrencyDTO to save.
     * @param cryptocurrencyDTO the cryptocurrencyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cryptocurrencyDTO,
     * or with status {@code 400 (Bad Request)} if the cryptocurrencyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cryptocurrencyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cryptocurrencyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cryptocurrencies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CryptocurrencyDTO> partialUpdateCryptocurrency(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CryptocurrencyDTO cryptocurrencyDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Cryptocurrency partially : {}, {}", id, cryptocurrencyDTO);
        if (cryptocurrencyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cryptocurrencyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cryptocurrencyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CryptocurrencyDTO> result = cryptocurrencyService.partialUpdate(cryptocurrencyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cryptocurrencyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cryptocurrencies} : get all the cryptocurrencies.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cryptocurrencies in body.
     */
    @GetMapping("/cryptocurrencies")
    public ResponseEntity<List<CryptocurrencyDTO>> getAllCryptocurrencies(
        CryptocurrencyCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Cryptocurrencies by criteria: {}", criteria);

        Page<CryptocurrencyDTO> page = cryptocurrencyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cryptocurrencies/count} : count all the cryptocurrencies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/cryptocurrencies/count")
    public ResponseEntity<Long> countCryptocurrencies(CryptocurrencyCriteria criteria) {
        log.debug("REST request to count Cryptocurrencies by criteria: {}", criteria);
        return ResponseEntity.ok().body(cryptocurrencyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cryptocurrencies/:id} : get the "id" cryptocurrency.
     *
     * @param id the id of the cryptocurrencyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cryptocurrencyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cryptocurrencies/{id}")
    public ResponseEntity<CryptocurrencyDTO> getCryptocurrency(@PathVariable Long id) {
        log.debug("REST request to get Cryptocurrency : {}", id);
        Optional<CryptocurrencyDTO> cryptocurrencyDTO = cryptocurrencyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cryptocurrencyDTO);
    }

    /**
     * {@code DELETE  /cryptocurrencies/:id} : delete the "id" cryptocurrency.
     *
     * @param id the id of the cryptocurrencyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cryptocurrencies/{id}")
    public ResponseEntity<Void> deleteCryptocurrency(@PathVariable Long id) {
        log.debug("REST request to delete Cryptocurrency : {}", id);
        cryptocurrencyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
