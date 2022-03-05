package com.example.poshell.biz;

import com.example.poshell.model.Customer;
import com.example.poshell.model.Item;
import com.example.poshell.model.Product;

import java.util.List;

// 业务逻辑接口
public interface PosService {

    public PosResult<Customer> signIn(String name, String passwd);

    public PosResult<Customer> logIn(String name, String passwd);

    public PosResult<Object> logOut();

    public PosResult<List<Product>> getAllProducts();

    public PosResult<Object> addToCustomerCart(int customerId, int productId, int amount);

    public PosResult<Object> setInCustomerCart(int customerId, int productId, int amount);

    public PosResult<Object> removeFromCustomerCart(int customerId, int productId);

    public PosResult<Integer> emptyCustomerCart(int customerId);

    public PosResult<List<Item>> getItemsInCustomerCart(int customerId);

//    public void buyProductById(String productId, int amount);
//
//    public void buyAllProductsInCart();

}
