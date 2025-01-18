package app.backend.click_and_buy.responses;

import app.backend.click_and_buy.entities.Category;
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
