package flab.rocket_market.products.exception;

import flab.rocket_market.products.exception.error.CategoryErrorProperty;
import flab.rocket_market.global.exception.RocketMarketException;

public class CategoryNotFoundException extends RocketMarketException {

    public static final CategoryNotFoundException EXCEPTION = new CategoryNotFoundException();

    private CategoryNotFoundException() {
        super(CategoryErrorProperty.CATEGORY_NOT_FOUND_EXCEPTION);
    }
}
