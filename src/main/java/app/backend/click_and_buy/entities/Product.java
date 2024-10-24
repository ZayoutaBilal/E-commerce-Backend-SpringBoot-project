package app.backend.click_and_buy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
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

//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
//    private List<CartItem> cartItems;
//
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
//    private List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "category_id",referencedColumnName = "category_id")
    private Category category;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Rating rating;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "products_categories",
//            joinColumns = {
//                    @JoinColumn(name = "product_id")
//            },
//            inverseJoinColumns = {
//                    @JoinColumn(name = "category_id")
//            }
//    )
//    private List<Category> category;




}
