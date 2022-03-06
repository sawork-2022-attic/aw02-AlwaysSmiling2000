package com.example.poshell.cli;

import com.example.poshell.biz.PosResult;
import com.example.poshell.biz.PosService;
import com.example.poshell.biz.PosStatus;
import com.example.poshell.model.Customer;
import com.example.poshell.model.Item;
import com.example.poshell.model.Product;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import javax.validation.constraints.Pattern;
import java.util.List;

// 按照分类，PosCommand 应当属于视图层；另外为了方便，这里我们使用 spring shell
// 自带的参数校验机制以及命令可用性校验；缺点是自带的错误提示和我们自定义的不一致
@ShellComponent
public class PosCommand {

    private static final String NAME_PATTERN = "[a-zA-Z0-9_]{1,32}";

    private static final String PASSWD_PATTERN = "[a-zA-Z0-9_]{3,18}";

    // null 表示未登录状态
    private Customer customer = null;

    private PosService posService;

    private ApplicationContext context;

    public PosCommand(PosService posService, ApplicationContext context) {
        this.posService = posService;
        this.context = context;
    }

    @ShellMethod(key = "sign-in", value = "Create an account with a proper name and password.")
    public String signIn(@Pattern(regexp = NAME_PATTERN) String name, @Pattern(regexp = PASSWD_PATTERN) String passwd) {

        // 已经登录了
        if (customer != null) {
            return error("You have already logged in.");
        }

        PosResult<Customer> result = posService.signIn(name, passwd);
        if (result.getStatus() == PosStatus.OK) {
            customer = result.getBody();
            context.publishEvent(new CustomerUpdatedEvent(customer));
            return ok("Sign in successfully.");
        }

        return error(result.getMessage());
    }

    @ShellMethod(key = "log-in", value = "Log in with an existing account.")
    public String logIn(@Pattern(regexp = NAME_PATTERN) String name, @Pattern(regexp = PASSWD_PATTERN) String passwd) {
        // 已经登录了
        if (customer != null) {
            return error("You have already logged in.");
        }

        PosResult<Customer> result = posService.logIn(name, passwd);
        if (result.getStatus() == PosStatus.OK) {
            customer = result.getBody();
            context.publishEvent(new CustomerUpdatedEvent(customer));
            return ok("Log in successfully.");
        }

        return error(result.getMessage());
    }

    @ShellMethod(key = "log-out", value = "Log out the current account.")
    public String logOut() {
        // 尚未登录
        if (customer == null) {
            return error("You are not logged in.");
        }

        PosResult<Object> result = posService.logOut();
        if (result.getStatus() == PosStatus.OK) {
            customer = null;
            context.publishEvent(new CustomerUpdatedEvent(null));
            return ok("Log out successfully.");
        }

        return error(result.getMessage());
    }

    @ShellMethod(key = "list-products", value = "List all products available.")
    public String listAllProducts() {
        PosResult<List<Product>> result = posService.getAllProducts();
        if (result.getStatus() == PosStatus.NOT_OK) {
            return error(result.getMessage());
        }

        // 展示所有商品
        List<Product> productList = result.getBody();
        StringBuilder stringBuilder = new StringBuilder(ok(productList.size() + " products fetched.\n"));
        stringBuilder.append("Id\t\tName\t\t\tPrice\n");
        stringBuilder.append("-------------------------------------------------\n");
        for (Product product : productList) {
            stringBuilder.append(product.getId());
            stringBuilder.append("\t\t");
            stringBuilder.append(product.getName());
            stringBuilder.append("\t\t");
            stringBuilder.append(product.getPrice());
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }

    @ShellMethod(key = "list-cart", value = "List items in the cart.")
    public String listAllItemsInCart() {
        // 尚未登录
        if (customer == null) {
            return error("You are not logged in.");
        }

        PosResult<List<Item>> result = posService.getItemsInCustomerCart(customer.getId());
        if (result.getStatus() == PosStatus.NOT_OK) {
            return error(result.getMessage());
        }

        // 展示购物车信息
        List<Item> itemList = result.getBody();
        StringBuilder stringBuilder = new StringBuilder(ok(itemList.size() + " items in cart.\n"));
        stringBuilder.append("Id\t\tName\t\t\tPrice\t\tAmount\n");
        stringBuilder.append("----------------------------------------------------------------\n");
        double totalPrice = 0;
        for (Item item : itemList) {
            Product product = item.getProduct();
            stringBuilder.append(product.getId());
            stringBuilder.append("\t\t");
            stringBuilder.append(product.getName());
            stringBuilder.append("\t\t");
            stringBuilder.append(product.getPrice());
            stringBuilder.append("\t\t");
            stringBuilder.append(item.getAmount());
            stringBuilder.append('\n');
            totalPrice += product.getPrice() * item.getAmount();
        }
        stringBuilder.append("----------------------------------------------------------------\n");
        stringBuilder.append("Total: ");
        stringBuilder.append(totalPrice);
        stringBuilder.append('\n');

        return stringBuilder.toString();
    }

    @ShellMethod(key = "add", value = "Add a product to cart with a specified amount.")
    public String addItemToCart(int productId, int amount) {
        // 尚未登录
        if (customer == null) {
            return error("You are not logged in.");
        }

        // 添加的数量必须是正数，这和数据库约束有关
        if (amount <= 0) {
            return error("Add amount must be positive.");
        }

        PosResult<Object> result = posService.addToCustomerCart(customer.getId(), productId, amount);
        if (result.getStatus() == PosStatus.OK) {
            return ok("Add product " + productId + " with amount " + amount + ".");
        }

        return error(result.getMessage());
    }

    @ShellMethod(key = "set", value = "Set the amount of the specified product in cart.")
    public String setItemInCart(int productId, int amount) {
        // 尚未登录
        if (customer == null) {
            return error("You are not logged in.");
        }

        // 设置的数量必须是正数，这和数据库约束有关
        if (amount <= 0) {
            return error("Set amount must be positive.");
        }

        PosResult<Object> result = posService.setInCustomerCart(customer.getId(), productId, amount);
        if (result.getStatus() == PosStatus.OK) {
            return ok("Set product " + productId + " to amount " + amount + ".");
        }

        return error(result.getMessage());
    }

    @ShellMethod(key = "remove", value = "Remove the specified product from cart.")
    public String removeItemFromCart(int productId) {
        // 尚未登录
        if (customer == null) {
            return error("You are not logged in.");
        }

        PosResult<Object> result = posService.removeFromCustomerCart(customer.getId(), productId);
        if (result.getStatus() == PosStatus.OK) {
            return ok("Remove product " + productId + " from your cart.");
        }

        return error(result.getMessage());
    }

    @ShellMethod(key = "empty-cart", value = "Empty the cart.")
    public String emptyCart() {
        // 尚未登录
        if (customer == null) {
            return error("You are not logged in.");
        }

        PosResult<Integer> result = posService.emptyCustomerCart(customer.getId());
        if (result.getStatus() == PosStatus.OK) {
            return ok(result.getBody() + " item(s) " + "removed.");
        }

        return error(result.getMessage());
    }

    private String error(String message) {
        return "\nERROR: " + message + "\n";
    }

    private String ok(String message) {
        return "\nOK: " + message + "\n";
    }

}
