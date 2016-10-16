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

package dash.messagemanagement.business;

import java.io.IOException;

import dash.exceptions.NotFoundException;
import dash.messagemanagement.domain.AbstractMessage;
import dash.offermanagement.domain.Offer;
import freemarker.template.TemplateException;

public interface IMessageService {

	String getRecipient();

	String getSubject();

	AbstractMessage getOfferContent(final Offer offer, String templateWithPlaceholders) throws IOException, NotFoundException, TemplateException;
}