package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.CustomerDTO;
import com.garv.foodApp.foodApp.Entity.Customer;
import com.garv.foodApp.foodApp.Exception.CustomerNotFoundException;
import com.garv.foodApp.foodApp.Repository.CustomerRepository;
import com.garv.foodApp.foodApp.mapper.CustomerMappers;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service // Marks this class as a Spring Service (business logic layer)
public class CustomerService {

    // Injecting dependencies: Repository (DB access) + Mapper (DTO ↔ Entity conversion)
    private final CustomerRepository customerRepository;
    private final CustomerMappers customerMappers;

    /**
     * Constructor injection is recommended in Spring because:
     * - It makes the class immutable
     * - Easier to test (can pass mocks in unit tests)
     * - Ensures required dependencies are always provided
     */
    public CustomerService(CustomerRepository customerRepository, CustomerMappers customerMappers) {
        this.customerRepository = customerRepository;
        this.customerMappers = customerMappers;
    }

    /**
     * CREATE a new customer.
     * - Convert DTO → Entity using MapStruct
     * - Save Entity into DB using JPA repository
     * - Convert saved Entity → DTO for API response
     */
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMappers.toCustomer(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMappers.toCustomerDTO(savedCustomer);
    }

    /**
     * READ: Get all customers (unsorted).
     * - Fetch all customers from DB
     * - Convert each Entity → DTO using MapStruct
     * - Return as a list
     */
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMappers::toCustomerDTO)
                .toList();
    }

    /**
     * READ: Get a single customer by ID.
     * - Searches in DB
     * - If not found → throws CustomerNotFoundException
     * - If found → converts Entity → DTO
     *
     * @Cacheable: caches result in "Customer" cache with key = customerId
     * so repeated requests don’t hit the DB
     */
    @Cacheable(value = "Customer", key = "#id")
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return customerMappers.toCustomerDTO(customer);
    }

    /**
     * PARTIAL UPDATE (PATCH):
     * - Loads existing customer from DB
     * - Updates only non-null fields from DTO → Entity (MapStruct handles this)
     * - Saves updated Entity back to DB
     * - Returns updated DTO
     */
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customerMappers.updateCustomerFromDTO(customerDTO, existingCustomer);

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMappers.toCustomerDTO(updatedCustomer);
    }

    /**
     * FULL UPDATE (PUT):
     * - Loads existing customer from DB
     * - Replaces all fields (including null values) with DTO data
     * - Saves updated Entity back to DB
     * - Returns updated DTO
     */
    public CustomerDTO updateFullCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customerMappers.fullUpdateCustomerFromDTO(customerDTO, existingCustomer);

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMappers.toCustomerDTO(updatedCustomer);
    }

    /**
     * READ ALL with Sorting:
     * - Allows sorting by "name", "email", or "id"
     * - Defaults to sorting by "id" if invalid field provided
     */
    public List<CustomerDTO> getAllSortCustomers(String sort) {
        if (!Set.of("name", "email","id").contains(sort)) {
            sort = "id"; // fallback
        }

        return customerRepository.findAll(Sort.by(sort))
                .stream()
                .map(customerMappers::toCustomerDTO)
                .toList();
    }

    /**
     * READ: Get all customers by address.
     * - Uses a custom repository query (findByProfile_Address)
     * - Converts results to DTO list
     */
    public List<CustomerDTO> getCustomersByAddress(String address) {
        return customerRepository.findByProfile_Address(address)
                .stream()
                .map(customerMappers::toCustomerDTO)
                .toList();
    }

    /**
     * READ: Get all customers by name.
     * - Uses a custom repository query (findCustomerByName)
     * - Converts results to DTO list
     */
    public List<CustomerDTO> getCustomersByName(String name) {
        return customerRepository.findCustomerByName(name)
                .stream()
                .map(customerMappers::toCustomerDTO)
                .toList();
    }
}
