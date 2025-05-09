package app.backend.click_and_buy.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customers")
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Size(min = 1, max = 30)
    private String firstName;

    @Size(min = 1, max = 30)
    private String lastName;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "gender")
    private String gender;

    private LocalDate birthday;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Lob
    @Column(name = "picture",columnDefinition = "LONGBLOB")
    private byte[] picture;

//    @JsonBackReference
//    @OneToOne(mappedBy = "customer",cascade = CascadeType.ALL)
//    private User user;


    @OneToOne(mappedBy = "customer",fetch = FetchType.LAZY)
    private Cart cart;

    @OneToOne(mappedBy = "customer",fetch = FetchType.LAZY)
    private User user;

    public Customer(String firstName, String lastName, String gender, String phone, LocalDate birthDay,String address,String city) {
        this.birthday=birthDay;
        this.firstName=firstName;
        this.lastName=lastName;
        this.gender=gender;
        this.phone=phone;
        this.address=address;
        this.city=city;

    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

//    @OneToMany(mappedBy = "customer")
//    private List<Order> orders;



}
