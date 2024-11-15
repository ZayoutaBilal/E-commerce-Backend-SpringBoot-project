package app.backend.click_and_buy.entities;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "products")
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false,name = "product_name")
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(name = "old_price")
    private Double oldPrice;

    @Column(name = "product_description",columnDefinition = "TEXT")
    private String description;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private Double oldPriceBeforeUpdate; // Transient field to track price before update


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PostLoad
    protected void onLoad() {
        oldPriceBeforeUpdate = price;
    }

    @PreUpdate
    protected void onUpdate() {
        if (!price.equals(oldPriceBeforeUpdate)) {
            oldPrice = oldPriceBeforeUpdate;
            oldPriceBeforeUpdate = price;
        }
        updatedAt = LocalDateTime.now();
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",referencedColumnName = "category_id")
    private Category category;

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private Rating rating;





}
