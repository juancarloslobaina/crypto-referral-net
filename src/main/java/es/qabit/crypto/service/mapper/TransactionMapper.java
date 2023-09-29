package es.qabit.crypto.service.mapper;

import es.qabit.crypto.domain.Cryptocurrency;
import es.qabit.crypto.domain.Transaction;
import es.qabit.crypto.domain.User;
import es.qabit.crypto.service.dto.CryptocurrencyDTO;
import es.qabit.crypto.service.dto.TransactionDTO;
import es.qabit.crypto.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Transaction} and its DTO {@link TransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper extends EntityMapper<TransactionDTO, Transaction> {
    @Mapping(target = "userFrom", source = "userFrom", qualifiedByName = "userLogin")
    @Mapping(target = "userTo", source = "userTo", qualifiedByName = "userLogin")
    @Mapping(target = "crypto", source = "crypto", qualifiedByName = "cryptocurrencySymbol")
    TransactionDTO toDto(Transaction s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("cryptocurrencySymbol")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "symbol", source = "symbol")
    CryptocurrencyDTO toDtoCryptocurrencySymbol(Cryptocurrency cryptocurrency);
}
