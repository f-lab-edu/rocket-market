package flab.rocket_market.products.repository;

import flab.rocket_market.products.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Categories, Long> {
}
