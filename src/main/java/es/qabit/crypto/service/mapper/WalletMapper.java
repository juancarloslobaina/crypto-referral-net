package es.qabit.crypto.service.mapper;

import es.qabit.crypto.domain.Cryptocurrency;
import es.qabit.crypto.domain.User;
import es.qabit.crypto.domain.Wallet;
import es.qabit.crypto.service.dto.CryptocurrencyDTO;
import es.qabit.crypto.service.dto.UserDTO;
import es.qabit.crypto.service.dto.WalletDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Wallet} and its DTO {@link WalletDTO}.
 */
@Mapper(componentModel = "spring")
public interface WalletMapper extends EntityMapper<WalletDTO, Wallet> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "cryto", source = "cryto", qualifiedByName = "cryptocurrencySymbol")
    WalletDTO toDto(Wallet s);

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
