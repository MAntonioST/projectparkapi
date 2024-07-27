package com.marcot.projectparkapi.web.dto.mapper;

import com.marcot.projectparkapi.entity.UserAccount;
import com.marcot.projectparkapi.web.dto.UserAccountCreateDto;
import com.marcot.projectparkapi.web.dto.UserAccountResponseDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;


import java.util.List;
import java.util.stream.Collectors;

public class UserAccountMapper {

        public static UserAccount toUser(UserAccountCreateDto createDto) {
            return new ModelMapper().map(createDto, UserAccount.class);
        }

        public static UserAccountResponseDto toDto(UserAccount user) {
            String role = user.getRole().name().substring("ROLE_".length());
            PropertyMap<UserAccount, UserAccountResponseDto> props = new PropertyMap<UserAccount, UserAccountResponseDto>() {
                @Override
                protected void configure() {
                    map().setRole(role);
                }
            };
            ModelMapper mapper = new ModelMapper();
            mapper.addMappings(props);
            return mapper.map(user, UserAccountResponseDto.class);
        }

        public static List<UserAccountResponseDto> toListDto(List<UserAccount> users) {
            return users.stream().map(user -> toDto(user)).collect(Collectors.toList());
        }

}

