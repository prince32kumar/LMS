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

package dash.smtpmanagement.business;

import org.springframework.data.jpa.repository.JpaRepository;

import dash.smtpmanagement.domain.Smtp;
import dash.usermanagement.domain.User;

public interface SmtpRepository extends JpaRepository<Smtp, Long> {

	public Smtp findByUser(User user);

	public Smtp findByUserId(Long id);
}
