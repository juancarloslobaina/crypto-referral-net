package es.qabit.crypto.service.impl;

import es.qabit.crypto.domain.Cryptocurrency;
import es.qabit.crypto.repository.CryptocurrencyRepository;
import es.qabit.crypto.service.CryptocurrencyService;
import es.qabit.crypto.service.dto.CryptocurrencyDTO;
import es.qabit.crypto.service.mapper.CryptocurrencyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link es.qabit.crypto.domain.Cryptocurrency}.
 */
@Service
@Transactional
public class CryptocurrencyServiceImpl implements CryptocurrencyService {

    private final Logger log = LoggerFactory.getLogger(CryptocurrencyServiceImpl.class);

    private final CryptocurrencyRepository cryptocurrencyRepository;

    private final CryptocurrencyMapper cryptocurrencyMapper;

    public CryptocurrencyServiceImpl(CryptocurrencyRepository cryptocurrencyRepository, CryptocurrencyMapper cryptocurrencyMapper) {
        this.cryptocurrencyRepository = cryptocurrencyRepository;
        this.cryptocurrencyMapper = cryptocurrencyMapper;
    }

    @Override
    public CryptocurrencyDTO save(CryptocurrencyDTO cryptocurrencyDTO) {
        log.debug("Request to save Cryptocurrency : {}", cryptocurrencyDTO);
        Cryptocurrency cryptocurrency = cryptocurrencyMapper.toEntity(cryptocurrencyDTO);
        cryptocurrency = cryptocurrencyRepository.save(cryptocurrency);
        return cryptocurrencyMapper.toDto(cryptocurrency);
    }

    @Override
    public CryptocurrencyDTO update(CryptocurrencyDTO cryptocurrencyDTO) {
        log.debug("Request to update Cryptocurrency : {}", cryptocurrencyDTO);
        Cryptocurrency cryptocurrency = cryptocurrencyMapper.toEntity(cryptocurrencyDTO);
        cryptocurrency = cryptocurrencyRepository.save(cryptocurrency);
        return cryptocurrencyMapper.toDto(cryptocurrency);
    }

    @Override
    public Optional<CryptocurrencyDTO> partialUpdate(CryptocurrencyDTO cryptocurrencyDTO) {
        log.debug("Request to partially update Cryptocurrency : {}", cryptocurrencyDTO);

        return cryptocurrencyRepository
            .findById(cryptocurrencyDTO.getId())
            .map(existingCryptocurrency -> {
                cryptocurrencyMapper.partialUpdate(existingCryptocurrency, cryptocurrencyDTO);

                return existingCryptocurrency;
            })
            .map(cryptocurrencyRepository::save)
            .map(cryptocurrencyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CryptocurrencyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Cryptocurrencies");
        return cryptocurrencyRepository.findAll(pageable).map(cryptocurrencyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CryptocurrencyDTO> findOne(Long id) {
        log.debug("Request to get Cryptocurrency : {}", id);
        return cryptocurrencyRepository.findById(id).map(cryptocurrencyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Cryptocurrency : {}", id);
        cryptocurrencyRepository.deleteById(id);
    }
}
