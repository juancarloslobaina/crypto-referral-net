package es.qabit.crypto.web.rest;

import es.qabit.crypto.repository.WalletRepository;
import es.qabit.crypto.service.WalletQueryService;
import es.qabit.crypto.service.WalletService;
import es.qabit.crypto.service.criteria.WalletCriteria;
import es.qabit.crypto.service.dto.WalletDTO;
import es.qabit.crypto.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
 * REST controller for managing {@link es.qabit.crypto.domain.Wallet}.
 */
@RestController
@RequestMapping("/api")
public class WalletResource {

    private final Logger log = LoggerFactory.getLogger(WalletResource.class);

    private static final String ENTITY_NAME = "wallet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WalletService walletService;

    private final WalletRepository walletRepository;

    private final WalletQueryService walletQueryService;

    public WalletResource(WalletService walletService, WalletRepository walletRepository, WalletQueryService walletQueryService) {
        this.walletService = walletService;
        this.walletRepository = walletRepository;
        this.walletQueryService = walletQueryService;
    }

    /**
     * {@code POST  /wallets} : Create a new wallet.
     *
     * @param walletDTO the walletDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new walletDTO, or with status {@code 400 (Bad Request)} if the wallet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/wallets")
    public ResponseEntity<WalletDTO> createWallet(@Valid @RequestBody WalletDTO walletDTO) throws URISyntaxException {
        log.debug("REST request to save Wallet : {}", walletDTO);
        if (walletDTO.getId() != null) {
            throw new BadRequestAlertException("A new wallet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WalletDTO result = walletService.save(walletDTO);
        return ResponseEntity
            .created(new URI("/api/wallets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /wallets/:id} : Updates an existing wallet.
     *
     * @param id the id of the walletDTO to save.
     * @param walletDTO the walletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated walletDTO,
     * or with status {@code 400 (Bad Request)} if the walletDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the walletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/wallets/{id}")
    public ResponseEntity<WalletDTO> updateWallet(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WalletDTO walletDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Wallet : {}, {}", id, walletDTO);
        if (walletDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, walletDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!walletRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WalletDTO result = walletService.update(walletDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, walletDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /wallets/:id} : Partial updates given fields of an existing wallet, field will ignore if it is null
     *
     * @param id the id of the walletDTO to save.
     * @param walletDTO the walletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated walletDTO,
     * or with status {@code 400 (Bad Request)} if the walletDTO is not valid,
     * or with status {@code 404 (Not Found)} if the walletDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the walletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/wallets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WalletDTO> partialUpdateWallet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WalletDTO walletDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Wallet partially : {}, {}", id, walletDTO);
        if (walletDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, walletDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!walletRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WalletDTO> result = walletService.partialUpdate(walletDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, walletDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /wallets} : get all the wallets.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wallets in body.
     */
    @GetMapping("/wallets")
    public ResponseEntity<List<WalletDTO>> getAllWallets(
        WalletCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Wallets by criteria: {}", criteria);

        Page<WalletDTO> page = walletQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /wallets/count} : count all the wallets.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/wallets/count")
    public ResponseEntity<Long> countWallets(WalletCriteria criteria) {
        log.debug("REST request to count Wallets by criteria: {}", criteria);
        return ResponseEntity.ok().body(walletQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /wallets/:id} : get the "id" wallet.
     *
     * @param id the id of the walletDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the walletDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/wallets/{id}")
    public ResponseEntity<WalletDTO> getWallet(@PathVariable Long id) {
        log.debug("REST request to get Wallet : {}", id);
        Optional<WalletDTO> walletDTO = walletService.findOne(id);
        return ResponseUtil.wrapOrNotFound(walletDTO);
    }

    /**
     * {@code DELETE  /wallets/:id} : delete the "id" wallet.
     *
     * @param id the id of the walletDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/wallets/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
        log.debug("REST request to delete Wallet : {}", id);
        walletService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
