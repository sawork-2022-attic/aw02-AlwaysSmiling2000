package com.example.poshell.cli;

import com.example.poshell.model.Customer;
import org.springframework.context.ApplicationEvent;

// PosPromptProvider 根据此事件来切换命令行提示符
public class CustomerUpdatedEvent extends ApplicationEvent {

    private Customer customer;

    public CustomerUpdatedEvent(Customer customer) {
        // 父类接收的对象不能为 null
        super(customer == null ? "ok" : customer);
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
