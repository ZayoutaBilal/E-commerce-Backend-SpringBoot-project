package app.backend.click_and_buy.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
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

    @Column(name = "product_information",columnDefinition = "TEXT")
    private String information;


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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id",referencedColumnName = "discount_id")
    private Discount discount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private List<ProductVariation> productVariations;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Rating rating;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private List<ProductImage> productImages;







}
