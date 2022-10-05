package io.github.tobiasz.hateos.controller;


import io.github.tobiasz.hateos.controller.assembler.AppUserDtoAssembler;
import io.github.tobiasz.hateos.dto.AppUserDto;
import io.github.tobiasz.hateos.entity.AppUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final AppUserDtoAssembler appUserDtoAssembler;

	@GetMapping
	public ResponseEntity<List<AppUserDto>> getUsers() {
		AppUserDto appUserDto = this.appUserDtoAssembler.toModel(new AppUser("bob", "the builder"));
		return ResponseEntity.ok(List.of(appUserDto));
	}
}
