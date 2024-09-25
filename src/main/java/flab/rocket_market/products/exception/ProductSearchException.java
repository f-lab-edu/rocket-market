package flab.rocket_market.products.exception;

import flab.rocket_market.global.exception.RocketMarketException;
import flab.rocket_market.products.exception.error.ProductErrorProperty;

public class ProductSearchException extends RocketMarketException {

    public static final ProductSearchException EXCEPTION = new ProductSearchException();

    private ProductSearchException() {
        super(ProductErrorProperty.PRODUCT_SEARCH_EXCEPTION);
    }
}
