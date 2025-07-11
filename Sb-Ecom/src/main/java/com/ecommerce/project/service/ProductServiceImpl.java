package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourseNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositiries.CategoryRepository;
import com.ecommerce.project.repositiries.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service




public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileService fileService;

     @Autowired
     private ModelMapper modelMapper;

     @Value("${project.image}")
     private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourseNotFoundException("Category", "categoryId", categoryId));
        boolean ifProductIsNotPresent = true;
        List<Product> products = category.getProducts();
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                ifProductIsNotPresent = false;
                break;
            }
        }

        if (ifProductIsNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() - ((product.getDiscount() * .01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product saveProduct = productRepository.save(product);
            return modelMapper.map(saveProduct, ProductDTO.class);
        }else{
            throw new APIException("Product already exists");
        }
    }




    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
      Sort sortByAscOrder=sortOrder.equalsIgnoreCase("Asc")? Sort.by(sortBy).ascending()
                          :Sort.by(sortBy).descending();
        Pageable pageDetails=PageRequest.of(pageNumber,pageSize,sortByAscOrder);
        Page<Product>pageProducts=productRepository.findAll(pageDetails);



        List<Product> products= pageProducts.getContent();
      List<ProductDTO>productDTOS=products.stream().map(product -> modelMapper.map(product,ProductDTO.class))
              .toList();

      if(products.isEmpty()){
          throw new APIException("No products exists");
      }

      ProductResponse productResponse=new ProductResponse();
      productResponse.setContent(productDTOS);
      productResponse.setPageNumber(pageProducts.getNumber());
      productResponse.setPageSize(pageProducts.getSize());
      productResponse.setTotalElements(pageProducts.getTotalElements());
      productResponse.setTotalPages(pageProducts.getTotalPages());
      productResponse.setLastPage(pageProducts.isLast());
      return productResponse;


    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category=categoryRepository.findById(categoryId).orElseThrow(()->
                new ResourseNotFoundException("Category","categoryId",categoryId));
        Sort sortByAscOrder=sortOrder.equalsIgnoreCase("Asc")? Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails=PageRequest.of(pageNumber,pageSize,sortByAscOrder);
        Page<Product>pageProducts=productRepository.findByCategoryOrderByPrice(category,pageDetails);



        List<Product> products= pageProducts.getContent();
        if(products.isEmpty()){
            throw new APIException("Category not found with name" +category);
        }

        List<ProductDTO>productDTOS=products.stream().map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());


        return productResponse;
    }

    @Override
    public ProductResponse searchByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAscOrder=sortOrder.equalsIgnoreCase("Asc")? Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails=PageRequest.of(pageNumber,pageSize,sortByAscOrder);
        Page<Product>pageProducts=productRepository.findByProductNameLikeIgnoreCase("%"+keyword+"%",pageDetails);

        List<Product>products=pageProducts.getContent();
        List<ProductDTO>productDTOS=products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();

        if(products.isEmpty()){
            throw new APIException("Product not found with keyword"+keyword);
        }

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());


        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {

        //get the existing product from db
        Product productFromDb=productRepository.findById(productId)
                .orElseThrow(()->new ResourseNotFoundException("Product","productId",productId));

        //update the product info user shared in request body
        Product product=modelMapper.map(productDTO,Product.class);
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
        product.setPrice(product.getPrice());
        productFromDb.setImage(product.getImage());
        productFromDb.setSpecialPrice(product.getSpecialPrice());
        Product saveProduct=productRepository.save(productFromDb);

        return modelMapper.map(saveProduct,ProductDTO.class);

    }




    public ProductDTO deleteProduct(Long productId) {
        Product product=productRepository.findById(productId).orElseThrow(()-> new ResourseNotFoundException("Product","productId",productId));
        productRepository.delete(product);
        return modelMapper.map(product,ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //Get the product from db
        Product productFromDb=productRepository.findById(productId).orElseThrow(()->new ResourseNotFoundException("Product","productId",productId));

        //Upload image to server
        //Get the file name of the uploaded image

        String fileName=fileService.uploadImage(path,image);

        //updating the new file name to product
        productFromDb.setImage(fileName);

        //save the updated product
        Product updatedProduct=productRepository.save(productFromDb);
        //return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct,ProductDTO.class);

    }



}
