package com.acme.insurance.repository;

import com.acme.insurance.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE LOWER(c.lastName) = LOWER(?1)")
    List<Customer> findByLastNameIgnoreCase(String lastName);

    // Raw JPQL — older teams often used native queries here
    @Query(value = "SELECT * FROM customers WHERE phone_number IS NOT NULL", nativeQuery = true)
    List<Customer> findAllWithPhoneNumber();
}
