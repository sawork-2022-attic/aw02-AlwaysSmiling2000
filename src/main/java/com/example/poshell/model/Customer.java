package com.example.poshell.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private int id = 0;

    private String name = "";

    private String token = "";

    public Customer(String name, String token) {
        this.name = name;
        this.token = token;
    }
}
