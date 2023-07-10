
package dash.offermanagement.business;

import static dash.Constants.BECAUSE_OF_ILLEGAL_ID;
import static dash.Constants.BECAUSE_OF_OBJECT_IS_NULL;
import static dash.Constants.DELETE_FAILED_EXCEPTION;
import static dash.Constants.OFFER_NOT_FOUND;
import static dash.Constants.SAVE_FAILED_EXCEPTION;
import static dash.Constants.UPDATE_FAILED_EXCEPTION;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dash.exceptions.DeleteFailedException;
import dash.exceptions.NotFoundException;
import dash.exceptions.SaveFailedException;
import dash.exceptions.UpdateFailedException;
import dash.offermanagement.domain.Offer;
import dash.productmanagement.domain.OrderPosition;

@Service
public class OfferService implements IOfferService {

	private static final Logger logger = Logger.getLogger(OfferService.class);

	@Autowired
	private OfferRepository offerRepository;

	@Override
	public List<Offer> getAll() {
		return offerRepository.findAll();
	}

	@Override
	public Offer getOfferById(final long id) throws NotFoundException {
		if (Optional.ofNullable(id).isPresent()) {
			return offerRepository.findOne(id);
		} else {
			NotFoundException nfex = new NotFoundException(OFFER_NOT_FOUND);
			logger.error(OFFER_NOT_FOUND + OfferService.class.getSimpleName() + BECAUSE_OF_ILLEGAL_ID, nfex);
			throw nfex;
		}
	}

	@Override
	public Offer save(final Offer offer) throws SaveFailedException {
		if (Optional.ofNullable(offer).isPresent()) {
			if (offer != null) {
				for (OrderPosition temp : offer.getOrderPositions()) {
					temp.setWorkflow(offer);
				}
			}
			return offerRepository.save(offer);
		} else {
			SaveFailedException sfex = new SaveFailedException(SAVE_FAILED_EXCEPTION);
			logger.error(OFFER_NOT_FOUND + OfferService.class.getSimpleName() + BECAUSE_OF_OBJECT_IS_NULL, sfex);
			throw sfex;
		}
	}

	@Override
	public Offer update(final Offer offer) throws UpdateFailedException {
		if (Optional.ofNullable(offer).isPresent()) {
			try {
				if (offer != null) {
					for (OrderPosition temp : offer.getOrderPositions()) {
						temp.setWorkflow(offer);
					}
				}
				return save(offer);
			} catch (IllegalArgumentException | SaveFailedException ex) {
				logger.error(ex.getMessage() + OfferService.class.getSimpleName(), ex);
				throw new UpdateFailedException(UPDATE_FAILED_EXCEPTION);
			}
		} else {
			UpdateFailedException ufex = new UpdateFailedException(UPDATE_FAILED_EXCEPTION);
			logger.error(OFFER_NOT_FOUND + OfferService.class.getSimpleName() + BECAUSE_OF_OBJECT_IS_NULL, ufex);
			throw ufex;
		}
	}

	@Override
	public void delete(final long id) throws DeleteFailedException {
		if (Optional.ofNullable(id).isPresent()) {
			try {
				offerRepository.delete(id);
			} catch (EmptyResultDataAccessException erdaex) {
				logger.error(OFFER_NOT_FOUND + OfferService.class.getSimpleName() + erdaex.getMessage(), erdaex);
				throw new DeleteFailedException(DELETE_FAILED_EXCEPTION);
			}
		} else {
			DeleteFailedException dfex = new DeleteFailedException(DELETE_FAILED_EXCEPTION);
			logger.error(OFFER_NOT_FOUND + OfferService.class.getSimpleName() + BECAUSE_OF_OBJECT_IS_NULL, dfex);
			throw dfex;
		}
	}

	@Override
	public Page<Offer> getAll(Pageable pageable) {
		return offerRepository.findAll(pageable);
	}

	@Override
	public List<Offer> getByCustomer(final long id) {
		return offerRepository.findByCustomerIdAndDeleted(id, false);
	}

}
