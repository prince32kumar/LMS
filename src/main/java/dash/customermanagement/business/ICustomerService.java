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
package dash.customermanagement.business;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import dash.common.ConsistencyFailedException;
import dash.customermanagement.domain.Customer;
import dash.exceptions.DeleteFailedException;
import dash.exceptions.NotFoundException;
import dash.exceptions.SaveFailedException;

@Service
public interface ICustomerService {

	public List<Customer> getAll();

	public Customer getById(final Long id) throws NotFoundException;

	public List<Customer> getByEmailIgnoreCase(String email);

	public List<Customer> getRealCustomer();

	public Customer save(final Customer inquirer) throws SaveFailedException, ConsistencyFailedException;

	public void delete(final Long id) throws DeleteFailedException;

	public Page<Customer> getAllByPage(Integer start, Integer length, String searchText, Boolean allCustomers);
	
	public List<Customer> getCustomerBySearchText(String searchText);

}
