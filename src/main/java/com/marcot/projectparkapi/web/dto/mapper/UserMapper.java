package com.marcot.projectparkapi.web.dto.mapper;

import com.marcot.projectparkapi.entity.UserEntity;
import com.marcot.projectparkapi.web.dto.UserCreateDto;
import com.marcot.projectparkapi.web.dto.UserResponseDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;


import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

        public static UserEntity toUser(UserCreateDto createDto) {
            return new ModelMapper().map(createDto, UserEntity.class);
        }

        public static UserResponseDto toDto(UserEntity user) {
            String role = user.getRole().name().substring("ROLE_".length());
            PropertyMap<UserEntity, UserResponseDto> props = new PropertyMap<UserEntity, UserResponseDto>() {
                @Override
                protected void configure() {
                    map().setRole(role);
                }
            };
            ModelMapper mapper = new ModelMapper();
            mapper.addMappings(props);
            return mapper.map(user, UserResponseDto.class);
        }

        public static List<UserResponseDto> toListDto(List<UserEntity> users) {
            return users.stream().map(user -> toDto(user)).collect(Collectors.toList());
        }

}

