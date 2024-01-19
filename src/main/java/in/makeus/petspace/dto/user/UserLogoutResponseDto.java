package in.makeus.petspace.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLogoutResponseDto {

    private String email;
}
