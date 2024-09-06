package flab.rocket_market.repository;

import flab.rocket_market.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Products, Long> {

    @Override
    @EntityGraph(attributePaths = "category")
    Page<Products> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "category")
    List<Products> findAll();

    @EntityGraph(attributePaths = "category")
    Page<Products> findByNameContaining(String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM products p WHERE MATCH(p.name) AGAINST(:keyword IN BOOLEAN MODE)",
            countQuery = "SELECT COUNT(*) FROM products p WHERE MATCH(p.name) AGAINST(:keyword IN BOOLEAN MODE)",
            nativeQuery = true)
    Page<Products> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
