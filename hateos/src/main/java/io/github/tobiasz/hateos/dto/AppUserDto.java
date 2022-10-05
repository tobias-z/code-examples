package io.github.tobiasz.hateos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDto extends RepresentationModel<AppUserDto> {

	private String username;
	private String password;

}
