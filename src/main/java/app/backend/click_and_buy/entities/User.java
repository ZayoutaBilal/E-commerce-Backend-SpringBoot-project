package app.backend.click_and_buy.entities;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.*;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.type.descriptor.java.BooleanJavaType;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "users")
@FilterDef(name = "deletedUserFilter", parameters = @ParamDef(name = "isDeleted",type = BooleanJavaType.class))
@Filter(name = "deletedUserFilter", condition = "deleted = :isDeleted")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Size(min = 5, max = 15)
    @Column(name = "username",unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Size(min = 8, max = 40)
    @Email
    @Column(name = "email",unique = true, nullable = false)
    private String email;

    @Column(name = "email_confirmed", nullable = false)
    private Boolean emailConfirmed = Boolean.FALSE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted",nullable = false)
    private Boolean deleted = Boolean.FALSE;

    private Boolean active = Boolean.TRUE;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles;

    @OneToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "customer_id")
    private Customer customer;

    private Integer reportedTimes;

    @Transient
    private Boolean isReported;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        reportedTimes = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @PostLoad
    protected void afterLoad(){
        isReported = reportedTimes >= 3;
    }

}
