package app.backend.click_and_buy.entities;


import app.backend.click_and_buy.enums.UserActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "user_behavior")
public class UserBehavior implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_behavior_id")
    private Long userBehaviorId;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private UserActionType actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "user_behavior_products", joinColumns = @JoinColumn(name = "user_behavior_id"))
    private Set<ProductTimestamp> productTimestamps = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_behavior_categories", joinColumns = @JoinColumn(name = "user_behavior_id"))
    private Set<CategoryTimestamp> categoryTimestamps = new HashSet<>();


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addDetails(Object details) {
        if (details instanceof Set<?>) {
            if (actionType == UserActionType.VIEW || actionType == UserActionType.ADD_TO_CART) {
                for(Object detail : (Set<?>)details) {
                    productTimestamps.add(ProductTimestamp.builder()
                            .product((Product) detail)
                            .build());
                }
            } else if (actionType == UserActionType.SEARCH) {
                for(Object detail : (Set<?>)details) {
                    categoryTimestamps.add(CategoryTimestamp.builder()
                            .category((Category) detail)
                            .build());
                }
            }
        }
    }


}