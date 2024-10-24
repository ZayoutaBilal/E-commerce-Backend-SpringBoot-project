package app.backend.click_and_buy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "orders")
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_address")
    private String orderAddress;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "order_dateTime")
    private LocalDateTime orderDateTime;

    @Column(name = "estimated_arrival_date")
    private LocalDate estimatedArrivalDate;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

//    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
//    private List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "discount_id",referencedColumnName = "discount_id")
    private Discount discount;

//    @OneToOne(mappedBy = "order",cascade = CascadeType.ALL)
//    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "customer_id")
    private Customer customer;



}
