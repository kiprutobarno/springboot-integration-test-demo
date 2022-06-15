package com.ywalakamar.crud;

import static org.junit.jupiter.api.Assertions.*;

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
	@Sql(statements = "TRUNCATE TABLE products", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
	@Sql(statements = "TRUNCATE TABLE products", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
	@Sql(statements = "TRUNCATE TABLE products", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void testGetProductByName() {
		Product product = rest.getForObject(baseUrl + "/name?name=Samsung Galaxy S10", Product.class);
		assertEquals("Samsung Galaxy S10", product.getName());
		assertEquals(50000.0, product.getPrice());
		assertEquals(1, product.getQuantity());

	}

	/* Test updateProduct() method */
	@Test
	@Sql(statements = "INSERT INTO products (id, name, quantity, price) VALUES (8,'Mac Book Pro', 3, 250000.00)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "TRUNCATE TABLE products", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void testUpdateProduct() {
		Product product = new Product("Mac Book Air", 4, 180000.00);
		rest.put(baseUrl + "/update/{id}", product, 8);

		Product dbProduct = repository.findById(8).get();
		assertAll(
				() -> assertNotNull(dbProduct),
				() -> assertEquals("Mac Book Air", dbProduct.getName()),
				() -> assertEquals(180000.00, dbProduct.getPrice()),
				() -> assertEquals(4, dbProduct.getQuantity()));

	}

}
