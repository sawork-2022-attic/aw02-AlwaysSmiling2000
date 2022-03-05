package com.example.poshell.biz;

import com.example.poshell.db.SqlitePosDB;
import com.example.poshell.model.Customer;
import com.example.poshell.model.Item;
import com.example.poshell.model.Product;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.sql.SQLException;
import java.util.List;

// 考虑到 spring shell 自带命令行参数校验，服务层就不再做额外校验了
// 因为输入参数只可能从命令行来
@Component
public class SimplePosService implements PosService {

    private SqlitePosDB posDB;

    // 构造函数自动注入
    public SimplePosService(SqlitePosDB posDB) {
        this.posDB = posDB;
        posDB.openForeignKeyConstraint();
    }

    // 成功返回注册好的 customer 对象，失败返回 null
    @Override
    @Transactional
    public PosResult<Customer> signIn(String name, String passwd) {
        String token = generateToken(passwd);
        Customer customer = new Customer(name, token);
        // 尝试创建用户
        try {
            int result = posDB.addCustomer(customer);
            // 创建失败
            if (result == 0) {
                throw new SQLException("Failed to create user.");
            }
        } catch (Exception e) {
            return PosResult.error().message("Name '" + name + "' already exists.");
        }

        return PosResult.ok().body(customer);
    }

    @Override
    public PosResult<Customer> logIn(String name, String passwd) {
        String token = generateToken(passwd);
        Customer customer = posDB.getCustomer(name, token);
        // 用户不存在，登录失败
        if (customer == null) {
            return PosResult.error().message("User doesn't exist or wrong passwd.");
        }

        return PosResult.ok().body(customer);
    }

    // 获取密码的 MD5 摘要
    private String generateToken(String passwd) {
        return DigestUtils.md5DigestAsHex(passwd.getBytes()).toUpperCase();
    }

    @Override
    public PosResult<Object> logOut() {
        return PosResult.ok().build();
    }

    @Override
    public PosResult<List<Product>> getAllProducts() {
        List<Product> productList = posDB.getAllProducts();
        return PosResult.ok().body(productList);
    }

    @Override
    @Transactional
    public PosResult<Object> addToCustomerCart(int customerId, int productId, int amount) {
        try {
            int result = posDB.addToCustomerCart(customerId, productId, amount);
            // 添加失败
            if (result == 0) {
                throw new SQLException("Failed to add item.");
            }
        } catch (Exception e) {
            // 外键约束不满足，比如 customerId 不合法，productId 不合法，amount 不合法
            return PosResult.error().message("Can not add product " + productId + " with amount " + amount + ".");
        }

        return PosResult.ok().build();
    }

    // 将购物车中的指定商品减少 amount 件
    @Override
    public PosResult<Object> setInCustomerCart(int customerId, int productId, int amount) {
        try {
            int result = posDB.setInCustomerCart(customerId, productId, amount);
            // 购物车中没有该商品
            if (result == 0) {
                return PosResult.error().message("No such item with id " + productId + ".");
            }
        } catch (Exception e) {
            return PosResult.error().message("Can not set product " + productId + " with amount " + amount + ".");
        }

        return PosResult.ok().build();
    }

    @Override
    public PosResult<Object> removeFromCustomerCart(int customerId, int productId) {
        try {
            int result = posDB.removeFromCustomerCart(customerId, productId);
            // 购物车中没有该商品
            if (result == 0) {
                return PosResult.error().message("No such item with id " + productId + ".");
            }
        } catch (Exception e) {
            return PosResult.error().message("Failed to remove item.");
        }

        return PosResult.ok().build();
    }

    // 清空用户的购物车记录，返回清空的记录条数
    @Override
    public PosResult<Integer> emptyCustomerCart(int customerId) {
        try {
            int result = posDB.emptyCustomerCart(customerId);
            return PosResult.ok().body(result);
        } catch (Exception e) {
            return PosResult.error().message("Failed to empty cart.");
        }
    }

    @Override
    public PosResult<List<Item>> getItemsInCustomerCart(int customerId) {
        List<Item> itemList = posDB.getItemsInCustomerCart(customerId);
        return PosResult.ok().body(itemList);
    }

//    @Override
//    public void buyProductById(String productId, int amout) {
//
//    }
//
//    @Override
//    public void buyAllProductsInCart() {
//
//    }
}
