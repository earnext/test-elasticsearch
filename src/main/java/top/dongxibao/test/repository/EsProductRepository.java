package top.dongxibao.test.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import top.dongxibao.test.entity.EsProduct;

import java.util.List;

/**
 * @ClassName EsProductRepository
 * @description ES商品操作类
 * @author Dongxibao
 * @date 2019/12/8
 * @Version 1.0
 */
public interface EsProductRepository extends ElasticsearchRepository<EsProduct, Long> {
    /**
     * 根据属性1查询
     * @param attr1
     * @param pageable
     * @return
     */
    Page<EsProduct> findByAttr1(String attr1, Pageable pageable);
}
