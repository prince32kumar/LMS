package dash.templatemanagement.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import dash.exceptions.ConsistencyFailedException;
import dash.exceptions.DeleteFailedException;
import dash.exceptions.NotFoundException;
import dash.exceptions.SaveFailedException;
import dash.exceptions.UpdateFailedException;
import dash.fileuploadmanagement.business.PdfGenerationFailedException;
import dash.messagemanagement.domain.AbstractMessage;
import dash.messagemanagement.domain.MessageContext;
import dash.security.jwt.domain.UserContext;
import dash.templatemanagement.business.ITemplateService;
import dash.templatemanagement.business.TemplateCompilationException;
import dash.templatemanagement.domain.Template;
import dash.templatemanagement.domain.WorkflowTemplateObject;
import dash.usermanagement.business.UserService;
import dash.usermanagement.domain.User;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController(value = "Template Resource")
@RequestMapping(value = "/api/rest/templates", consumes = { MediaType.ALL_VALUE }, produces = { MediaType.ALL_VALUE })
@Api(value = "Template API")
public class TemplateResource {

	private ITemplateService templateService;
	private UserService userService;

	@Autowired
	public TemplateResource(ITemplateService templateService, UserService userService) {
		this.templateService = templateService;
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get all templates. ", notes = "")
	public List<Template> getAll() {
		return templateService.getAll();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get template by Id. ", notes = "")
	public Template getById(@ApiParam(required = true) @PathVariable final long id) throws NotFoundException {
		return templateService.getById(id);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Post a template. ", notes = "")
	public Template save(@ApiParam(required = true) @RequestBody @Valid final Template template)
			throws SaveFailedException, ConsistencyFailedException {
		return templateService.save(template);
	}

	@ApiOperation(value = "Update a single template.", notes = "")
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public Template update(@ApiParam(required = true) @RequestBody @Valid final Template template)
			throws UpdateFailedException, SaveFailedException, ConsistencyFailedException {
		return templateService.save(template);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Delete a template. ", notes = "")
	public void delete(@ApiParam(required = true) @PathVariable final Long id) throws DeleteFailedException {
		templateService.delete(id);
	}

	@RequestMapping(value = "/{templateId}/offers/generate", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Generate a email content based on a template and an AbstractWorkflow.", notes = "")
	public AbstractMessage generate(@ApiParam(required = true) @PathVariable final Long templateId,
			@ApiParam(required = true) @RequestBody @Valid final MessageContext messageContext)
			throws NotFoundException, IOException, TemplateCompilationException {
		return templateService.getMessageContent(templateId, messageContext.getWorkflowTemplateObject(),
				messageContext.getNotification(), messageContext.getUser());
	}

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Generate a email content based on a template and an AbstractWorkflow.", notes = "")
	public AbstractMessage generate(@ApiParam(required = true) @RequestBody @Valid final MessageContext messageContext)
			throws NotFoundException, IOException, TemplateCompilationException {
		return templateService.getMessageContentByTemplate(messageContext.getTemplate(),
				messageContext.getWorkflowTemplateObject(), messageContext.getNotification(), messageContext.getUser());
	}

	@RequestMapping(value = "/{templateId}/offers/pdf/generate", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Generate a pdf based on a template and an offer.", notes = "")
	public Map<String, byte[]> generatePdf(@ApiParam(required = true) @PathVariable final long templateId,
			@ApiParam(required = true) @RequestBody @Valid final MessageContext messageContext)
			throws NotFoundException, IOException, TemplateCompilationException, PdfGenerationFailedException,
			TemplateException {
		Map<String, byte[]> map = new HashMap<>();
		map.put("data", templateService.getPdfBytemplateId(templateId, messageContext.getWorkflowTemplateObject(),
				messageContext.getUser()));
		return map;
	}

	@RequestMapping(value = "/test/pdf/generate", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Generate a pdf based on a template and an offer.", notes = "")
	public Map<String, byte[]> generatePdfByTemplate(@RequestBody @Valid String payload) throws NotFoundException,
			IOException, TemplateCompilationException, PdfGenerationFailedException, TemplateException, JSONException {
		JSONObject json = null;
		Template template = null;
		WorkflowTemplateObject workflowTemplateObject = null;
		User user = null;
		try {
			json = new JSONObject(payload);
			template = new ObjectMapper().readValue(json.get("template").toString(), Template.class);
			workflowTemplateObject = new ObjectMapper().readValue(json.get("workflowTemplateObject").toString(),
					WorkflowTemplateObject.class);
			user = new ObjectMapper().readValue(json.get("user").toString(), User.class);
		} catch (JSONException e) {
			throw new JSONException(e.getMessage());
		}
		Map<String, byte[]> map = new HashMap<>();
		map.put("data", templateService.getPdfBytemplate(template, workflowTemplateObject, user));
		return map;
	}

	@RequestMapping(value = "/process/pdf", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Generate a pdf based on a workflowTemplateObject.", notes = "")
	public Map<String, byte[]> exportProcessAsPDF(
			@ApiParam(required = true) @RequestBody @Valid final WorkflowTemplateObject workflowTemplateObject)
			throws NotFoundException, IOException, TemplateCompilationException, PdfGenerationFailedException,
			TemplateException {
		UserContext userContext = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, byte[]> map = new HashMap<>();
		map.put("data", templateService.exportProcessAsPDF(workflowTemplateObject,
				this.userService.getUserByEmail(userContext.getUsername())));
		return map;
	}

}