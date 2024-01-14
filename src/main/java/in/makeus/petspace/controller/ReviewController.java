package in.makeus.petspace.controller;

import in.makeus.petspace.domain.user.auth.PrincipalDetails;
import in.makeus.petspace.dto.review.ReviewDeleteResponseDto;
import in.makeus.petspace.dto.review.ReviewRequestDto;
import in.makeus.petspace.dto.review.ReviewResponseDto;
import in.makeus.petspace.dto.review.ReviewsSliceResponseDto;
import in.makeus.petspace.service.ReviewService;
import in.makeus.petspace.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(value = "/reviews")
    public BaseResponse<ReviewResponseDto> createReview(@AuthenticationPrincipal PrincipalDetails principalDetail,
                                                          @RequestParam("reservationId") Long reservationId,
                                                          @RequestParam(value = "reviewImages", required = false) List<MultipartFile> reviewImages,
                                                          @RequestParam(value = "score", required = false, defaultValue = "0") int score,
                                                          @RequestParam(value = "content", required = false) String content) {

        Long userId = principalDetail.getId();
        ReviewRequestDto requestDto = ReviewRequestDto.of(reviewImages, score, content);
        ReviewResponseDto createResponseDto = reviewService.save(userId, reservationId, requestDto);
        return new BaseResponse<>(createResponseDto);
    }


    @GetMapping("/reviews")
    public BaseResponse<ReviewsSliceResponseDto> getAllReviews(@RequestParam Long roomId,
                                                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                               @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        log.info("리뷰 받기 통신 시작");
        PageRequest pageRequest = PageRequest.of(page, size);
        ReviewsSliceResponseDto responseDto = reviewService.findAllReviewsByPage(roomId, pageRequest);
        return new BaseResponse<>(responseDto);
    }

    @PatchMapping("/reviews/{reviewId}")
    public BaseResponse<ReviewResponseDto> updateReview(@AuthenticationPrincipal PrincipalDetails principalDetail,
                                                        @PathVariable Long reviewId,
                                                        @ModelAttribute ReviewRequestDto reviewUpdateRequestDto) {
        Long userId = principalDetail.getId();
        ReviewResponseDto responseDto = reviewService.updateReview(userId, reviewId, reviewUpdateRequestDto);

        return new BaseResponse<>(responseDto);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public BaseResponse<ReviewDeleteResponseDto> deleteReview(@AuthenticationPrincipal PrincipalDetails principalDetail,
                                                              @PathVariable Long reviewId) {
        Long userId = principalDetail.getId();
        ReviewDeleteResponseDto deleteResponseDto = reviewService.deleteReview(userId, reviewId);

        return new BaseResponse<>(deleteResponseDto);
    }
}

