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

package dash.notificationmanagement.business;

import javax.mail.MessagingException;

import dash.exceptions.SMTPdoesntExistsException;
import dash.notificationmanagement.domain.Notification;

public interface INotificationService {

	void sendNotification(final long userId, Notification notification) throws SMTPdoesntExistsException, MessagingException;
}
