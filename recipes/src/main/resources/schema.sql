DROP TABLE IF EXISTS recipes;

CREATE TABLE recipes (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cuisine VARCHAR(255),
  title VARCHAR(500) NOT NULL,
  rating DECIMAL(3,2) NULL,
  prep_time INT NULL,
  cook_time INT NULL,
  total_time INT NULL,
  description TEXT,
  url VARCHAR(1000),
  ingredients TEXT,
  instructions TEXT,
  nutrients JSON NULL,
  serves VARCHAR(255),

  
  calories_num INT GENERATED ALWAYS AS (
    CAST(
      REGEXP_REPLACE(
        JSON_UNQUOTE(JSON_EXTRACT(nutrients, '$.calories')),
        '[^0-9.]',
        ''
      ) AS UNSIGNED
    )
  ) STORED
);


CREATE INDEX idx_recipes_rating ON recipes (rating DESC);
CREATE INDEX idx_recipes_total_time ON recipes (total_time);
CREATE INDEX idx_recipes_cuisine ON recipes (cuisine);
CREATE INDEX idx_recipes_title ON recipes (title(191));
CREATE INDEX idx_recipes_calories_num ON recipes (calories_num);
