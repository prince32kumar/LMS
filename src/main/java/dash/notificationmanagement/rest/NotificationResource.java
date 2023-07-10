
package dash.notificationmanagement.rest;

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dash.exceptions.NotFoundException;
import dash.exceptions.SaveFailedException;
import dash.notificationmanagement.business.EmailSendFailedException;
import dash.notificationmanagement.business.INotificationService;
import dash.notificationmanagement.domain.Notification;
import dash.notificationmanagement.domain.NotificationContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/api/rest/notifications", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
		MediaType.ALL_VALUE })
@Api(value = "Notifications API")
public class NotificationResource {

	private static final Logger logger = Logger.getLogger(NotificationResource.class);

	private final INotificationService notificationService;

	@Autowired
	public NotificationResource(INotificationService notificationService) {
		this.notificationService = notificationService;

	}

	@ApiOperation(value = "Send a single Notification.", notes = "")
	@RequestMapping(value = "/proccess/{processId}/user/{senderId}/send", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public Notification sendNotification(@ApiParam(required = true) @PathVariable(required = true) final Long processId,
			@ApiParam(required = true) @PathVariable(required = true) final Long senderId,
			@ApiParam(required = true) @RequestBody @Valid final NotificationContext notificationContext)
			throws NotFoundException, SaveFailedException, EmailSendFailedException {

		return notificationService.sendNotification(processId, senderId, notificationContext);

	}

	@ApiOperation(value = "Return notifications by sender.", notes = "")
	@RequestMapping(value = "/sender/{senderId}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public List<Notification> getNotificationsBySenderId(@PathVariable final Long senderId) throws NotFoundException {
		return notificationService.getNotificationsBySenderId(senderId);
	}

}
