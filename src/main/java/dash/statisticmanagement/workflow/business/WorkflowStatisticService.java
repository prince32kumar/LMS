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

package dash.statisticmanagement.workflow.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import dash.processmanagement.request.Request;
import dash.statisticmanagement.common.AbstractStatisticService;

@Service
public class WorkflowStatisticService extends AbstractStatisticService {

	@Override
	public List<Double> buildStatistic(Map<String, Double> calendarMap, List<Request> requests) {
		for (Request request : requests) {
			Calendar timeStamp = request.getTimestamp();
			String key = statisticHelper.getKeyByDateRange(timeStamp, statisticHelper.getDateRange());
			if (calendarMap.containsKey(key)) {
				double value = calendarMap.get(key) + 1.00;
				calendarMap.put(key, value);
			}
		}
		return new ArrayList<>(calendarMap.values());
	}
}