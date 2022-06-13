package com.ywalakamar.crud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import com.ywalakamar.crud.model.Product;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CrudApplicationTests {

	@LocalServerPort
	private int port;
	private String baseUrl = "http://localhost";
	private static RestTemplate rest;

	@BeforeAll
	public static void init() {
		rest = new RestTemplate();
	}

	@BeforeEach
	public void setUp() {
		baseUrl = baseUrl.concat(":").concat(port + "").concat("/products");
	}

	@Test
	void testCreateProduct() {
		Product product = new Product("Earphones", 2, 700.00);
		Product response = rest.postForObject(baseUrl, product, Product.class);
		assertEquals("Earphones", response.getName());
	}

}
