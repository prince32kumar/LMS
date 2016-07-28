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
package dash.containermanagement.rest.test;

import static org.junit.Assert.assertEquals;

import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dash.Application;
import dash.containermanagement.domain.Container;
import dash.test.BaseConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class ContainerIntegrationTest extends BaseConfig {

	private static final String EXTENDED_URI = BASE_URI + "/api/rest/containers";

	@Before
	public void setup() {
		headers.clear();
		headers.add("Authorization", "Basic " + base64Creds);
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	@Test
	public void postContainer() {
		Container container = createContainer();
		HttpEntity<Container> entity = new HttpEntity<Container>(container, headers);

		ResponseEntity<Container> response = restTemplate.exchange(EXTENDED_URI, HttpMethod.POST, entity, Container.class);
		Container responseContainer = response.getBody();

		assertEquals(ContentType.APPLICATION_JSON.getCharset(), response.getHeaders().getContentType().getCharSet());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(container, responseContainer);
	}

	@Test
	public void getContainer() {

		HttpEntity<Container> entityCreateContainer = new HttpEntity<Container>(createContainer(), headers);

		ResponseEntity<Container> responseCreate = restTemplate.exchange(EXTENDED_URI, HttpMethod.POST, entityCreateContainer, Container.class);
		Container responseCreateContainer = responseCreate.getBody();

		HttpEntity<Container> entityGetContainer = new HttpEntity<Container>(headers);

		ResponseEntity<Container> responseGetContainer = restTemplate.exchange(EXTENDED_URI + "/{id}", HttpMethod.GET, entityGetContainer, Container.class,
				responseCreateContainer.getId());

		assertEquals(ContentType.APPLICATION_JSON.getCharset(), responseGetContainer.getHeaders().getContentType().getCharSet());
		assertEquals(HttpStatus.OK, responseGetContainer.getStatusCode());
		assertEquals(createContainer(), responseGetContainer.getBody());
	}

	@Test
	public void updateContainer() {

		Container container = createContainer();
		HttpEntity<Container> entityCreateContainer = new HttpEntity<Container>(container, headers);

		ResponseEntity<Container> responseCreate = restTemplate.exchange(EXTENDED_URI, HttpMethod.POST, entityCreateContainer, Container.class);
		Container responseCreateContainer = responseCreate.getBody();

		container.setName("Fu�container");
		HttpEntity<Container> entity = new HttpEntity<Container>(container, headers);

		ResponseEntity<Container> response = restTemplate.exchange(EXTENDED_URI + "/{id}", HttpMethod.PUT, entity, Container.class,
				responseCreateContainer.getId());
		Container responseContainer = response.getBody();

		assertEquals(ContentType.APPLICATION_JSON.getCharset(), response.getHeaders().getContentType().getCharSet());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(container, responseContainer);
	}

	@Ignore
	public void deleteProcess() {

	}

	private Container createContainer() {

		Container container = new Container();
		container.setName("K�hlcontainer");
		container.setDescription("Dieser K�hlcontainer k�hlt am aller besten");
		container.setPriceNetto(1000.00);

		return container;
	}

}