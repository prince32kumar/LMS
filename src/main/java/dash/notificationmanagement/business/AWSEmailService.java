package dash.notificationmanagement.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;

import dash.fileuploadmanagement.domain.FileUpload;
import dash.messagemanagement.business.MailBuilder;
import dash.notificationmanagement.domain.Attachment;

@Service
public class AWSEmailService {

	private static final String CLASSPATH_RESOURCE_IMAGE_EMPLOYEE = "classpath:email/images/Max_Mustermann.png";
	private static final String CLASSPATH_RESOURCE_IMAGE_LOGO = "classpath:email/images/logo.png";

	private static final Logger logger = Logger.getLogger(AWSEmailService.class);

	private AmazonSimpleEmailServiceClient amazonSimpleEmailServiceClient;
	private ResourceLoader resourceLoader;

	@Autowired
	public AWSEmailService(AmazonSimpleEmailServiceClient amazonSimpleEmailServiceClient,
			ResourceLoader resourceLoader) {
		this.amazonSimpleEmailServiceClient = amazonSimpleEmailServiceClient;
		this.resourceLoader = resourceLoader;
	}

	public void send(final String fromEmail, final String toEmail, final String subject, final String emailBody)
			throws MessagingException {
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage message = new MimeMessage(session);
		message.setSubject(subject, "UTF-8");
		message.setFrom(new InternetAddress(fromEmail));
		message.setReplyTo(new Address[] { new InternetAddress(fromEmail) });
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
		message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(fromEmail));

		// Cover wrap
		MimeBodyPart wrap = new MimeBodyPart();

		// Alternative TEXT/HTML content
		MimeMultipart cover = new MimeMultipart("alternative");
		MimeBodyPart html = new MimeBodyPart();
		cover.addBodyPart(html);

		wrap.setContent(cover);

		MimeMultipart content = new MimeMultipart("related");
		message.setContent(content);
		content.addBodyPart(wrap);

		Resource resourceEmployee = resourceLoader.getResource(CLASSPATH_RESOURCE_IMAGE_EMPLOYEE);
		Resource resourceLogo = resourceLoader.getResource(CLASSPATH_RESOURCE_IMAGE_LOGO);

		Map<String, Resource> attachmentsFiles = new HashMap<>();
		attachmentsFiles.put("employee", resourceEmployee);
		attachmentsFiles.put("logo", resourceLogo);

		for (Map.Entry<String, Resource> entry : attachmentsFiles.entrySet()) {

			MimeBodyPart attachment = new MimeBodyPart();

			DataSource fds;
			try {
				fds = new ByteArrayDataSource(entry.getValue().getInputStream(), "image/png");
				attachment.setDataHandler(new DataHandler(fds));
				attachment.setHeader("Content-ID", "<" + entry.getKey() + ">");
				attachment.setFileName(fds.getName());
			} catch (IOException e) {
				logger.error("Error creating FileDataSource Object from File.", e);
			}

			content.addBodyPart(attachment);
		}

		html.setContent(emailBody, "text/html; charset=utf-8");
		try {
			// Send the email.
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			message.writeTo(outputStream);
			RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

			SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);

			this.amazonSimpleEmailServiceClient.sendRawEmail(rawEmailRequest);
		} catch (Exception e) {
			logger.error("Error connecting to AWS SES.", e);
		}
	}

	public void sendMail(final String fromEmail, final String toEmail, final String subject, final String emailBody)
			throws MessagingException {

		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage message = new MimeMessage(session);
		message.setSubject(subject, "UTF-8");
		message.setFrom(new InternetAddress(fromEmail));
		message.setReplyTo(new Address[] { new InternetAddress(fromEmail) });
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
		message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(fromEmail));

		Multipart email = MailBuilder.build("", emailBody, getInlineAttachments(), null);
		message.setContent(email, "text/html; charset=utf-8");

		try {
			// Send the email.
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			message.writeTo(outputStream);
			RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

			SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);

			this.amazonSimpleEmailServiceClient.sendRawEmail(rawEmailRequest);
		} catch (Exception e) {
			logger.error("Error connecting to AWS SES.", e);
		}
	}

	private Set<Attachment> getInlineAttachments() {
		Set<Attachment> attachments = new HashSet<>();

		Resource resourceEmployee = resourceLoader.getResource(CLASSPATH_RESOURCE_IMAGE_EMPLOYEE);
		Resource resourceLogo = resourceLoader.getResource(CLASSPATH_RESOURCE_IMAGE_LOGO);

		Map<String, Resource> attachmentsFiles = new HashMap<>();
		attachmentsFiles.put("employee", resourceEmployee);
		attachmentsFiles.put("logo", resourceLogo);

		try {
			for (Map.Entry<String, Resource> entry : attachmentsFiles.entrySet()) {
				Attachment attachment = new Attachment();
				FileUpload file = new FileUpload();
				file.setFilename(entry.getKey());
				file.setMimeType("image/png");
				file.setContent(IOUtils.toByteArray(entry.getValue().getInputStream()));

				attachment.setFileUpload(file);
				attachments.add(attachment);
			}

		} catch (IOException e) {
			logger.error("Cannot find images. ", e);
		}
		return attachments;
	}
}
