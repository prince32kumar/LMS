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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dash.customermanagement.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	public List<Customer> findByEmailIgnoreCaseAndDeletedFalse(String email);

	public List<Customer> findByDeletedFalse();

	public Page<Customer> findByDeletedFalse(Pageable pageable);

	public Customer findByIdAndDeletedFalse(Long id);

	public Page<Customer> findByRealCustomerAndDeletedFalse(Boolean realCustomer, Pageable pageable);

	@Query("select c from Customer c where c.realCustomer = true AND c.deleted = false AND (LOWER(c.company) LIKE LOWER(CONCAT('%',:searchText,'%')) OR LOWER(c.firstname) like LOWER(CONCAT('%',:searchText,'%')) OR LOWER(c.lastname) like LOWER(CONCAT('%',:searchText,'%')) OR LOWER(c.email) like LOWER(CONCAT('%',:searchText,'%')) OR LOWER(c.customerNumber) like LOWER(CONCAT('%',:searchText,'%')))")
	public Page<Customer> findRealCustomerBySearchText(@Param(value = "searchText") String searchText,
			Pageable pageable);

	public List<Customer> findByRealCustomerAndDeletedFalse(Boolean realCustomer);

	@Query("select c from Customer c where c.deleted = false AND (LOWER(c.company) LIKE LOWER(CONCAT('%',:searchText,'%')) OR LOWER(c.firstname) like LOWER(CONCAT('%',:searchText,'%')) OR LOWER(c.lastname) like LOWER(CONCAT('%',:searchText,'%')) OR LOWER(c.email) like LOWER(CONCAT('%',:searchText,'%')) OR LOWER(c.customerNumber) like LOWER(CONCAT('%',:searchText,'%')))")
	public List<Customer> findCustomerBySearchText(@Param(value = "searchText") String searchText);

	@Query("select c from Customer c where c.deleted = false AND (LOWER(c.company) LIKE LOWER(CONCAT('%',:searchText,'%')) OR LOWER(c.firstname) like LOWER(CONCAT('%',:searchText,'%')) OR LOWER(c.lastname) like LOWER(CONCAT('%',:searchText,'%')) OR LOWER(c.email) like LOWER(CONCAT('%',:searchText,'%')) OR LOWER(c.customerNumber) like LOWER(CONCAT('%',:searchText,'%')))")
	public Page<Customer> findPageableCustomerBySearchText(
			@Param(value = "searchText") String searchText, Pageable pageable);

}
