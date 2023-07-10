
package dash.salemanagement.business;

import static dash.Constants.BECAUSE_OF_ILLEGAL_ID;
import static dash.Constants.BECAUSE_OF_OBJECT_IS_NULL;
import static dash.Constants.DELETE_FAILED_EXCEPTION;
import static dash.Constants.SALE_NOT_FOUND;
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
import dash.productmanagement.domain.OrderPosition;
import dash.salemanagement.domain.Sale;

@Service
public class SaleService implements ISaleService {

	private static final Logger logger = Logger.getLogger(SaleService.class);

	private SaleRepository saleRepository;

	@Autowired
	public SaleService(SaleRepository saleRepository) {
		this.saleRepository = saleRepository;
	}

	@Override
	public List<Sale> getAll() {
		return saleRepository.findAll();
	}

	@Override
	public Sale getById(final Long id) throws NotFoundException {
		if (Optional.ofNullable(id).isPresent()) {
			try {
				return saleRepository.findOne(id);
			} catch (Exception ex) {
				logger.error(SALE_NOT_FOUND + SaleService.class.getSimpleName() + BECAUSE_OF_ILLEGAL_ID, ex);
				throw new NotFoundException(SALE_NOT_FOUND);
			}
		} else {
			NotFoundException nfex = new NotFoundException(SALE_NOT_FOUND);
			logger.error(SALE_NOT_FOUND + SaleService.class.getSimpleName() + BECAUSE_OF_ILLEGAL_ID, nfex);
			throw nfex;
		}
	}

	@Override
	public Sale save(final Sale sale) throws SaveFailedException {
		if (Optional.ofNullable(sale).isPresent()) {
			try {
				if (sale != null) {
					for (OrderPosition temp : sale.getOrderPositions()) {
						temp.setWorkflow(sale);
					}
				}
				return saleRepository.save(sale);
			} catch (Exception ex) {
				logger.error(SaleService.class.getSimpleName() + ex.getMessage(), ex);
				throw new SaveFailedException(SAVE_FAILED_EXCEPTION);
			}
		} else {
			SaveFailedException sfex = new SaveFailedException(SAVE_FAILED_EXCEPTION);
			logger.error(SALE_NOT_FOUND + SaleService.class.getSimpleName() + BECAUSE_OF_OBJECT_IS_NULL, sfex);
			throw sfex;
		}
	}

	@Override
	public Sale update(final Sale sale) throws UpdateFailedException {
		if (Optional.ofNullable(sale).isPresent()) {
			try {
				if (sale != null) {
					for (OrderPosition temp : sale.getOrderPositions()) {
						temp.setWorkflow(sale);
					}
				}
				return save(sale);
			} catch (IllegalArgumentException | SaveFailedException ex) {
				logger.error(SALE_NOT_FOUND + SaleService.class.getSimpleName() + BECAUSE_OF_ILLEGAL_ID, ex);
				throw new UpdateFailedException(UPDATE_FAILED_EXCEPTION);
			}
		} else {
			UpdateFailedException ufex = new UpdateFailedException(UPDATE_FAILED_EXCEPTION);
			logger.error(SALE_NOT_FOUND + SaleService.class.getSimpleName() + BECAUSE_OF_OBJECT_IS_NULL, ufex);
			throw ufex;
		}
	}

	@Override
	public void delete(final Long id) throws DeleteFailedException {
		if (Optional.ofNullable(id).isPresent()) {
			try {
				saleRepository.delete(id);
			} catch (EmptyResultDataAccessException erdaex) {
				logger.error(SALE_NOT_FOUND + SaleService.class.getSimpleName() + erdaex.getMessage(), erdaex);
				throw new DeleteFailedException(DELETE_FAILED_EXCEPTION);
			}
		} else {
			DeleteFailedException dfex = new DeleteFailedException(DELETE_FAILED_EXCEPTION);
			logger.error(SALE_NOT_FOUND + SaleService.class.getSimpleName() + BECAUSE_OF_OBJECT_IS_NULL, dfex);
			throw dfex;
		}
	}

	@Override
	public Page<Sale> getAll(Pageable pageable) {
		return saleRepository.findAll(pageable);
	}

	@Override
	public List<Sale> getByCustomer(Long id) {
		return saleRepository.findByCustomerIdAndDeleted(id, false);
	}

	@Override
	public List<Sale> getByInvoiceNumber(String invoiceNumber) {
		return saleRepository.findByInvoiceNumber(invoiceNumber);
	}
}
