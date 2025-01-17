create table categories (
    id int8 not null,
    name varchar(255) not null,
    primary key (id)
);

create table localized_product (
    id int8 not null,
    description varchar(255),
    locale varchar(255) not null,
    name varchar(255) not null,
    primary key (id)
);

create table opening_hours (
    id int8 not null,
    close_at time not null,
    day int4 not null check (day>=1 AND day<=7),
    open_at time not null,
    primary key (id)
);

create table products (
    id int8 not null,
    price float4 not null,
    shop_id int8,
    primary key (id)
);

create table products_categories (
    product_id int8 not null,
    category_id int8 not null
);

create table products_localized_product (
    product_id int8 not null,
    localized_product_id int8 not null
);

create table shops (
    id int8 not null,
    created_at date not null,
    in_vacations boolean not null,
    name varchar(255) not null,
    primary key (id)
);

create table shops_opening_hours (
    shop_id int8 not null,
    opening_hours_id int8 not null
);

create table translation (
    id int8 not null,
    field_type varchar(255) not null,
    language varchar(255) not null,
    value varchar(255) not null,
    primary key (id)
);

alter table products_localized_product
add constraint UK_n8q0vltkv2dgjclj2aqn26l03 unique(localized_product_id);

alter table shops_opening_hours
add constraint UK_cnkerx0e3gn4yuhpjkr1d7heu unique (opening_hours_id);

alter table products
add constraint FK7kp8sbhxboponhx3lxqtmkcoj foreign key (shop_id) references shops;

alter table products_categories
add constraint FKqt6m2o5dly3luqcm00f5t4h2p foreign key (category_id) references categories;

alter table products_categories
add constraint FKtj1vdea8qwerbjqie4xldl1el foreign key (product_id) references products;

alter table products_localized_product
add constraint FKjs8yfvw4we59oaei8c9txb4wy foreign key (localized_product_id) references localized_product;

alter table products_localized_product add constraint
FK6i2yelx9i3lagm1u7n6v0xnfh foreign key (product_id) references products;

alter table shops_opening_hours
add constraint FKti43xlm3mfbeodhgi4qn1yhgw foreign key (opening_hours_id) references opening_hours;

alter table shops_opening_hours
add constraint FK8dcjdnasobclsvyy8wjfki7gj foreign key (shop_id) references shops;

CREATE INDEX idx_products_shop_id ON products (shop_id);
CREATE INDEX idx_products_categories_product_id ON products_categories (product_id);
CREATE INDEX idx_products_categories_category_id ON products_categories (category_id);
CREATE INDEX idx_shops_opening_hours_shop_id ON shops_opening_hours (shop_id);
CREATE INDEX idx_shops_opening_hours_opening_hours_id ON shops_opening_hours (opening_hours_id);
CREATE INDEX idx_products_localized_product_product_id ON products_localized_product (product_id);
CREATE INDEX idx_products_localized_product_localized_product_id ON products_localized_product (localized_product_id);

CREATE INDEX idx_products_price ON products (price);
CREATE INDEX idx_categories_name ON categories (name);
CREATE INDEX idx_opening_hours_day ON opening_hours (day);
CREATE INDEX idx_localized_product_locale ON localized_product (locale);
CREATE INDEX idx_shops_name ON shops (name);
CREATE INDEX idx_shops_created_at ON shops (created_at);

CREATE INDEX idx_products_categories ON products_categories (product_id, category_id);
CREATE INDEX idx_products_localized_product ON products_localized_product (product_id, localized_product_id);
CREATE INDEX idx_shops_opening_hours ON shops_opening_hours (shop_id, opening_hours_id);

-- Création d'une séquence pour chaque table
CREATE SEQUENCE categories_id_seq;
ALTER TABLE categories ALTER COLUMN id SET DEFAULT NEXTVAL('categories_id_seq');

CREATE SEQUENCE localized_product_id_seq;
ALTER TABLE localized_product ALTER COLUMN id SET DEFAULT NEXTVAL('localized_product_id_seq');

CREATE SEQUENCE opening_hours_id_seq;
ALTER TABLE opening_hours ALTER COLUMN id SET DEFAULT NEXTVAL('opening_hours_id_seq');

CREATE SEQUENCE products_id_seq;
ALTER TABLE products ALTER COLUMN id SET DEFAULT NEXTVAL('products_id_seq');

CREATE SEQUENCE shops_id_seq;
ALTER TABLE shops ALTER COLUMN id SET DEFAULT NEXTVAL('shops_id_seq');

CREATE SEQUENCE translation_id_seq;
ALTER TABLE translation ALTER COLUMN id SET DEFAULT NEXTVAL('translation_id_seq');

SELECT setval('categories_id_seq', COALESCE((SELECT MAX(id) FROM categories), 1), false);
SELECT setval('localized_product_id_seq', COALESCE((SELECT MAX(id) FROM localized_product), 1), false);
SELECT setval('opening_hours_id_seq', COALESCE((SELECT MAX(id) FROM opening_hours), 1), false);
SELECT setval('products_id_seq', COALESCE((SELECT MAX(id) FROM products), 1), false);
SELECT setval('shops_id_seq', COALESCE((SELECT MAX(id) FROM shops), 1), false);
SELECT setval('translation_id_seq', COALESCE((SELECT MAX(id) FROM translation), 1), false);
