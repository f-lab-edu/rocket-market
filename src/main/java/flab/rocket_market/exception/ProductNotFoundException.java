package flab.rocket_market.exception;

import flab.rocket_market.exception.error.ProductErrorProperty;
import flab.rocket_market.global.exception.RocketMarketException;

public class ProductNotFoundException extends RocketMarketException {

    public static final ProductNotFoundException EXCEPTION = new ProductNotFoundException();

    private ProductNotFoundException() {
        super(ProductErrorProperty.PRODUCT_NOTFOUNTD_EXCEPTION);
    }
}
