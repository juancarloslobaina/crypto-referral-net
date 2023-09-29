package es.qabit.crypto.service.mapper;

import es.qabit.crypto.domain.Notifications;
import es.qabit.crypto.domain.User;
import es.qabit.crypto.service.dto.NotificationsDTO;
import es.qabit.crypto.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notifications} and its DTO {@link NotificationsDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationsMapper extends EntityMapper<NotificationsDTO, Notifications> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    NotificationsDTO toDto(Notifications s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
