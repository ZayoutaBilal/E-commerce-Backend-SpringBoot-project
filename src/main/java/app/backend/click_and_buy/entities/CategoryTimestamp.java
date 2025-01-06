package app.backend.click_and_buy.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Builder
public class CategoryTimestamp {

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(columnDefinition = "int default 1")
    private Integer times;


}
