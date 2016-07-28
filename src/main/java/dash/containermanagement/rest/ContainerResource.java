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

package dash.containermanagement.rest;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dash.containermanagement.business.ContainerRepository;
import dash.containermanagement.domain.Container;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Component
@RestController
@RequestMapping("/api/rest/containers")
@Api(value = "containers")
public class ContainerResource {

	@Autowired
	private ContainerRepository containerRepository;

	@ApiOperation(value = "Get all containers.", notes = "")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Iterable<Container> get() {
		return containerRepository.findAll();
	}

	@ApiOperation(value = "Get a single container.", notes = "You have to provide a valid container ID.")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Container findById(@ApiParam(required = true) @PathVariable Long id) {
		return containerRepository.findOne(id);
	}

	@ApiOperation(value = "Add a single container.", notes = "You have to provide a valid Container Object")
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public Container add(@ApiParam(required = true) @RequestBody @Valid final Container container) {
		return containerRepository.save(container);
	}

	@ApiOperation(value = "Update a single container.", notes = "")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public Container update(@ApiParam(required = true) @PathVariable Long id, @ApiParam(required = true) @RequestBody final Container updateContainer) {
		Container container = containerRepository.findOne(id);
		container.setName(updateContainer.getName());
		container.setDescription(updateContainer.getDescription());
		container.setPriceNetto(updateContainer.getPriceNetto());
		return containerRepository.save(container);
	}

	@ApiOperation(value = "Delete a single container.", notes = "")
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public void delete(@ApiParam(required = true) @PathVariable Long id) {
		containerRepository.delete(id);
	}
}