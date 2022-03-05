# POS in Shell

The demo shows a simple POS system with command line interface and [SQLite](https://www.sqlite.org/index.html) backend. Currently it implements nine commands which you can see using the `help` command.

```shell
unknown:>help
AVAILABLE COMMANDS

Built-In Commands
        clear: Clear the shell screen.
        exit, quit: Exit the shell.
        help: Display help about available commands.
        history: Display or save the history of previously run commands
        script: Read and execute commands from a file.
        stacktrace: Display the full stacktrace of the last error.

Pos Command
        add: Add a product to cart with a specified amount.
        empty-cart: Empty the cart.
        list-cart: List items in the cart.
        list-products: List all products available.
        log-in: Log in with an existing account.
        log-out: Log out the current account.
        remove: Remove the specified product from cart.
        set: Set the amount of the specified product in cart.
        sign-in: Create an account with a proper name and password.


unknown:>
```

You can always type the `list-products` command to see products available, but an account is needed in order to use the shopping cart. An account with name `guest` and password `guest` is included by default. Each modification to the cart is recorded in the database so that the data would not be lost.

# System Architecture

The system is built with a typical three-layered architecture, composed of a presentation layer, a business logic layer and a data access layer. We will discuss them in detail below.

## Presentation Layer

Presentation layer is used to interact with users. In this application, it consists of the [SpringShell](https://spring.io/projects/spring-shell) framework and classes below `cli` package. The SpringShell framework has provided us with an interactive terminal, and the `ShellCommand` component is used to handle user commands. Presentation layer connects users with specific business logic, so that we do not need to consider interaction issues when implementing the latter.

## Business Logic Layer

Business logic layer is where the actual work is done. It decouples the presentation layer and data access layer, making them independent of each other. Usually input validation is done here, the presentation layer only checks for a better experience. However, in this application I didn't do additional validation at this layer because the checks provided by `ShellCommand` are reliable enough. Other invalid states are controlled by database constraints. 

Classes below `biz` package make up the business logic layer. The `SimplePosService` implementation provides the `ShellCommand` component with a unified interface to access the processing results.

## Data Access Layer

Data access layer is used to interact with database. In this application, it's made up of classes below `db` package and `model` package. I have removed the original `Cart` class and replaced it with a table named `cart` in  SQLite database. And the implementation of `SqlitePosDB` interface is completed with [MyBatis](https://mybatis.org/mybatis-3/zh/index.html). Frankly speaking, it's inefficient to rely on database constraints for input validation, but I'll keep it for convenience. 

## 说明

我发现用英文不太说得清楚，总之本系统是一个典型的三层架构。其中`ShellCommand`属于展示层，`PosService`属于服务层，`PosDB`和`model`包下的 java bean 属于数据访问层。另外 ——
1. SQLite 是一个轻量级的数据库，它的数据都保存在一个文件中（resources/sqlite3/posDB.db），这使得它非常适合拿来做演示。pom 文件里包含了相关的驱动，你可能还需要安装命令行的 sqlite3（也可能不需要）。由于它非常的轻量，因此多进程访问同一个数据库文件会出现诡异的问题，最好不要尝试；
2. 数据访问层的 SQL 查询是通过 MyBatis 实现的，这是一个比较流行的半 ORM 框架，相关的 SQL 语句都写在一个`xml`文件里；
3. 服务层的实现依赖展示层做输入校验，依赖数据库约束保证数据完整性。这既不可靠也不高效，但是它使得我可以不用在服务层编写大量的检查逻辑，这很方便；
4. 数据访问层的实现很简单，没有做额外的缓存； 另外，目前还不支持购买商品的功能。
