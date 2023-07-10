package dash.templatemanagement.business;

import static dash.Constants.BECAUSE_OF_ILLEGAL_ID;
import static dash.Constants.BECAUSE_OF_OBJECT_IS_NULL;
import static dash.Constants.DELETE_FAILED_EXCEPTION;
import static dash.Constants.OFFER_NOT_FOUND;
import static dash.Constants.TEMPLATE_NOT_FOUND;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import dash.consistencymanagement.business.ConsistencyService;
import dash.exceptions.ConsistencyFailedException;
import dash.exceptions.DeleteFailedException;
import dash.exceptions.NotFoundException;
import dash.fileuploadmanagement.business.HtmlToPdfService;
import dash.fileuploadmanagement.business.PdfGenerationFailedException;
import dash.messagemanagement.business.IMessageService;
import dash.messagemanagement.domain.AbstractMessage;
import dash.notificationmanagement.domain.Notification;
import dash.templatemanagement.domain.Template;
import dash.templatemanagement.domain.WorkflowTemplateObject;
import dash.usermanagement.domain.User;
import freemarker.template.TemplateException;

@Service
public class TemplateService extends ConsistencyService implements ITemplateService {

	private static final Logger logger = Logger.getLogger(TemplateService.class);

	@Autowired
	private TemplateRepository templateRepository;

	@Autowired
	private IMessageService messageService;

	@Autowired
	private HtmlToPdfService htmlToPdfService;

	@Override
	public List<Template> getAll() {
		return templateRepository.findAll();
	}

	@Override
	public Template save(final Template template) throws ConsistencyFailedException {
		if (template == null) {
			logger.error(TEMPLATE_NOT_FOUND + TemplateService.class.getSimpleName() + BECAUSE_OF_OBJECT_IS_NULL);
			throw new IllegalArgumentException(
					TEMPLATE_NOT_FOUND + TemplateService.class.getSimpleName() + BECAUSE_OF_OBJECT_IS_NULL);
		}
		this.checkConsistencyAndSetTimestamp(template, templateRepository);
		return templateRepository.save(template);
	}

	@Override
	public void delete(final long id) throws DeleteFailedException {
		if (Optional.ofNullable(id).isPresent()) {
			try {
				templateRepository.delete(id);
			} catch (EmptyResultDataAccessException erdaex) {
				logger.error(TEMPLATE_NOT_FOUND + TemplateService.class.getSimpleName() + erdaex.getMessage(), erdaex);
				throw new DeleteFailedException(DELETE_FAILED_EXCEPTION);
			}
		} else {
			DeleteFailedException dfex = new DeleteFailedException(DELETE_FAILED_EXCEPTION);
			logger.error(TEMPLATE_NOT_FOUND + TemplateService.class.getSimpleName() + BECAUSE_OF_OBJECT_IS_NULL, dfex);
			throw dfex;
		}

	}

	@Override
	public Template getById(final long id) throws NotFoundException {
		if (Optional.ofNullable(id).isPresent()) {
			try {
				return templateRepository.findOne(id);
			} catch (Exception ex) {
				logger.error(TEMPLATE_NOT_FOUND + TemplateService.class.getSimpleName() + BECAUSE_OF_ILLEGAL_ID, ex);
				throw new NotFoundException(TEMPLATE_NOT_FOUND);
			}
		} else {
			NotFoundException nfex = new NotFoundException(TEMPLATE_NOT_FOUND);
			logger.error(TEMPLATE_NOT_FOUND + TemplateService.class.getSimpleName() + BECAUSE_OF_ILLEGAL_ID, nfex);
			throw nfex;
		}
	}

	@Override
	public AbstractMessage getMessageContent(final long templateId, final WorkflowTemplateObject workflowTemplateObject,
			final Notification notification, final User user)
			throws NotFoundException, IOException, TemplateCompilationException {
		return getMessageContentByTemplate(getById(templateId), workflowTemplateObject, notification, user);
	}

