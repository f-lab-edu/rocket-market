package flab.rocket_market.entity;

import flab.rocket_market.service.ProductsSearchService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductsEntityListener {

    @Autowired
    private ProductsSearchService productsSearchService;

    @PostPersist
    @PostUpdate
    public void postPersistOrUpdate(Products products) {
        productsSearchService.saveProductsToElasticsearch(products);
    }

    @PostRemove
    public void postRemove(Products products) {
        productsSearchService.deleteProductsFromElasticsearch(products.getProductId());
    }
}
