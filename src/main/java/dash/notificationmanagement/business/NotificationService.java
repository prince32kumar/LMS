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

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dash.exceptions.NotFoundException;
import dash.exceptions.SMTPdoesntExistsException;
import dash.exceptions.SaveFailedException;
import dash.notificationmanagement.domain.Notification;
import dash.offermanagement.business.OfferService;
import dash.offermanagement.domain.Offer;
import dash.smtpmanagement.domain.Smtp;
import dash.usermanagement.business.IUserService;
import dash.usermanagement.domain.User;

@Service
public class NotificationService implements INotificationService {

	private static final Logger logger = Logger.getLogger(NotificationService.class);

	@Autowired
	private IUserService userService;

	@Autowired
	private OfferService offerService;

	@Autowired
	private NotificationRepository notificationRepository;

	@Override
	public void sendNotification(final long userId, final long offerId, final Notification notification)
			throws SMTPdoesntExistsException, MessagingException, SaveFailedException, NotFoundException {
		doSendEmail(userId, offerService.getOfferById(offerId), notification);
	}

	public void doSendEmail(final long userId, final Offer offer, final Notification notification) throws SMTPdoesntExistsException, MessagingException {
		try {
			User principle = userService.getById(userId);
			if (principle.getSmtp() != null) {

				final Session emailSession = newSession(principle.getSmtp());
				Transport transport = emailSession.getTransport("smtp");
				transport.connect();

				Message msg = new MimeMessage(emailSession);
				try {

					msg.setFrom(new InternetAddress(principle.getSmtp().getEmail()));
					msg.setRecipient(Message.RecipientType.TO, new InternetAddress(notification.getRecipient()));
					msg.setSubject(notification.getSubject());

					Multipart multipart = new MimeMultipart();

					MimeBodyPart textBodyPart = new MimeBodyPart();
					textBodyPart.setText(notification.getContent());
					multipart.addBodyPart(textBodyPart);

					if (notification.getAttachment() != null) {
						MimeBodyPart attachmentBodyPart = new MimeBodyPart();
						ByteArrayDataSource ds = new ByteArrayDataSource(notification.getAttachment().getContent(), "application/octet-stream");
						attachmentBodyPart.setDataHandler(new DataHandler(ds));
						attachmentBodyPart.setFileName(notification.getAttachment().getFilename());
						multipart.addBodyPart(attachmentBodyPart);
					}

					msg.setContent(multipart);

					Transport.send(msg);
					offer.setNotification(notification);
					offerService.save(offer);
					// notificationRepository.save(notification);
				} catch (MessagingException mex) {
					logger.error(NotificationService.class.getSimpleName(), mex);
				}

			} else {
				throw new SMTPdoesntExistsException("No valid SMTP Data for this User");
			}
		} catch (Exception ex) {
			MessagingException mex = new MessagingException("Messaging Failed");
			logger.error(NotificationService.class.getSimpleName(), mex);
			throw mex;
		}
	}

	private Session newSession(Smtp smtp) {
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", smtp.getHost());
		props.setProperty("mail.smtp.port", String.valueOf(smtp.getPort()));
		props.put("mail.smtp.ssl.trust", smtp.getHost());
		props.put("mail.smtp.auth", "true");
		final String mailUser = smtp.getUsername();
		final String mailPassword = smtp.getPassword();

		return Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailUser, mailPassword);
			}
		});
	}

}
