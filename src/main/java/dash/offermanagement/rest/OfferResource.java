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

package dash.offermanagement.rest;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dash.offermanagement.business.OfferRepository;
import dash.offermanagement.domain.Offer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/rest/processes/offers")
@Api(value = "Offers API")
public class OfferResource {

	@Autowired
	private OfferRepository offerRepository;

	@ApiOperation(value = "Return a single offer.", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Offer getOfferById(@PathVariable Long id) {
		return offerRepository.findOne(id);
	}

	@ApiOperation(value = "Update a single offer.", notes = "")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Offer update(@ApiParam(required = true) @PathVariable Long id, @ApiParam(required = true) @RequestBody @Valid Offer updateOffer) {
		Offer offer = offerRepository.findOne(id);

		//set prospect datas
		offer.getProspect().setFirstname(updateOffer.getProspect().getFirstname());
		offer.getProspect().setLastname(updateOffer.getProspect().getLastname());
		offer.getProspect().setCompany(updateOffer.getProspect().getCompany());
		offer.getProspect().setEmail(updateOffer.getProspect().getEmail());
		offer.getProspect().setPhone(updateOffer.getProspect().getPhone());
		offer.getProspect().setTitle(updateOffer.getProspect().getTitle());

		//set vendor datas
		offer.getVendor().setName(updateOffer.getVendor().getName());
		offer.getVendor().setPhone(updateOffer.getVendor().getPhone());

		//set container datas
		offer.getContainer().setName(updateOffer.getContainer().getName());
		offer.getContainer().setDescription(updateOffer.getContainer().getDescription());
		offer.getContainer().setPriceNetto(updateOffer.getContainer().getPriceNetto());

		//set main data
		offer.setTimestamp(updateOffer.getTimestamp());
		offer.setOfferPrice(updateOffer.getOfferPrice());
		offer.setContainerAmount(updateOffer.getContainerAmount());
		offer.setDeliveryDate(updateOffer.getDeliveryDate());
		offer.setDeliveryAddress(updateOffer.getDeliveryAddress());

		offerRepository.save(offer);

		return offer;
	}

	@ApiOperation(value = "Delete a single offer.", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void delete(@ApiParam(required = true) @PathVariable Long id) {
		offerRepository.delete(id);
	}
}
