package com.marcot.projectparkapi.web.dto.mapper;

import com.marcot.projectparkapi.entity.User;
import com.marcot.projectparkapi.web.dto.UserCreateDto;
import com.marcot.projectparkapi.web.dto.UserResponseDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;


import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

        public static User toUser(UserCreateDto createDto) {
            return new ModelMapper().map(createDto, User.class);
        }

        public static UserResponseDto toDto(User User) {
            String role = User.getRole().name().substring("ROLE_".length());
            PropertyMap<User, UserResponseDto> props = new PropertyMap<User, UserResponseDto>() {
                @Override
                protected void configure() {
                    map().setRole(role);
                }
            };
            ModelMapper mapper = new ModelMapper();
            mapper.addMappings(props);
            return mapper.map(User, UserResponseDto.class);
        }

        public static List<UserResponseDto> toListDto(List<User> Users) {
            return Users.stream().map(user -> toDto(user)).collect(Collectors.toList());
        }

}

