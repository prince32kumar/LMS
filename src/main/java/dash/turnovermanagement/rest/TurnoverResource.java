/*******************************************************************************
 * Copyright (c) 2016 Eviarc GmbH.
 * All rights reserved.  
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Eviarc GmbH and its suppliers, if any.  
 * The intellectual and technical concepts contained
 * herein are proprietary to Eviarc GmbH,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Eviarc GmbH.
 *******************************************************************************/

package dash.turnovermanagement.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dash.statisticmanagement.result.domain.Result;
import dash.turnovermanagement.business.ITurnoverStatisticService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/rest/processes/statistics/turnover")
@Api(value = "Statistic Profit API")
public class TurnoverResource {

	@Autowired
	private ITurnoverStatisticService turnoverStatisticService;

	@RequestMapping(value = "/day", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Daily Turnover Statistic", notes = "")
	public Result getDailyTurnoverStatistic() {
		return turnoverStatisticService.getDailyTurnoverStatistic();
	}

	@RequestMapping(value = "/week", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Weekly Turnover Statistic", notes = "")
	public Result getWeeklyTurnoverStatistic() {
		return turnoverStatisticService.getWeeklyTurnoverStatistic();
	}

	@RequestMapping(value = "/month", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Monthly Turnover Statistic", notes = "")
	public Result getMonthlyTurnoverStatistic() {
		return turnoverStatisticService.getMonthlyTurnoverStatistic();
	}

	@RequestMapping(value = "/year", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Yearly Turnover Statistic", notes = "")
	public Result getYearlyTurnoverStatistic() {
		return turnoverStatisticService.getYearlyTurnoverStatistic();
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get All Turnover Statistic", notes = "")
	public Result getAllTurnoverStatistic() {
		return turnoverStatisticService.getAllTurnoverStatistic();
	}
}
