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
public class CategoryTimestamp {

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(columnDefinition = "int default 1")
    private Integer times;



}
