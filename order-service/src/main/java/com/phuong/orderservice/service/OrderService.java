package com.phuong.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.phuong.orderservice.dto.InventoryResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.phuong.orderservice.dto.OrderLineItemsDto;
import com.phuong.orderservice.dto.OrderRequest;
import com.phuong.orderservice.model.Order;
import com.phuong.orderservice.model.OrderLineItems;
import com.phuong.orderservice.repository.OrderRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional
public class OrderService {

    @Autowired // Hoặc dùng RequiredArgsConstructor
    OrderRepository orderRepository;

    @Autowired
    WebClient.Builder webClientBuilder;

    @Transactional
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        
        List<OrderLineItems> orderLineItems =  orderRequest.getOrderLineItemsDtoList()
                                                    .stream()
                                                    .map(orderLineItem -> mapToDto(orderLineItem,order))
                                                    .toList();
        order.setOrderLineItemList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemList().stream()
                .map(OrderLineItems::getSkuCode) //.map(orderLineItem -> orderLineItem.getSkuCode());
                .toList();
        // Call Inventory service, and place order if item is in stock
        InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("sku-code",skuCodes).build())
                    .retrieve() // To get the response
                    .bodyToMono(InventoryResponse[].class) // To Casting the response type result
                    .block(); // Web client will make a synchronous request

        // Check every reponse is true or not
        assert inventoryResponsesArray != null;
        boolean isInStock = Arrays.stream(inventoryResponsesArray)
                    .allMatch(InventoryResponse::isInStock);

        if (isInStock) {
            orderRepository.save(order);
            return "Order Placed Successfully";
        } else {
            throw new IllegalArgumentException("Product out of stock. Please try again later");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderlineitemsdto,Order order) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderlineitemsdto.getPrice());
        orderLineItems.setQuantity(orderlineitemsdto.getQuantity());
        orderLineItems.setSkuCode(orderlineitemsdto.getSkuCode());
        orderLineItems.setOrder(order);
        return orderLineItems;
    }
}
