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

package dash.statisticmanagement.profit.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dash.statisticmanagement.profit.business.IProfitStatisticService;
import dash.statisticmanagement.result.domain.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/rest/processes/statistics/profit")
@Api(value = "Statistic Profit API")
public class ProfitResource {

	@Autowired
	private IProfitStatisticService profitStatisticService;

	@RequestMapping(value = "/day", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Daily Lead Statistic", notes = "")
	public Result getDailyProfitStatistic() {
		return profitStatisticService.getDailyProfitStatistic();
	}

	@RequestMapping(value = "/week", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Weekly Profit Statistic", notes = "")
	public Result getWeeklyProfitStatistic() {
		return profitStatisticService.getWeeklyProfitStatistic();
	}

	@RequestMapping(value = "/month", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Monthly Profit Statistic", notes = "")
	public Result getMonthlyProfitStatistic() {
		return profitStatisticService.getMonthlyProfitStatistic();
	}

	@RequestMapping(value = "/year", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Yearly Profit Statistic", notes = "")
	public Result getYearlyProfitStatistic() {
		return profitStatisticService.getYearlyProfitStatistic();
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get Yearly Profit Statistic", notes = "")
	public Result getAllProfitStatistic() {
		return profitStatisticService.getAllProfitStatistic();
	}
}
