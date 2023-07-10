
package dash.statisticmanagement.olap.rest;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dash.exceptions.NotFoundException;
import dash.statisticmanagement.domain.DateRange;
import dash.statisticmanagement.olap.business.OlapStatisticService;
import dash.statisticmanagement.olap.domain.Olap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/api/rest/processes/statistics/generate")
@Api(value = "Statistic generate API")
public class OlapResource {

	@Autowired
	private OlapStatisticService olapStatisticService;

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Generate Statistics", notes = "")
	public void getAllStatisticsByDateRange() throws NotFoundException {
		olapStatisticService.generateOlapStatistics();
	}

	@RequestMapping(value = "/timestamp/{dateRange}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Generate Statistics", notes = "")
	JSONObject getOlapTimestampByDateRange(@ApiParam(required = true) @PathVariable @Valid final DateRange dateRange) {
		Olap olap = this.olapStatisticService.getLastOlapByDateRange(dateRange);
		JSONObject jsonObj = new JSONObject();
		if (olap != null) {
			jsonObj.put("timestamp", olap.getTimestamp());
		}
		return jsonObj;
	}

}
