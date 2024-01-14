package in.makeus.petspace.dto.review;

import in.makeus.petspace.domain.Review;
import in.makeus.petspace.domain.image.ReviewImage;
import in.makeus.petspace.util.formatter.DayAfterFormatter;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ReviewsResponseDto {
    private Long id;
    private String nickName;
    private String profileImage;
    private List<String> reviewImages;
    private int score;
    private String dayAfterCreated;
    private String content;

    public static ReviewsResponseDto of(Review review) {

        List<String> reviewImageUrls = review.getReviewImages().stream()
                .map(ReviewImage::getReviewImageUrl)
                .collect(Collectors.toList());

        return ReviewsResponseDto.builder()
                .id(review.getId())
                .nickName(review.getUser().getNickname())
                .profileImage(review.getUser().getProfileImage())
                .reviewImages(reviewImageUrls)
                .score(review.getScore())
                .dayAfterCreated(DayAfterFormatter.formattingDayAfter(review.getCreatedAt()))
                .content(review.getContent())
                .build();
    }
}
