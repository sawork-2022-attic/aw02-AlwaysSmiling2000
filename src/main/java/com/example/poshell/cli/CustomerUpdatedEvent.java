package com.example.poshell.cli;

import com.example.poshell.model.Customer;
import org.springframework.context.ApplicationEvent;

public class CustomerUpdatedEvent extends ApplicationEvent {

    private Customer customer;

    public CustomerUpdatedEvent(Customer customer) {
        super(customer == null ? "ok" : customer);
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
