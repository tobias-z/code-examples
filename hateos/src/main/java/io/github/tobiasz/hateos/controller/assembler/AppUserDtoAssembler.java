package io.github.tobiasz.hateos.controller.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.github.tobiasz.hateos.controller.UserController;
import io.github.tobiasz.hateos.dto.AppUserDto;
import io.github.tobiasz.hateos.entity.AppUser;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class AppUserDtoAssembler extends RepresentationModelAssemblerSupport<AppUser, AppUserDto> {

	public AppUserDtoAssembler() {
		super(UserController.class, AppUserDto.class);
	}

	@Override
	public AppUserDto toModel(AppUser entity) {
		AppUserDto appUserDto = new AppUserDto();
		BeanUtils.copyProperties(entity, appUserDto);
		appUserDto.add(linkTo(methodOn(UserController.class).getUsers()).withSelfRel());
		return appUserDto;
	}
}
