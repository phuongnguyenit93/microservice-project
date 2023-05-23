package com.phuong.productservice;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phuong.productservice.dto.ProductRequest;
import com.phuong.productservice.repository.ProductRepository;

@SpringBootTest
@Testcontainers // Sử dụng jupiter để làm auto test
@AutoConfigureMockMvc // Mặc định Spring ko tạo bean Mockmvc nên phải tự tạo
class ProductServiceApplicationTests {

	@Container //để janet 5 hiểu đây là 1 container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

	// Tạo Mocked servlet env để có thể gọi đến endpoint của controller trong method
	@Autowired
	private MockMvc mockMvc;

	// Giúp convert object to json String và ngược lại
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	// Tạo MongoDB Container trong đây và set url cho container này
	// Ở đây do ta không dùng container trong application.properties mà sử dụng mongodb docker container
	// Và để properties này dynamically thì phải thêm @DynamicPropertySource để chuyển context về dynamic
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	// Khởi đầu Test sẽ download mongoDB 4.4.2 về và start container và set URL cho nó
	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productString = objectMapper.writeValueAsString(productRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
			   								  .contentType(MediaType.APPLICATION_JSON)
											  .content(productString)).andExpect(MockMvcResultMatchers.status().isCreated());
	
		Assertions.assertEquals(1, productRepository.findAll().size());
	}


	private ProductRequest getProductRequest() {
		return ProductRequest.builder().name("Iphone 14")
										.description("Iphone 14")
										.price(BigDecimal.valueOf(1200)
										).build();
	}

}
