package in.makeus.petspace.dto.room;

import in.makeus.petspace.domain.image.RoomImage;
import in.makeus.petspace.domain.Reservation;
import in.makeus.petspace.domain.Review;
import in.makeus.petspace.domain.Room;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class RoomListResponseDto {
    private Long roomId;
    private List<String> roomImages;
    private String city;
    private String district;
    private int price;
    private double averageReviewScore;
    private int numberOfReview;

    public RoomListResponseDto(Room entity) {
        List<Integer> reviewScores = entity.getReservation()
                .stream()
                .map(Reservation::getReview).filter(Objects::nonNull)
                .map(Review::getScore)
                .collect(Collectors.toList());

        this.roomId = entity.getId();
        this.roomImages = entity.getRoomImages()
                .stream().map(RoomImage::getRoomImageUrl)
                .collect(Collectors.toList());
        if (roomImages.size() > 5) roomImages.subList(0, 5);
        this.averageReviewScore = reviewScores.stream().mapToInt(n -> n).average().orElse(0);
        this.numberOfReview = reviewScores.size();
        this.city = entity.getAddress().getCity();
        this.district = entity.getAddress().getDistrict();
        this.price = entity.getPrice();
    }
}
