package com.garv.foodApp.foodApp.Controller;
import com.garv.foodApp.foodApp.DTO.CustomerDTO;
import com.garv.foodApp.foodApp.Service.CustomerService;
import com.garv.foodApp.foodApp.Service.cacheInspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    final private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Autowired
    cacheInspectionService cacheInspectionService;

    // ---------------- CREATE ----------------
    @PostMapping
    public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) {
      return customerService.createCustomer(customerDTO);
    }


    // ---------------- READ ALL ----------------
    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
       return customerService.getAllCustomers();
    }

    // ---------------- READ ALL BY SORT ----------------
    @GetMapping("/sorted")
    public List<CustomerDTO> getAllSortCustomers(@RequestParam(required = false, defaultValue = "") String sort) {
        return customerService.getAllSortCustomers(sort);
    }

    // ---------------- READ BY ID ----------------
    @GetMapping("/{id}") public ResponseEntity<CustomerDTO>
    getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id)); }

    // ---------------- PARTIAL UPDATE (PATCH) ----------------
    @PatchMapping("/{id}")
    public CustomerDTO updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        return customerService.updateCustomer(id, customerDTO);
    }

    // ---------------- FULL UPDATE (PUT) ----------------
    @PutMapping("/{id}")
    public CustomerDTO updateFullCustomer(@RequestBody CustomerDTO customerDTO, @PathVariable Long id) {
        return customerService.updateFullCustomer(id, customerDTO);
    }

    @GetMapping("/cacheData")
    public void getCacheData() {
        cacheInspectionService.printCache("Customer");

    }

    @GetMapping("/address/{address}")
    public ResponseEntity<List<CustomerDTO>> getCustomersByAddress(@PathVariable String address) {
        return ResponseEntity.ok(customerService.getCustomersByAddress(address));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<CustomerDTO>> getCustomerByName(@PathVariable String name) {
        return ResponseEntity.ok(customerService.getCustomersByName(name));
    }
}
