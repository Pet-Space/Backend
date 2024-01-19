package in.makeus.petspace.dto.review;

import in.makeus.petspace.domain.Reservation;
import in.makeus.petspace.domain.Review;
import in.makeus.petspace.domain.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Slf4j
@Builder
public class ReviewRequestDto {

    private List<MultipartFile> reviewImages;
    private Integer score;
    private String content;

    public Review toEntity(Reservation reservation) {
        return Review.builder()
                .user(reservation.getUser())
                .reservation(reservation)
                .room(reservation.getRoom())
                .status(Status.ACTIVE)
                .score(score)
                .content(content)
                .build();
    }

    public static ReviewRequestDto of(List<MultipartFile> reviewImages, int score, String content) {
        return ReviewRequestDto.builder()
                .reviewImages(reviewImages)
                .score(score)
                .content(content)
                .build();
    }
}
