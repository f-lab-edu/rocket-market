package flab.rocket_market.repository;

import flab.rocket_market.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Products, Long> {

    @Override
    @EntityGraph(attributePaths = "category")
    Page<Products> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Page<Products> findByNameContaining(String keyword, Pageable pageable);
}
