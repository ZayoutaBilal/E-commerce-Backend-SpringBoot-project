package app.backend.click_and_buy.responses;

import app.backend.click_and_buy.entities.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CategoryDetails {
    private long categoryId;
    private String name;
    private String description;
    private Object image;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private long parentCategoryId;

    public CategoryDetails(Category category){
        this.categoryId=category.getCategoryId();
        this.name=category.getName();
        this.description=category.getDescription();
        this.createdAt=category.getCreatedAt();
        this.parentCategoryId=category.getParentCategoryId();
        this.image=category.getImage();
    }
}
