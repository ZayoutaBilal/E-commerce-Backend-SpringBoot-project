package app.backend.click_and_buy.services;

import app.backend.click_and_buy.entities.Category;
import app.backend.click_and_buy.entities.Product;
import app.backend.click_and_buy.repositories.CategoryRepository;
import app.backend.click_and_buy.repositories.ProductRepository;
import app.backend.click_and_buy.responses.Categories;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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


}
