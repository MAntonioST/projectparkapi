package com.marcot.projectparkapi.web.dto.mapper;

import com.marcot.projectparkapi.entity.User;
import com.marcot.projectparkapi.web.dto.UserCreateDto;
import com.marcot.projectparkapi.web.dto.UserResponseDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;

public class UserMapper {

    public static User toUser(UserCreateDto createDto){
        return new ModelMapper().map(createDto, User.class);
    }

    public static UserResponseDto toUserDto(User user) {
        ModelMapper modelMapper = new ModelMapper();
        Converter<String, String> roleConverter = new Converter<>() {
            @Override
            public String convert(MappingContext<String, String> context) {
                return context.getSource().replace("ROLE_", "");
            }
        };
        PropertyMap<User, UserResponseDto> userMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                using(roleConverter).map(source.getRole(), destination.role());
            }
        };
        modelMapper.addMappings(userMap);
        return modelMapper.map(user, UserResponseDto.class);
    }
}
