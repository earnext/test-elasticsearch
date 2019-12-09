package top.dongxibao.test.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.dongxibao.test.domain.CommonPage;
import top.dongxibao.test.entity.EsProduct;
import top.dongxibao.test.service.EsProductService;

/**
 * es-产品 信息操作处理
 * 
 * @author Dongxibao
 * @date 2019-12-08
 */
@Api(tags = "es-产品")
@RestController("esProductController")
@RequestMapping("es_product")
public class EsProductController {

	@Autowired
	private EsProductService esProductService;

	@ApiOperation(value = "创建索引及映射", notes = "")
	@GetMapping("create_index")
    ResponseEntity<Void> createIndex() {
        esProductService.createIndex();
		return ResponseEntity.ok().build();
	}

	@ApiOperation(value = "初始化ES产品数据", notes = "")
	@PostMapping("init_data")
	public ResponseEntity<Void> initData() {
		esProductService.initData();
		return ResponseEntity.ok().build();
	}

	@ApiOperation(value = "简单查询", notes = "")
	@GetMapping("page")
	public ResponseEntity<CommonPage<EsProduct>> pageList(@RequestParam(required = false) String keywords,
													@RequestParam(required = false, defaultValue = "0") Integer pageNum,
													@RequestParam(required = false, defaultValue = "10") Integer pageSize) {
		Page<EsProduct> result = esProductService.pageList(pageNum,pageSize,keywords);
		return ResponseEntity.ok(CommonPage.restPage(result));
	}

	@ApiOperation(value = "自定义查询", notes = "")
	@GetMapping("list_attr1")
	public ResponseEntity<CommonPage<EsProduct>> listAttr1(@RequestParam(required = false) String attr1,
														   @RequestParam(required = false, defaultValue = "0") Integer pageNum,
														   @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
		Page<EsProduct> result = esProductService.listAttr1(pageNum,pageSize,attr1);
		return ResponseEntity.ok(CommonPage.restPage(result));
	}

	@ApiOperation(value = "根据attr1聚合查询", notes = "")
	@GetMapping("aggs_attr1")
	public ResponseEntity<EsProduct> aggsAttr1() {
		EsProduct result = esProductService.aggsAttr1();
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "新增或修改ES产品", notes = "有id为修改，没有为新增")
	@PostMapping("save")
	public ResponseEntity<EsProduct> save(@RequestBody EsProduct esProduct) {
		EsProduct result = esProductService.save(esProduct);
		return ResponseEntity.ok(result);
	}

	@ApiOperation(value = "根据id删除文档", notes = "")
	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
		esProductService.deleteById(id);
		return ResponseEntity.ok().build();
	}
}
