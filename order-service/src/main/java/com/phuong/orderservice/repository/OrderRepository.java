package com.phuong.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.phuong.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order,Long> {
    
}
