package com.ecommerce.project.service;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourseNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositiries.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;


@Service
public class    CategoryServiceImpl implements CategoryService {

@Autowired
private CategoryRepository categoryRepository;
@Autowired
private ModelMapper modelMapper;

    @Override

    public CategoryResponse getAllcategory(Integer pageNumber, Integer pageSize,String sortBy,String sortOrder) {
        Sort sortOrderBy=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortOrderBy);
        Page<Category>categoryPage=categoryRepository.findAll(pageDetails);
        List<Category>categories=categoryPage.getContent();
        if(categories.isEmpty()){
            throw new APIException("No categories are present");
        }
        List<CategoryDTO>categoryDTOS=categories.stream().map(category -> modelMapper.map(category,CategoryDTO.class)).collect(Collectors.toList());
        CategoryResponse categoryResponse=new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryResponse.getPageNumber());
        categoryResponse.setPageSize(categoryResponse.getPageSize());
        categoryResponse.setTotalElements(categoryResponse.getTotalElements());
        categoryResponse.setTotalPages(categoryResponse.getTotalPages());
        categoryResponse.setLastPage(categoryResponse.isLastPage());
        return categoryResponse;
    }

    @Override

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category=modelMapper.map(categoryDTO,Category.class);
        Category savedCategorydb=categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if(savedCategorydb!=null){
            throw new APIException("Category with the name" + categoryDTO.getCategoryName()+ "already exists");
        }

        Category savedCategory=categoryRepository.save(category);
        CategoryDTO savedCategoryDTO=modelMapper.map(savedCategory,CategoryDTO.class);
        return savedCategoryDTO;
    }

    @Override

    public CategoryDTO deleteCategory(Long categoryId) {
       Category category=categoryRepository.findById(categoryId).orElseThrow(()->new ResourseNotFoundException("Category","categoryId", categoryId));
categoryRepository.delete(category);
        return modelMapper.map(category,CategoryDTO.class);
    }

    @Override

    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category savedCategory =categoryRepository.findById(categoryId).orElseThrow(()->new ResourseNotFoundException("Category","categoryId", categoryId));
        Category category=modelMapper.map(categoryDTO,Category.class);

        category.setCategoryId(categoryId);
        savedCategory=categoryRepository.save(category);
        return modelMapper.map(savedCategory,CategoryDTO.class);
//
    }
}
