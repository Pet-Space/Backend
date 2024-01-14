package in.makeus.petspace.dto.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class RoomImageAddRequestDto {

    private MultipartFile roomImage;

}
