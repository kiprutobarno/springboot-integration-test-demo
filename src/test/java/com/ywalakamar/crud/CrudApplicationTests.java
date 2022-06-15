package com.ywalakamar.crud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import com.ywalakamar.crud.model.Product;
import com.ywalakamar.crud.repository.TestProductRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CrudApplicationTests {

	@LocalServerPort
	private int port;
	private String baseUrl = "http://localhost";
	private static RestTemplate rest;

	@Autowired
	private TestProductRepository repository;

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
		int recordCount = repository.findAll().size();
		assertEquals("Earphones", response.getName());
		assertEquals(1, recordCount);

	}

	/* Test getProducts() method */
	@Test
	@Sql(statements = "INSERT INTO products (id, name, quantity, price) VALUES (2,'charger', 1, 750)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM products WHERE name='charger'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void testGetProducts() {
		ResponseEntity<List<Product>> response = rest.exchange(baseUrl, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Product>>() {

				});
		List<Product> products = response.getBody();
		assertEquals(1, products.size());
		assertEquals(1, repository.findAll().size());

	}

	/* Test getProductByName() method */
	@Test
	@Sql(statements = "INSERT INTO products (id, name, quantity, price) VALUES (3,'Samsung Galaxy S10', 1, 50000)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM products WHERE name='Samsung Galaxy S10'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void testGetProductByName() {
		ResponseEntity<Product> response = rest.exchange(baseUrl + "/name?name=Samsung Galaxy S10", HttpMethod.GET,
				null,
				new ParameterizedTypeReference<Product>() {

				});
		Product product = response.getBody();
		assertEquals("Samsung Galaxy S10", product.getName());
		assertEquals(50000.0, product.getPrice());
		assertEquals(1, product.getQuantity());

	}

}
