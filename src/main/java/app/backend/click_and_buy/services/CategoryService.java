package app.backend.click_and_buy.services;

import app.backend.click_and_buy.dto.CategoryProjection;
import app.backend.click_and_buy.entities.Category;
import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.repositories.CategoryRepository;
import app.backend.click_and_buy.repositories.ProductRepository;
import app.backend.click_and_buy.responses.Categories;
import app.backend.click_and_buy.responses.CategoryDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {


    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;

    public Category getCategoryById(long id) {
        return categoryRepository.findByCategoryId(id);
    }

    public String getCategoryHierarchyString(Product product) {
        Category category = product.getCategory();
        String categoryString = category.getName();
        while (category.getParentCategoryId() != 0) {
            category = getCategoryById(category.getParentCategoryId());
            categoryString = category.getName().concat(" / " + categoryString);
        }
        return categoryString;
    }

    public List<Category> getCategoryByName(String name) {
        System.out.println(name);
        List<Category> cat= categoryRepository.findByNameContains(name);
        System.out.println("Category f=> "+cat);
        return cat;

    }

    public List<Category> getCategoryByName(List<String> name) {
        List<Category> categories = new ArrayList<>();
        for (String s : name) {
            categories.addAll(getCategoryByName(s));
        }
        return categories;
    }

    public Category getOriginCategory(String categoryName,long level) {
        return categoryRepository.findByNameLikeAndParentCategoryId(categoryName.concat("%"),level);
    }

    public List<Category> getCategoriesTree(List<Category> categories) {

        List<Category> categoryList = new ArrayList<>(categories);
        List<Long> categoryIdList = categoryList.stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toList());

        int initialSize = 0;
        int newSize = categoryList.size();

        while (newSize > initialSize) {

            initialSize = newSize;
            List<Category> subcategories = categoryRepository.findByParentCategoryIdIn(categoryIdList);
            for (Category subcategory : subcategories) {
                if (!categoryIdList.contains(subcategory.getCategoryId())) {
                    categoryList.add(subcategory);
                    categoryIdList.add(subcategory.getCategoryId());
                }
            }
            newSize = categoryList.size();
        }
        return categoryList;
    }

    public List<Categories> getCategoriesWithSubcategories() {
        List<Category> mainCategories = categoryRepository.findByParentCategoryId(0L);

         return mainCategories.stream().map(category -> {
            List<Category> subcategories = categoryRepository.findByParentCategoryId(category.getCategoryId());

            List<String> subcategoryNames = subcategories.stream()
                    .map(Category::getName)
                    .collect(Collectors.toList());

            List<Integer> productsCount = subcategories.stream()
                    .map(subcategory -> productRepository.countByCategory(subcategory))
                    .collect(Collectors.toList());

            return new Categories(category.getName(), subcategoryNames, productsCount);
        }).collect(Collectors.toList());
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<CategoryProjection> getAllCategories_Projection() {
        return categoryRepository.findAllProjectedBy();
    }

    public long addCategory(CategoryDetails category) throws IOException {
        return categoryRepository.save(Category.builder()
                .name(category.getName())
                .description(category.getDescription())
                .image(category.getImage() == null ? new byte[0] : ((MultipartFile)category.getImage()).getBytes())
                .parentCategoryId(category.getParentCategoryId())
                .build())
                .getCategoryId();
    }

    public void deleteCategory(long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public void updateCategory(CategoryDetails category){
        Category oldCategory = categoryRepository.findByCategoryId(category.getCategoryId());
        if(Objects.nonNull(oldCategory)){
            oldCategory.setName(category.getName());
            oldCategory.setDescription(category.getDescription());
            oldCategory.setParentCategoryId(category.getParentCategoryId());
            if(!Objects.isNull(category.getImage())) {
                try {
                    oldCategory.setImage(((MultipartFile)category.getImage()).getBytes());
                } catch (IOException ignored) {}
            }
            categoryRepository.save(oldCategory);
        }
    }


}
