
package dash.smtpmanagement.rest;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dash.exceptions.NotFoundException;
import dash.smtpmanagement.business.ISmtpService;
import dash.smtpmanagement.domain.Smtp;
import dash.smtpmanagement.domain.SmtpContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/api/rest/smtp", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
		MediaType.APPLICATION_JSON_VALUE })
@Api(value = "Smtp API")
public class SmtpResource {

	private static final Logger logger = Logger.getLogger(SmtpResource.class);

	@Autowired
	private ISmtpService smtpService;

	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "Create a single Smtp.", notes = "You have to provide a valid Smtp entity.")
	public ResponseEntity<Object> save(@ApiParam(required = true) @RequestBody @Valid final SmtpContext smtpContext) {
		try {

			return new ResponseEntity<>(smtpService.save(smtpContext.getSmtp(), null), HttpStatus.CREATED);

		} catch (Exception e) {
			logger.error(SmtpResource.class.getSimpleName() + e.getMessage(), e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation(value = "Testing Connection.")
	@RequestMapping(value = "/{id}/test", method = RequestMethod.POST)
	public Smtp testConnection(@ApiParam(required = true) @PathVariable(required = true) final Long id,
			@RequestBody @Valid final SmtpContext smtpContext)
			throws UnsupportedEncodingException, NotFoundException, MessagingException, Exception {

		return smtpService.test(id);

	}

}
