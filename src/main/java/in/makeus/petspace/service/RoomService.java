package in.makeus.petspace.service;

import in.makeus.petspace.domain.Favorite;
import in.makeus.petspace.domain.Room;
import in.makeus.petspace.dto.room.RoomDetailResponseDto;
import in.makeus.petspace.dto.room.RoomFacilityResponseDto;
import in.makeus.petspace.repository.FavoriteRepository;
import in.makeus.petspace.repository.RoomRepository;
import in.makeus.petspace.util.exception.RoomException;
import in.makeus.petspace.util.input.room.CategoryType;
import in.makeus.petspace.util.input.room.SortBy;
import in.makeus.petspace.dto.room.RoomListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static in.makeus.petspace.util.BaseResponseStatus.NONE_ROOM;

@RequiredArgsConstructor
@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final FavoriteRepository favoriteRepository;

    private static final int DEFAULT_PAGE_SIZE = 5;

    @Transactional(readOnly = true)
    public List<RoomListResponseDto> findAllDesc(Optional<Integer> page,
                                                 Optional<SortBy> sortBy) {
        Sort sort = getSortBy(sortBy.orElse(SortBy.ID_DESC));
        Pageable pageable = PageRequest.of(page.orElse(0), DEFAULT_PAGE_SIZE, sort);

        return roomRepository.findAllDesc(pageable).stream()
                .map(RoomListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomListResponseDto> findAllDescByCategory(Optional<Integer> page,
                                                 Optional<SortBy> sortBy,
                                                 CategoryType categoryType) {
        Sort sort = getSortBy(sortBy.orElse(SortBy.ID_DESC));
        Pageable pageable = PageRequest.of(page.orElse(0), DEFAULT_PAGE_SIZE, sort);

        return roomRepository.findAllDescByCategory(pageable, categoryType.getCategoryId()).stream()
                .map(RoomListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomListResponseDto> findAllDescByUserId(Long userId, Optional<Integer> page) {
        Sort sort = getSortBy(SortBy.ID_DESC);
        Pageable pageable = PageRequest.of(page.orElse(0), DEFAULT_PAGE_SIZE, sort);
        return roomRepository.findAllDescByUserId(pageable, userId).stream()
                .map(RoomListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomListResponseDto> findAllDescByFilter(LocalDate startDay,
                                                         LocalDate endDay,
                                                         Optional<Integer> people,
                                                         Optional<Integer> pets,
                                                         Optional<String> keyword,
                                                         Optional<Integer> page,
                                                         Optional<SortBy> sortBy) {
        Sort sort = getSortBy(sortBy.orElse(SortBy.ID_DESC));
        Pageable pageable = PageRequest.of(page.orElse(0), DEFAULT_PAGE_SIZE, sort);

        long days = (long) Duration.between(startDay.atStartOfDay(), endDay.atStartOfDay()).toDays();

        return roomRepository.findAllDescByFilter(pageable,
                        startDay, endDay, days,
                        people.orElse(0), pets.orElse(0),
                        keyword.orElse("")).stream()
                .map(RoomListResponseDto::new)
                .collect(Collectors.toList());

    }

    public List<RoomListResponseDto> findAllDescByFilterAndCategory(LocalDate startDay,
                                                                    LocalDate endDay,
                                                                    Optional<Integer> people,
                                                                    Optional<Integer> pets,
                                                                    Optional<String> keyword,
                                                                    Optional<Integer> page,
                                                                    Optional<SortBy> sortBy,
                                                                    CategoryType categoryType) {
        Sort sort = getSortBy(sortBy.orElse(SortBy.ID_DESC));
        Pageable pageable = PageRequest.of(page.orElse(0), DEFAULT_PAGE_SIZE, sort);

        long days = (long) Duration.between(startDay.atStartOfDay(), endDay.atStartOfDay()).toDays();

        return roomRepository.findAllDescByFilterAndCategory(pageable,
                        startDay, endDay, days,
                        people.orElse(0), pets.orElse(0),
                        keyword.orElse(""),
                        categoryType.getCategoryId()).stream()
                .map(RoomListResponseDto::new)
                .collect(Collectors.toList());
    }

    public Sort getSortBy(SortBy sortBy) {
        if (sortBy.getOrderType().equals("ASC")) {
            return Sort.by(sortBy.getSortType()).ascending().and(Sort.by("id").descending());
        }
        return Sort.by(sortBy.getSortType()).descending().and(Sort.by("id").descending());
    }

    @Transactional(readOnly = true)
    public RoomDetailResponseDto getRoomDetail(Long roomId, Long userId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new RoomException(NONE_ROOM));

        RoomDetailResponseDto roomDetailResponseDto = new RoomDetailResponseDto(room);

        // 로그인 상태
        if(userId != null){
            Favorite favorite = favoriteRepository.findByUserIdAndRoomId(userId, roomId)
                    .orElse(null);

            if(favorite != null && favorite.isClicked() == true){
                roomDetailResponseDto.setFavorite(true);
            } else{
                roomDetailResponseDto.setFavorite(false);
            }
        } else{
            roomDetailResponseDto.setFavorite(false);
        }

        return roomDetailResponseDto;
    }

    @Transactional(readOnly = true)
    public RoomFacilityResponseDto getRoomFacilities(Long roomId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new RoomException(NONE_ROOM));

        return new RoomFacilityResponseDto(room);
    }
}