	@Override
	public AbstractMessage getMessageContentByTemplate(final Template template,
			final WorkflowTemplateObject workflowTemplateObject, final Notification notification, final User user)
			throws NotFoundException, IOException, TemplateCompilationException {
		if (workflowTemplateObject != null && template != null) {
			try {
				return messageService.getMessageContent(workflowTemplateObject, template.getContent(), notification,
						user, template.getId());
			} catch (NotFoundException ex) {
				logger.error(OFFER_NOT_FOUND + TemplateService.class.getSimpleName() + BECAUSE_OF_ILLEGAL_ID, ex);
				throw ex;
			} catch (TemplateException ex) {
				throw new TemplateCompilationException(ex.getFTLInstructionStack());
			} catch (IOException ex) {
				throw new TemplateCompilationException(ex.getMessage());
			}

		} else {
			NotFoundException nfex = new NotFoundException(OFFER_NOT_FOUND);
			logger.error(OFFER_NOT_FOUND + TemplateService.class.getSimpleName() + BECAUSE_OF_ILLEGAL_ID, nfex);
			throw nfex;
		}
	}

	@Override
	public String getMessageContentStringByTemplateId(final long templateId,
			final WorkflowTemplateObject workflowTemplateObject, final User user)
			throws NotFoundException, IOException, TemplateException, TemplateCompilationException {

		if (workflowTemplateObject != null) {
			try {
				return messageService.getMessageContentString(workflowTemplateObject, getById(templateId).getContent(),
						user, templateId);

			} catch (NotFoundException ex) {
				logger.error(OFFER_NOT_FOUND + TemplateService.class.getSimpleName() + BECAUSE_OF_ILLEGAL_ID, ex);
				throw ex;
			} catch (TemplateException ex) {
				throw new TemplateCompilationException(ex.getFTLInstructionStack());
			} catch (IOException ex) {
				throw new TemplateCompilationException(ex.getMessage());
			}

		} else {
			NotFoundException nfex = new NotFoundException(OFFER_NOT_FOUND);
			logger.error(OFFER_NOT_FOUND + TemplateService.class.getSimpleName() + BECAUSE_OF_ILLEGAL_ID, nfex);
			throw nfex;
		}

	}

	@Override
	public String getMessageContentStringByTemplate(final Template template,
			final WorkflowTemplateObject workflowTemplateObject, final User user)
			throws NotFoundException, IOException, TemplateException, TemplateCompilationException {

		if (workflowTemplateObject != null) {
			try {
				return messageService.getMessageContentString(workflowTemplateObject, template.getContent(), user,
						null);

			} catch (NotFoundException ex) {
				logger.error(OFFER_NOT_FOUND + TemplateService.class.getSimpleName() + BECAUSE_OF_ILLEGAL_ID, ex);
				throw ex;
			} catch (TemplateException ex) {
				throw new TemplateCompilationException(ex.getFTLInstructionStack());
			} catch (IOException ex) {
				throw new TemplateCompilationException(ex.getMessage());
			}

		} else {
			NotFoundException nfex = new NotFoundException(OFFER_NOT_FOUND);
			logger.error(OFFER_NOT_FOUND + TemplateService.class.getSimpleName() + BECAUSE_OF_ILLEGAL_ID, nfex);
			throw nfex;
		}

	}

	@Override
	public byte[] getPdfBytemplateId(final long templateId, final WorkflowTemplateObject workflowTemplateObject,
			final User user) throws NotFoundException, IOException, TemplateCompilationException,
			PdfGenerationFailedException, TemplateException {
		String message = getMessageContentStringByTemplateId(templateId, workflowTemplateObject, user);
		return htmlToPdfService.genereatePdfFromHtml(message);
	}
	
	@Override
	public byte[] getPdfBytemplate(final Template template, final WorkflowTemplateObject workflowTemplateObject,
			final User user) throws NotFoundException, IOException, TemplateCompilationException,
			PdfGenerationFailedException, TemplateException {
		String message = getMessageContentStringByTemplate(template, workflowTemplateObject, user);
		return htmlToPdfService.genereatePdfFromHtml(message);
	}


	@Override
	public byte[] exportProcessAsPDF(WorkflowTemplateObject workflowTemplateObject, final User user)
			throws TemplateException, IOException, PdfGenerationFailedException {
		return htmlToPdfService.genereatePdfFromHtml(messageService.exportProcessAsPDF(workflowTemplateObject, user));
	}

}
