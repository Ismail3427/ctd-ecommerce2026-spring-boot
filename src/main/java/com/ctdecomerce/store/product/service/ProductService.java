package com.ctdecomerce.store.product.service;

import com.ctdecomerce.store.categories.model.CategoriesModel;
import com.ctdecomerce.store.categories.repository.CategoriesRepo;
import com.ctdecomerce.store.dto.IdRequest;
import com.ctdecomerce.store.product.dto.CreateProductDTO;
import com.ctdecomerce.store.product.dto.OwnerDTO;
import com.ctdecomerce.store.product.dto.ProductDTO;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.product.repository.ProductRepo;
import com.ctdecomerce.store.retailers.model.RetailersModel;
import com.ctdecomerce.store.retailers.repository.RetailersRepo;
import com.stripe.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepo productRepo;
    private final RetailersRepo retailersRepo;
    private final CategoriesRepo categoriesRepo;

    public ProductService(ProductRepo productRepo, RetailersRepo retailersRepo, CategoriesRepo categoriesRepo) {
        this.productRepo = productRepo;
        this.retailersRepo = retailersRepo;
        this.categoriesRepo = categoriesRepo;
    }

    @Transactional
    public ProductModel createProduct(CreateProductDTO createProductDTO) {
        ProductModel productModel = new ProductModel();
        productModel.setName(createProductDTO.getName());
        List<CategoriesModel> cats = new ArrayList<>();
        for (String id : createProductDTO.getCategoryId()) {
            CategoriesModel category = categoriesRepo.findById(UUID.fromString(id)).orElse(null);
            cats.add(category);
        }
        productModel.setCategories(cats);
        productModel.setDescription(createProductDTO.getDescription());
        productModel.setPriceInCents(createProductDTO.getPriceInCents());
        RetailersModel retailersModel = retailersRepo.findById(UUID.fromString(createProductDTO.getUserId())).orElse(null);
        productModel.setOwner(retailersModel);
        productRepo.save(productModel);
        return productModel;
    }

    @Transactional
    public List<ProductDTO> getAllProducts() {
        List<ProductModel> allProductsUnfiltered = productRepo.findAll();
        List<ProductDTO> filteredProducts = new ArrayList<>();
        for (ProductModel product : allProductsUnfiltered) {
            OwnerDTO owner = new OwnerDTO(product.getOwner().getId(), product.getOwner().getName());
            ProductDTO newProduct = new ProductDTO(product.getId(), product.getName(), owner);
            filteredProducts.add(newProduct);
        }
        return filteredProducts;
    }

    @Transactional
    public ProductModel getProductById(IdRequest idRequest) {
        return productRepo.findById(UUID.fromString(idRequest.getId())).orElse(null);
    }
}
