package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    ProductService productService;
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO>addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId){
        ProductDTO saveproductDTO=productService.addProduct(categoryId,productDTO);
        return new ResponseEntity<>(saveproductDTO, HttpStatus.CREATED);

    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse>getAllProducts(
            @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER)Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE)Integer pageSize,
            @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY)String sortBy,
            @RequestParam (name="sortOrder",defaultValue = AppConstants.SORT_DIR)String sortOrder){

        ProductResponse productResponse=productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }


    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse>getByProductById(@PathVariable Long categoryId,
                                                           @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER)Integer pageNumber,
                                                           @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE)Integer pageSize,
                                                           @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY)String sortBy,
                                                           @RequestParam (name="sortOrder",defaultValue = AppConstants.SORT_DIR)String sortOrder                                              ){
        ProductResponse productResponse=productService.searchByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse>getByProductByKeyword(@PathVariable String keyword
    , @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER)Integer pageNumber,
                                                                @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE)Integer pageSize,
                                                                @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY)String sortBy,
                                                                @RequestParam (name="sortOrder",defaultValue = AppConstants.SORT_DIR)String sortOrder
                                                                ){
        ProductResponse productResponse=productService.searchByKeyword(keyword,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO>updateProduct(@Valid@RequestBody ProductDTO productDTO ,
                                                   @PathVariable Long productId)
    {
       ProductDTO updateProductDTO=productService.updateProduct(productId,productDTO );
       return new ResponseEntity<>(updateProductDTO,HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO>deleteProduct(@PathVariable long productId){
       ProductDTO deleteproductDTO= productService.deleteProduct(productId);
       return new ResponseEntity<>(deleteproductDTO,HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO>updateProductImage(@PathVariable Long productId, @RequestParam("image")MultipartFile image) throws IOException {
      ProductDTO updateProduct=productService.updateProductImage(productId,image);
      return new ResponseEntity<>(updateProduct,HttpStatus.OK);
    }
}
