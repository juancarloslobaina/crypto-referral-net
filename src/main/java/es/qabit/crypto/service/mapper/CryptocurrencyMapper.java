package es.qabit.crypto.service.mapper;

import es.qabit.crypto.domain.Cryptocurrency;
import es.qabit.crypto.service.dto.CryptocurrencyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cryptocurrency} and its DTO {@link CryptocurrencyDTO}.
 */
@Mapper(componentModel = "spring")
public interface CryptocurrencyMapper extends EntityMapper<CryptocurrencyDTO, Cryptocurrency> {}
