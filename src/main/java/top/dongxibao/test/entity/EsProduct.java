package top.dongxibao.test.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.aggregations.Aggregation;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.Map;

/**
 * es-产品表 es_product
 * 
 * @author Dongxibao
 * @date 2019-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("es-产品")
@Document(indexName = "product", type = "phone",shards = 2,replicas = 0)
public class EsProduct {

	/**  */
	@ApiModelProperty("")
	@Id
	private Long id;
	/** 这条记录所有需要被搜索信息的拼接,后面拿关键字查询匹配这个字段 */
	@Field(analyzer = "ik_max_word",type = FieldType.Text)
	private String keywordsAll;
	/** 分类id */
	@ApiModelProperty("分类id")
	@Field(type = FieldType.Long,index = false)
	private Long catalogId;
	/** 产品名称 */
	@ApiModelProperty("产品名称")
	@Field(type = FieldType.Keyword)
	private String productName;
	/** 单价 */
	@ApiModelProperty("单价")
	@Field(type = FieldType.Double)
	private BigDecimal price;
	/** 属性1 */
	@ApiModelProperty("属性1")
	@Field(type = FieldType.Keyword)
	private String attr1;
	/** 属性2 */
	@ApiModelProperty("属性2")
	@Field(type = FieldType.Keyword)
	private String attr2;
	/** 属性3 */
	@ApiModelProperty("属性3")
	@Field(type = FieldType.Keyword)
	private String attr3;

	Map<String, Object> map;
}
