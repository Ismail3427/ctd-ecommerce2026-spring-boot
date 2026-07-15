package com.ctdecomerce.store.product.service;

import com.ctdecomerce.store.discounts.model.DiscountsModel;
import com.ctdecomerce.store.discounts.repository.DiscountsRepo;
import com.ctdecomerce.store.dto.IdRequest;
import com.ctdecomerce.store.product.dto.*;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.product.repository.ProductRepo;
import com.ctdecomerce.store.retailers.dto.OrderItemDto;
import com.ctdecomerce.store.retailers.dto.ProductIdRequest;
import com.ctdecomerce.store.retailers.dto.RetailerIdRequest;
import com.ctdecomerce.store.retailers.model.RetailersModel;
import com.ctdecomerce.store.retailers.repository.RetailersRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepo productRepo;
    private final RetailersRepo retailersRepo;
    private final DiscountsRepo discountsRepo;

    public ProductService(ProductRepo productRepo, RetailersRepo retailersRepo, DiscountsRepo discountsRepo) {
        this.productRepo = productRepo;
        this.retailersRepo = retailersRepo;
        this.discountsRepo = discountsRepo;
    }

    @Transactional
    public ProductModel createProduct(CreateProductDTO createProductDTO) {
        ProductModel productModel = new ProductModel();
        productModel.setName(createProductDTO.getName());
        productModel.setDescription(createProductDTO.getDescription());
        productModel.setPriceInCents(createProductDTO.getPriceInCents());
        productModel.setShowing(createProductDTO.getIsShowing());
        productModel.setAvailable(createProductDTO.getIsAvailable());
        productModel.setStock(createProductDTO.getStock());
        RetailersModel retailersModel = retailersRepo.findById(UUID.fromString(createProductDTO.getOwnerId())).orElse(null);
        productModel.setOwner(retailersModel);
        productRepo.save(productModel);
        return productModel;
    }


//    @Transactional
//    public ProductModel changeProductName(CreateProductDTO product) {
//        var product = productRepo.findById()
//    }

    @Transactional
    public List<ProductDTO> getAllProducts() {
        List<ProductModel> allProductsUnfiltered = productRepo.findAll();
        List<ProductDTO> filteredProducts = new ArrayList<>();
        for (ProductModel product : allProductsUnfiltered) {
            OwnerDTO owner = new OwnerDTO(product.getOwner().getId(), product.getOwner().getName());
            DiscountsModel discounts = discountsRepo.findDiscountsModelByProduct(product);
            if (discounts != null) {
                double productOgPrice = ((double) product.getPriceInCents() / 100) * (1 - discounts.getOffer()) * 100;
                ProductDTO newProduct = new ProductDTO(product.getId(), product.getName(), owner, (int) productOgPrice, true, product.getPriceInCents(), product.isShowing(), product.isAvailable());
                if (product.getStock() > 0) {
                    filteredProducts.add(newProduct);
                }
            } else {
                ProductDTO newProduct = new ProductDTO(product.getId(), product.getName(), owner, product.getPriceInCents(), false, product.getPriceInCents(), product.isShowing(), product.isAvailable());
                if (product.getStock() > 0) {
                    filteredProducts.add(newProduct);
                }
            }
        }
        return filteredProducts;
    }

    @Transactional
    public ProductModel getProductById(IdRequest idRequest) {
        return productRepo.findById(UUID.fromString(idRequest.getId())).orElse(null);
    }

    @Transactional
    public void changeProductName(EditNameReqDto editNameReqDto) {
        var product = productRepo.findById(editNameReqDto.getProduct_id()).orElseThrow();
        product.setName(editNameReqDto.getName());
        productRepo.save(product);
    }

    @Transactional
    public void changeProductDescription(EditDescriptionReqDto editDescriptionReqDto) {
        var product = productRepo.findById(editDescriptionReqDto.getProduct_id()).orElseThrow();
        product.setDescription(editDescriptionReqDto.getDescription());
        productRepo.save(product);
    }

    @Transactional
    public void changeProductPrice(EditPriceReqDto editPriceReqDto) {
        var product = productRepo.findById(editPriceReqDto.getProduct_id()).orElseThrow();
        product.setPriceInCents(editPriceReqDto.getPriceInCents());
        productRepo.save(product);
    }

    @Transactional
    public void changeProductStock(EditStockReqDto editStockReqDto) {
        var product = productRepo.findById(editStockReqDto.getProduct_id()).orElseThrow();
        product.setStock(editStockReqDto.getStock());
        productRepo.save(product);
    }

    @Transactional
    public void changeProductAvailable(EditAvailableReqDto editAvailableReqDto) {
        var product = productRepo.findById(editAvailableReqDto.getProduct_id()).orElseThrow();
        product.setAvailable(editAvailableReqDto.isAvailable());
        productRepo.save(product);
    }

    @Transactional
    public void changeProductShowing(EditShowingReqDto editShowingReqDto) {
        var product = productRepo.findById(editShowingReqDto.getProduct_id()).orElseThrow();
        product.setShowing(editShowingReqDto.isShowing());
        productRepo.save(product);
    }

    @Transactional
    public List<ProductModel> getRetailersProducts(RetailerIdRequest retailerIdRequest) {

        var retailer = retailersRepo.findById(retailerIdRequest.getRetailer_id()).orElseThrow();
        return productRepo.findProductModelsByOwner(retailer);
    }

    @Transactional
    public ProductModel getRetailerProduct(ProductIdRequest productIdRequest) {
        return productRepo.findById(productIdRequest.getProductId()).orElseThrow();
    }

    @Transactional
    public void deleteRetailerProduct(ProductIdRequest productIdRequest) {
        discountsRepo.deleteAllByProductId(productIdRequest.getProductId());
        productRepo.deleteById(productIdRequest.getProductId());
    }

}
