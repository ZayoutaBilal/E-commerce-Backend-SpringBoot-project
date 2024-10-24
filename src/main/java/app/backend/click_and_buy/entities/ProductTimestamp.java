package app.backend.click_and_buy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Builder
public class ProductTimestamp {
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(columnDefinition = "int default 1")
    private Integer times;

}
