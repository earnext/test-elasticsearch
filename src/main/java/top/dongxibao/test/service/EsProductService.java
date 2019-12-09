package top.dongxibao.test.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import top.dongxibao.test.entity.EsProduct;
import top.dongxibao.test.repository.EsProductRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * es-产品 服务层实现
 * 
 * @author Dongxibao
 * @date 2019-12-08
 */
@Slf4j
@Service("esProductService")
public class EsProductService {
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	@Autowired
	private EsProductRepository esProductRepository;

	public void createIndex() {
		// 创建索引及映射
        this.elasticsearchTemplate.createIndex(EsProduct.class);
        // 配置映射
//        this.elasticsearchTemplate.putMapping(EsProduct.class);
	}

	public void initData() {
		BigDecimal price = BigDecimal.valueOf(999);
		for (int i = 0; i < 200; i++) {
			EsProduct esProduct = new EsProduct();
			esProduct.setId(Long.valueOf(i));
			esProduct.setCatalogId(i + 100L);
			esProduct.setPrice(price);
			String attr1;
			String attr2;
			String attr3;
			if (i % 2 == 0) {
				attr1 = "red";
				attr2 = "2000万像素";
				attr3 = "高通骁龙855";
				esProduct.setProductName("小米手机" + i);
			} else {
				attr1 = "blue";
				attr2 = "1200万像素";
				attr3 = "海思麒麟980";
				esProduct.setProductName("华为手机" + i);
			}
			esProduct.setAttr1(attr1);
			esProduct.setAttr2(attr2);
			esProduct.setAttr3(attr3);
			esProduct.setKeywordsAll(StringUtils.join(Arrays.asList(attr1,attr2,attr3,esProduct.getId())," "));
			price = price.add(BigDecimal.valueOf(200));
			esProductRepository.save(esProduct);
		}
	}

	/**
	 * 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<EsProduct> pageList(Integer pageNum, Integer pageSize, String keywords) {
		// 分页
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		nativeSearchQueryBuilder.withPageable(pageable);
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		if (StringUtils.isEmpty(keywords)) {
			nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
		} else {
			// 关键字查询
			boolQueryBuilder.must(QueryBuilders.matchQuery("keywordsAll", keywords));
			// 价格范围
			boolQueryBuilder.must(QueryBuilders.rangeQuery("price").from(2000D).to(6000D));
		}
		nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
		// 根据价格倒序
		nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
		NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
		return esProductRepository.search(nativeSearchQuery);
	}

	/**
	 * 根据属性1自定义查询
	 * @param pageNum
	 * @param pageSize
	 * @param attr1
	 * @return
	 */
	public Page<EsProduct> listAttr1(Integer pageNum, Integer pageSize, String attr1) {
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		Page<EsProduct> esProducts = esProductRepository.findByAttr1(attr1, pageable);
		return esProducts;
	}

	/**
	 * 根据attr1聚合查询
	 * @return
	 */
	public EsProduct aggsAttr1() {
		EsProduct esProduct = new EsProduct();
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("aggs_attr1").field("attr1"));
		NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();
		Map<String, Object> map = new HashMap<>();
		// 执行查询
		// 过elasticsearchTemplate.query()方法查询,获得聚合
		Aggregations aggregations = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());
		// 转换成Map集合
		Map<String, Aggregation> aggregationMap = aggregations.asMap();
		StringTerms stringTerms = (StringTerms) aggregationMap.get("aggs_attr1");
		List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
		buckets.forEach(bucket -> map.put(bucket.getKeyAsString(),bucket.getDocCount()));
		esProduct.setMap(map);
		return esProduct;
	}

	/**
	 * 新增或修改文档
	 * @param esProduct
	 * @return
	 */
	public EsProduct save(EsProduct esProduct) {
		EsProduct save = esProductRepository.save(esProduct);
		return save;
	}

	/**
	 * 根据id删除文档
	 * @param id
	 */
	public void deleteById(Long id) {
		esProductRepository.deleteById(id);
	}
}
