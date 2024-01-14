package in.makeus.petspace.domain.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.makeus.petspace.domain.BaseTimeEntity;
import in.makeus.petspace.domain.Favorite;
import in.makeus.petspace.domain.Reservation;
import in.makeus.petspace.domain.Review;
import in.makeus.petspace.domain.Room;
import in.makeus.petspace.domain.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "`user`")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String profileImage;

    @Column(length = 45)
    private String username;

    @Column(length = 45)
    private String nickname;

    @Column(length = 45)
    private String birth;

    @Column(length = 45, nullable = false)
    private String email;

    private String password;

    private boolean privacyAgreement;
    private boolean marketingAgreement;
    private boolean hostPermission;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private OauthProvider oauthProvider;

    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Favorite> favorites = new ArrayList<>();

    @Builder
    public User(String profileImage, String username, String nickname, String birth, String email, String password,
                boolean privacyAgreement, boolean marketingAgreement, boolean hostPermission,
                OauthProvider oauthProvider, Status status, Role role) {
        this.profileImage = profileImage;
        this.username = username;
        this.nickname = nickname;
        this.birth = birth;
        this.email = email;
        this.password = password;
        this.privacyAgreement = privacyAgreement;
        this.marketingAgreement = marketingAgreement;
        this.hostPermission = hostPermission;
        this.oauthProvider = oauthProvider;
        this.status = status;
        this.role = role;
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
