-- execute this script in sqlite3 to initialize the database

DROP TABLE IF EXISTS customer;

DROP TABLE IF EXISTS product;

DROP TABLE IF EXISTS cart;

-- customer_token is the MD5 representation of user's password
CREATE TABLE customer (
    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_name VARCHAR(32) UNIQUE NOT NULL,
    customer_token CHAR(32) NOT NULL
);

CREATE TABLE product (
  product_id INTEGER PRIMARY KEY AUTOINCREMENT,
  product_name VARCHAR(32) NOT NULL,
  product_price REAL NOT NULL CHECK ( product_price >= 0 )
);

-- this table stores the cart data of all users
CREATE TABLE cart (
  customer_id INTEGER NOT NULL,
  product_id INTEGER NOT NULL,
  amount INTEGER NOT NULL CHECK ( amount > 0 ),
  PRIMARY KEY (customer_id, product_id),
  FOREIGN KEY(customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
  FOREIGN KEY(product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

-- username: guest, password: guest, this is the default account
INSERT INTO customer(customer_name, customer_token) VALUES ('guest', '084E0343A0486FF05530DF6C705C8BB4');

INSERT INTO product(product_name, product_price) VALUES ('ELDEN RING', 339.00);
INSERT INTO product(product_name, product_price) VALUES ('FORZA HORIZON 5', 419.00);
INSERT INTO product(product_name, product_price) VALUES ('Dread Hunger', 129.00);
INSERT INTO product(product_name, product_price) VALUES ('MacBook Pro 16', 19799.00);
INSERT INTO product(product_name, product_price) VALUES ('iPhone 13 Pro', 8499.00);
INSERT INTO product(product_name, product_price) VALUES ('AirPods Pro', 1999);
