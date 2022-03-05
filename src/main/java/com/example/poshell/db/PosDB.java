package com.example.poshell.db;

import com.example.poshell.model.Customer;
import com.example.poshell.model.Item;
import com.example.poshell.model.Product;

import java.util.List;

public interface PosDB {

    // 创建一个新用户，并取回自动生成的 key（customer 的 id 属性）
    // 返回 insert 操作影响的行数
    public int addCustomer(Customer customer) throws Exception;

    // 从数据库读取用户信息，检查指定的账号是否存在
    // 返回设置好 id 的 customer 对象，如果该用户存在；否则返回 null
    public Customer getCustomer(String name, String token);

    // 获取所有产品的列表
    public List<Product> getAllProducts();

    // 添加一件商品到用户的购物车
    // 返回 insert 操作影响的行数
    public int addToCustomerCart(int customerId, int productId, int amount) throws Exception;

    // 减少用户购物车中某件商品的数量
    // 返回 update 操作影响的行数
    public int setInCustomerCart(int customerId, int productId, int amount) throws Exception;

    // 移除购物车中的某件商品
    public int removeFromCustomerCart(int customerId, int productId) throws Exception;

    // 清空用户的购物车
    // 返回 delete 操作影响的行数
    public int emptyCustomerCart(int customerId) throws Exception;

    // 从数据库读取用户的购物车清单
    public List<Item> getItemsInCustomerCart(int customerId);

//    public void buyProductById(int customerId, int productId, int amount);
//
//    public void buyAllProductsInCustomerCart(int customerId);

}
