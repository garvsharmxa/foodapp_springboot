package com.garv.foodApp.foodApp.Repository;

import com.garv.foodApp.foodApp.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByProfile_Address(String lastName);

    List<Customer> findCustomerByName(String name);

}