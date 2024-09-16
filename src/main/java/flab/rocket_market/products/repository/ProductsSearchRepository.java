package flab.rocket_market.products.repository;

import flab.rocket_market.products.document.ProductsDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsSearchRepository extends ElasticsearchRepository<ProductsDocument, Long> {
    Page<ProductsDocument> findByNameContaining(String keyword, Pageable pageable);
}
