package in.makeus.petspace.service;

import in.makeus.petspace.domain.Favorite;
import in.makeus.petspace.domain.Room;
import in.makeus.petspace.domain.user.User;
import in.makeus.petspace.dto.favorite.FavoriteClickResponseDto;
import in.makeus.petspace.dto.favorite.FavoritesResponseDto;
import in.makeus.petspace.dto.favorite.FavoritesSliceResponseDto;
import in.makeus.petspace.repository.FavoriteRepository;
import in.makeus.petspace.repository.RoomRepository;
import in.makeus.petspace.repository.UserRepository;
import in.makeus.petspace.util.exception.RoomException;
import in.makeus.petspace.util.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static in.makeus.petspace.util.BaseResponseStatus.NONE_ROOM;
import static in.makeus.petspace.util.BaseResponseStatus.NONE_USER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FavoriteService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public FavoriteClickResponseDto clickFavorite(Long userId, Long roomId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NONE_USER));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomException(NONE_ROOM));
        Favorite favorite = favoriteRepository.findByUserIdAndRoomId(userId, roomId)
                .orElse(null);

        log.info("user={}, room={}", user.getEmail(), room.getRoomName());
        return addOrChangeFavoriteStatus(favorite, user, room);
    }

    public FavoritesSliceResponseDto showFavoritesSliceByRegion(Long userId, String region, Pageable pageable) {
        Slice<Favorite> allFavoritesSliceBy = favoriteRepository.findAllFavoritesSliceBy(userId, region, pageable);

        List<FavoritesResponseDto> favorites = allFavoritesSliceBy.getContent().stream()
                .map((FavoritesResponseDto::of))
                .collect(Collectors.toList());

        return FavoritesSliceResponseDto.builder()
                .favorites(favorites)
                .page(allFavoritesSliceBy.getPageable().getPageNumber())
                .isLast(allFavoritesSliceBy.isLast())
                .build();
    }

    private FavoriteClickResponseDto addOrChangeFavoriteStatus(Favorite favorite, User user, Room room) {

        if (favorite == null) {
            favorite = Favorite.builder()
                    .user(user)
                    .room(room)
                    .isClicked(true)
                    .build();
            favoriteRepository.save(favorite);
        } else {
            favorite.changeFavoriteClickStatus();
        }
        return FavoriteClickResponseDto.builder()
                .id(favorite.getId())
                .roomName(favorite.getRoom().getRoomName())
                .isClicked(favorite.isClicked())
                .build();
    }
}
