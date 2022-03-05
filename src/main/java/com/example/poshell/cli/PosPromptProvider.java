package com.example.poshell.cli;

import com.example.poshell.model.Customer;
import org.jline.utils.AttributedString;
import org.springframework.context.event.EventListener;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class PosPromptProvider implements PromptProvider {

    private Customer customer = null;

    @Override
    public AttributedString getPrompt() {
        if (customer == null) {
            return new AttributedString("unknown:>");
        }
        return new AttributedString(customer.getName() + ":>");
    }

    @EventListener
    public void handle(CustomerUpdatedEvent event) {
        customer = event.getCustomer();
    }
}
