package app.backend.click_and_buy.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long ratingId;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "rating", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<UserRating> userRatings = new ArrayList<>();

    @Column(name = "average_stars", columnDefinition = "double default 0")
    private double averageStars;

    @Column(name = "total_ratings", columnDefinition = "int default 0")
    private int totalRatings;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


    public void updateRatingSummary() {
        setTotalRatings((int) getUserRatings().stream()
                .filter(rating -> rating.getStars() != 0)
                .count());
        setAverageStars(getUserRatings().stream()
                .filter(rating -> rating.getStars() != 0)
                .mapToInt(UserRating::getStars)
                .average()
                .orElse(0.0));
    }


    public List<UserRating> getUserRatings() {
        if (userRatings == null) {
            userRatings = new ArrayList<>();
        }
        return userRatings;
    }


}

