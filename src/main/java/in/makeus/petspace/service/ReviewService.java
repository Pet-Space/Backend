package in.makeus.petspace.service;

import in.makeus.petspace.domain.Reservation;
import in.makeus.petspace.domain.Review;
import in.makeus.petspace.domain.image.ReviewImage;
import in.makeus.petspace.dto.review.ReviewDeleteResponseDto;
import in.makeus.petspace.dto.review.ReviewRequestDto;
import in.makeus.petspace.dto.review.ReviewResponseDto;
import in.makeus.petspace.dto.review.ReviewsResponseDto;
import in.makeus.petspace.dto.review.ReviewsSliceResponseDto;
import in.makeus.petspace.repository.ReservationRepository;
import in.makeus.petspace.repository.ReviewImageRepository;
import in.makeus.petspace.repository.ReviewRepository;
import in.makeus.petspace.util.exception.ReviewException;
import in.makeus.petspace.util.s3.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static in.makeus.petspace.util.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewService {

    private final ReservationRepository reservationRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewRepository reviewRepository;
//    private final AwsS3Uploader awsS3Uploader;

    public ReviewResponseDto save(Long userId, Long reservationId, ReviewRequestDto requestDto) {
        log.info("Review create ! userId={}, reservationId={}", userId, reservationId);

        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new ReviewException(POST_REVIEW_EMPTY_RESERVATION));

        if (reservation.isReviewCreated()) {
            throw new ReviewException(POST_REVIEW_ALREADY_CREATED);
        }

        Review review = requestDto.toEntity(reservation);
//        uploadReviewImages(requestDto, review);
        reviewRepository.save(review);

        return ReviewResponseDto.of(review);
    }


    @Transactional(readOnly = true)
    public ReviewsSliceResponseDto findAllReviewsByPage(Long roomId, Pageable pageable) {
        List<Review> reviews = reviewRepository.findByRoomId(roomId);

        Slice<Review> allReviewsSliceBy = reviewRepository.findAllReviewsSliceBy(roomId, pageable);
        List<ReviewsResponseDto> reviewsResponseDtos = allReviewsSliceBy.getContent().stream()
                .map((ReviewsResponseDto::of))
                .collect(Collectors.toList());

        int numberOfReview = reviews.size();
        double averageReviewScore = reviews.stream()
                .mapToInt(Review::getScore)
                .average()
                .orElse(0);

        return ReviewsSliceResponseDto.builder()
                .reviews(reviewsResponseDtos)
                .numberOfReview(numberOfReview)
                .averageReviewScore(Double.parseDouble(String.format("%.2f", averageReviewScore)))
                .page(allReviewsSliceBy.getPageable().getPageNumber())
                .isLast(allReviewsSliceBy.isLast())
                .build();
    }

    public ReviewResponseDto updateReview(Long userId, Long reviewId, ReviewRequestDto reviewRequestDto) {

        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ReviewException(INVALID_REVIEW));

        updateEachReviewItem(reviewRequestDto, review);

        return ReviewResponseDto.of(review);
    }

    private void updateEachReviewItem(ReviewRequestDto reviewRequestDto, Review review) {
        if (reviewRequestDto.getScore() != null) {
            review.updateScore(reviewRequestDto.getScore());
        }

        if (reviewRequestDto.getContent() != null) {
            review.updateContent(reviewRequestDto.getContent());
        }

        if (!reviewRequestDto.getReviewImages().isEmpty()) {
//            review.updateReviewImages(deleteExistedImagesAndUploadNewImages(reviewRequestDto, review));
        }
    }

//    private List<ReviewImage> deleteExistedImagesAndUploadNewImages(ReviewRequestDto reviewRequestDto, Review review) {
//        deleteExistedImages(review);
//        return uploadReviewImages(reviewRequestDto, review);
//    }
//
//    private void deleteExistedImages(Review review) {
//        reviewImageRepository.deleteAllByIdInBatch(review.getId());
//        deleteS3Images(review);
//    }

//    private void deleteS3Images(Review review) {
//        List<ReviewImage> reviewImages = review.getReviewImages();
//
//        for (ReviewImage reviewImage : reviewImages) {
//            String imageKey = reviewImage.getReviewImageUrl().substring(49);
//            awsS3Uploader.deleteReviewImage(imageKey);
//        }
//    }

    public ReviewDeleteResponseDto deleteReview(Long userId, Long reviewId) {

        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ReviewException(INVALID_REVIEW));

        Reservation reservation = review.getReservation();
        reservation.deleteReview();
        return ReviewDeleteResponseDto.builder()
                .id(reviewId)
                .build();
    }

//    private List<ReviewImage> uploadReviewImages(ReviewRequestDto requestDto, Review review) {
//        return requestDto.getReviewImages().stream()
//                .map(reviewImage -> awsS3Uploader.upload(reviewImage, "review"))
//                .map(url -> createPostImage(review, url))
//                .collect(Collectors.toList());
//    }

    private ReviewImage createPostImage(Review review, String url) {
        log.info("url = {}", url);
        review.clearReviewImages();
        
        return reviewImageRepository.save(ReviewImage.builder()
                .reviewImageUrl(url)
                .review(review)
                .build());
    }
}


