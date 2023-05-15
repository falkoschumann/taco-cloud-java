CREATE TABLE IF NOT EXISTS ingredients (
  PRIMARY KEY (id),
  id   VARCHAR(4)  NOT NULL,
  name VARCHAR(25) NOT NULL,
  type VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS tacos (
  id         IDENTITY,
  name       VARCHAR(50) NOT NULL,
  created_at TIMESTAMP   NOT NULL
);

CREATE TABLE IF NOT EXISTS taco_ingredients (
  taco       BIGINT     NOT NULL REFERENCES tacos(id),
  ingredient VARCHAR(4) NOT NULL REFERENCES ingredients(id)
);

CREATE TABLE IF NOT EXISTS taco_orders (
  id              IDENTITY,
  delivery_name   VARCHAR(50) NOT NULL,
  delivery_street VARCHAR(50) NOT NULL,
  delivery_city   VARCHAR(50) NOT NULL,
  delivery_state  VARCHAR(2)  NOT NULL,
  delivery_zip    VARCHAR(10) NOT NULL,
  cc_number       VARCHAR(16) NOT NULL,
  cc_expiration   VARCHAR(5)  NOT NULL,
  cc_cvv          VARCHAR(3)  NOT NULL,
  placed_at       TIMESTAMP   NOT NULL
);

CREATE TABLE IF NOT EXISTS taco_order_tacos (
  taco_order BIGINT NOT NULL REFERENCES taco_orders(id),
  taco       BIGINT NOT NULL REFERENCES tacos(id)
);
