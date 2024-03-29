package in.makeus.petspace.dto.review;

import in.makeus.petspace.domain.Review;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponseDto {

    private Long id;
    private Integer score;
    private String content;

    public static ReviewResponseDto of(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .score(review.getScore())
                .content(review.getContent())
                .build();
    }
}
