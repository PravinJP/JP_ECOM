package com.ecommerce.project.repositiries;

import com.ecommerce.project.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface    CategoryRepository extends JpaRepository<Category,Long> {

    Category findByCategoryName(String categoryname);
}
