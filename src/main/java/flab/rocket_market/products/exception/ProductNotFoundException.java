package flab.rocket_market.products.exception;

import flab.rocket_market.products.exception.error.ProductErrorProperty;
import flab.rocket_market.global.exception.RocketMarketException;

public class ProductNotFoundException extends RocketMarketException {

    public static final ProductNotFoundException EXCEPTION = new ProductNotFoundException();

    private ProductNotFoundException() {
        super(ProductErrorProperty.PRODUCT_NOT_FOUND_EXCEPTION);
    }
}
