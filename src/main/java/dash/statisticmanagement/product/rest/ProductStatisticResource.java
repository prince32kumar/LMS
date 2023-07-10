
package dash.statisticmanagement.product.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dash.common.ByteSearializer;
import dash.common.CommonUtils;
import dash.exceptions.NotFoundException;
import dash.statisticmanagement.common.AbstractStatisticService;
import dash.statisticmanagement.domain.DateRange;
import dash.statisticmanagement.olap.business.OlapRepository;
import dash.statisticmanagement.olap.domain.Olap;
import dash.statisticmanagement.product.business.ProductStatistic;
import dash.statisticmanagement.product.business.ProductStatisticService;
import dash.workflowmanagement.domain.Workflow;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/rest/processes/statistics/product")
@Api(value = "Statistic Profit API")
public class ProductStatisticResource {

	private static final Logger logger = Logger.getLogger(ProductStatisticResource.class);

	@Autowired
	private OlapRepository olapRepository;

	@Autowired
	private ProductStatisticService productStatisticService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{workflow}/daterange/{dateRange}/source/{source}", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Statistic by workflow, dateRange and source", notes = "")
	public List<ProductStatistic> getProductStatisticByDateRange(
			@ApiParam(required = true) @PathVariable @Valid final Workflow workflow,
			@ApiParam(required = true) @PathVariable @Valid final DateRange dateRange,
			@ApiParam(required = true) @PathVariable @Valid String source)
			throws NotFoundException, ClassNotFoundException, IOException {
		if (CommonUtils.isNullOrEmpty(source))
			source = AbstractStatisticService.ALL_STATISTIC_KEY;

		Olap olap = olapRepository.findTopByDateRangeOrderByTimestampDesc(dateRange);
		if (olap != null && olap.getProducts() != null) {
			logger.info("Infromation from OLAP.");
			Object obj = ByteSearializer.deserialize(olap.getProducts());
			if (!(obj instanceof Map<?, ?>))
				return new ArrayList<>();
			Map<Workflow, Map<String, List<ProductStatistic>>> sourceMap = (Map<Workflow, Map<String, List<ProductStatistic>>>) obj;
			if (!sourceMap.containsKey(Workflow.SALE) || !sourceMap.get(Workflow.SALE).containsKey(source))
				return new ArrayList<>();
			return sourceMap.get(Workflow.SALE).get(source);
		} else {
			logger.info("Infromation directly calculating.");
			Map<String, List<ProductStatistic>> sourceMap = productStatisticService.getTopProductStatstic(workflow,
					dateRange, null);
			if (!sourceMap.containsKey(source))
				return new ArrayList<>();
			return sourceMap.get(source);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{workflow}/daterange/{dateRange}/source/{source}/id/{id}", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Statistic by dateRange and workflow", notes = "")
	public ProductStatistic getSingleProductStatistic(
			@ApiParam(required = true) @PathVariable @Valid final Workflow workflow,
			@ApiParam(required = true) @PathVariable @Valid final DateRange dateRange,
			@ApiParam(required = true) @PathVariable @Valid String source,
			@ApiParam(required = true) @PathVariable @Valid final Long id)
			throws NotFoundException, ClassNotFoundException, IOException {
		if (CommonUtils.isNullOrEmpty(source))
			source = AbstractStatisticService.ALL_STATISTIC_KEY;

		Olap olap = olapRepository.findTopByDateRangeOrderByTimestampDesc(dateRange);
		if (olap != null && olap.getProducts() != null) {
			logger.info("Infromation from OLAP.");
			Object obj = ByteSearializer.deserialize(olap.getProducts());
			if (!(obj instanceof Map<?, ?>))
				return new ProductStatistic();
			Map<Workflow, Map<String, List<ProductStatistic>>> sourceMap = (Map<Workflow, Map<String, List<ProductStatistic>>>) obj;
			if (!sourceMap.containsKey(workflow) || !sourceMap.get(workflow).containsKey(source))
				return new ProductStatistic();
			ProductStatistic productStatisticReturn = new ProductStatistic();
			for (ProductStatistic productStatistic : sourceMap.get(workflow).get(source)) {
				if (productStatistic.getProduct().getId().equals(id)) {
					productStatisticReturn = productStatistic;
					break;
				}
			}
			return productStatisticReturn;
		} else {
			logger.info("Infromation directly calculating.");
			ProductStatistic productStatistic = new ProductStatistic();
			Map<String, List<ProductStatistic>> sourceMap = productStatisticService.getTopProductStatstic(workflow,
					dateRange, id);
			if (!sourceMap.containsKey(source))
				return productStatistic;
			if (sourceMap.get(source).size() > 0)
				productStatistic = sourceMap.get(source).get(0);
			return productStatistic;
		}

	}
}
