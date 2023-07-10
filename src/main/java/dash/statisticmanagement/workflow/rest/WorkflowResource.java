
package dash.statisticmanagement.workflow.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import dash.statisticmanagement.workflow.business.WorkflowStatisticService;
import dash.workflowmanagement.domain.Workflow;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/api/rest/processes/statistics/workflow", consumes = { MediaType.ALL_VALUE }, produces = {
		MediaType.APPLICATION_JSON_VALUE })
@Api(value = "Statistic Conversion API")
public class WorkflowResource {

	private static final Logger logger = Logger.getLogger(WorkflowResource.class);

	@Autowired
	private OlapRepository olapRepository;

	@Autowired
	private WorkflowStatisticService statisticsService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{workflow}/daterange/{dateRange}/source/{source}", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Statistic by workflow, dateRange and source", notes = "")
	public List<Double> getWorkflowStatisticByDateRange(
			@ApiParam(required = true) @PathVariable @Valid final Workflow workflow,
			@ApiParam(required = true) @PathVariable @Valid final DateRange dateRange,
			@ApiParam(required = true) @PathVariable @Valid String source)
			throws NotFoundException, ClassNotFoundException, IOException {
		if (CommonUtils.isNullOrEmpty(source))
			source = AbstractStatisticService.ALL_STATISTIC_KEY;

		Olap olap = olapRepository.findTopByDateRangeOrderByTimestampDesc(dateRange);
		if (olap != null) {
			logger.info("Information from OLAP.");
			byte[] byteArr = null;
			if (workflow.equals(Workflow.LEAD) && olap.getLeads() != null) {
				byteArr = olap.getLeads();
			} else if (workflow.equals(Workflow.OFFER) && olap.getOffers() != null) {
				byteArr = olap.getOffers();
			} else if (workflow.equals(Workflow.SALE) && olap.getSales() != null) {
				byteArr = olap.getSales();
			} else if (byteArr == null) {
				logger.info("Information directly calculating.");
				Map<String, List<Double>> sourceMap = statisticsService.getStatisticByDateRange(workflow, dateRange,
						null);
				if (!sourceMap.containsKey(source))
					return new ArrayList<>();
				return sourceMap.get(source);
			}
			Object obj = ByteSearializer.deserialize(byteArr);
			if (!(obj instanceof Map<?, ?>))
				return new ArrayList<>();
			Map<String, List<Double>> sourceMap = (Map<String, List<Double>>) obj;
			if (!sourceMap.containsKey(source))
				return new ArrayList<>();
			return sourceMap.get(source);
		} else {
			logger.info("Information directly calculating.");
			Map<String, List<Double>> sourceMap = statisticsService.getStatisticByDateRange(workflow, dateRange, null);
			if (!sourceMap.containsKey(source))
				return new ArrayList<>();
			return sourceMap.get(source);
		}
	}
}
