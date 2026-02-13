BEGIN;

-- =====================================================
-- EXTENSIONS
-- =====================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =====================================================
-- ENUM TYPES
-- =====================================================

CREATE TYPE user_status_enum AS ENUM ('ACTIVE', 'DISABLED');
CREATE TYPE food_type_enum AS ENUM ('INGREDIENT', 'COMPOSITE');
CREATE TYPE meal_type_enum AS ENUM ('BREAKFAST', 'LUNCH', 'DINNER', 'SNACK');
CREATE TYPE created_by_enum AS ENUM ('SYSTEM', 'USER');
CREATE TYPE micronutrient_category_enum AS ENUM ('VITAMIN', 'MINERAL', 'OTHER');
CREATE TYPE gender_enum AS ENUM ('MALE','FEMALE','OTHER');


-- =====================================================
-- 1.USERS
-- =====================================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status user_status_enum NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);

-- =====================================================
-- 2.USER PROFILES (1:1)
-- =====================================================

CREATE TABLE user_profiles (
    user_id UUID PRIMARY KEY,
    full_name VARCHAR(255),
    age INT CHECK (age > 0),
    gender gender_enum NOT NULL,
    height_cm DECIMAL(5,2),
    weight_kg DECIMAL(5,2),
    activity_level VARCHAR(50),
    goal_type VARCHAR(50),

    CONSTRAINT fk_user_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_user_profiles_goal_type ON user_profiles(goal_type);

-- =====================================================
-- 3.NUTRITION PROFILES (container + version ready)
-- =====================================================

CREATE TABLE nutrition_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reference_quantity DECIMAL(8,2) NOT NULL,
    reference_unit VARCHAR(20) NOT NULL
        CHECK (reference_unit IN ('g', 'ml', 'piece','serving')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- =====================================================
-- 4.MACRONUTRIENTS (1:1)
-- =====================================================

CREATE TABLE nutrition_macros (
    nutrition_profile_id UUID PRIMARY KEY,
    calories_kcal DECIMAL(8,2) NOT NULL CHECK (calories_kcal >= 0),
    protein_g DECIMAL(8,2) NOT NULL CHECK (protein_g >= 0),
    carbs_g DECIMAL(8,2) NOT NULL CHECK (carbs_g >= 0),
    fat_g DECIMAL(8,2) NOT NULL CHECK (fat_g >= 0),
    fiber_g DECIMAL(8,2) NOT NULL CHECK (fiber_g >= 0),

    CONSTRAINT fk_macros_profile
        FOREIGN KEY (nutrition_profile_id)
        REFERENCES nutrition_profiles(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_macros_calories ON nutrition_macros(calories_kcal);

-- =====================================================
-- 5.MICRONUTRIENT TYPES (dimension)
-- =====================================================

CREATE TABLE micronutrient_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,       -- Vitamin C, Iron
    unit VARCHAR(20) NOT NULL,               -- mg / mcg / IU
    category micronutrient_category_enum NOT NULL
);

-- =====================================================
-- 6.NUTRITION MICRONUTRIENTS (bridge)
-- =====================================================

CREATE TABLE nutrition_micronutrients (
    nutrition_profile_id UUID NOT NULL,
    micronutrient_type_id UUID NOT NULL,
    amount DECIMAL(12,4) NOT NULL,

    PRIMARY KEY (nutrition_profile_id, micronutrient_type_id),

    CONSTRAINT fk_nm_profile
        FOREIGN KEY (nutrition_profile_id)
        REFERENCES nutrition_profiles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_nm_type
        FOREIGN KEY (micronutrient_type_id)
        REFERENCES micronutrient_types(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_nm_type ON nutrition_micronutrients(micronutrient_type_id);
CREATE INDEX idx_nm_profile ON nutrition_micronutrients(nutrition_profile_id);


-- =====================================================
-- 7.INGREDIENTS
-- =====================================================

CREATE TABLE ingredients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    default_unit VARCHAR(20) CHECK (default_unit IN ('g', 'ml', 'piece')),
    nutrition_profile_id UUID NOT NULL,
    deleted_at TIMESTAMP,

    CONSTRAINT fk_ingredients_nutrition
        FOREIGN KEY (nutrition_profile_id)
        REFERENCES nutrition_profiles(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_ingredients_name ON ingredients(name);
CREATE INDEX idx_ingredients_category ON ingredients(category);
CREATE INDEX idx_ingredients_nutrition ON ingredients(nutrition_profile_id);
CREATE INDEX idx_ingredients_active ON ingredients(name) WHERE deleted_at IS NULL;


CREATE INDEX idx_ingredients_search
ON ingredients
USING GIN (to_tsvector('english', name));

-- =====================================================
-- 8.FOOD ITEMS
-- =====================================================

CREATE TABLE food_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    food_type food_type_enum NOT NULL,
    serving_size DECIMAL(8,2),
    nutrition_profile_id UUID NOT NULL,
    deleted_at TIMESTAMP,

    CONSTRAINT fk_food_items_nutrition
        FOREIGN KEY (nutrition_profile_id)
        REFERENCES nutrition_profiles(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_food_items_name ON food_items(name);
CREATE INDEX idx_food_items_type ON food_items(food_type);
CREATE INDEX idx_food_items_nutrition ON food_items(nutrition_profile_id);

-- =====================================================
-- 9.RECIPES
-- =====================================================

CREATE TABLE recipes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    nutrition_profile_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    CONSTRAINT fk_recipes_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_recipes_nutrition
        FOREIGN KEY (nutrition_profile_id)
        REFERENCES nutrition_profiles(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_recipes_user ON recipes(user_id);
CREATE INDEX idx_recipes_created_at ON recipes(created_at);

-- =====================================================
-- 10.RECIPE INGREDIENTS
-- =====================================================

CREATE TABLE recipe_ingredients (
    recipe_id UUID NOT NULL,
    ingredient_id UUID NOT NULL,
    quantity DECIMAL(10,2) NOT NULL CHECK (quantity > 0),
    unit VARCHAR(20),

    PRIMARY KEY (recipe_id, ingredient_id),

    CONSTRAINT fk_recipe_ingredients_recipe
        FOREIGN KEY (recipe_id)
        REFERENCES recipes(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_recipe_ingredients_ingredient
        FOREIGN KEY (ingredient_id)
        REFERENCES ingredients(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_recipe_ingredients_ingredient
    ON recipe_ingredients(ingredient_id);

-- =====================================================
-- 11.DIET PLANS
-- =====================================================

CREATE TABLE diet_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    date DATE NOT NULL,
    calorie_target DECIMAL(8,2),
    created_by created_by_enum NOT NULL,

    CONSTRAINT fk_diet_plans_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT unique_user_date UNIQUE (user_id, date)
);

CREATE INDEX idx_diet_plans_user_date
    ON diet_plans(user_id, date);

-- =====================================================
-- 12.DIET PLAN MEALS
-- =====================================================

CREATE TABLE diet_plan_meals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    diet_plan_id UUID NOT NULL,
    meal_type meal_type_enum NOT NULL,
    food_item_id UUID,
    recipe_id UUID,
    suggested_quantity DECIMAL(8,2) NOT NULL CHECK (suggested_quantity > 0),

    CONSTRAINT fk_diet_plan_meals_plan
        FOREIGN KEY (diet_plan_id)
        REFERENCES diet_plans(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_diet_plan_meals_food
        FOREIGN KEY (food_item_id)
        REFERENCES food_items(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_diet_plan_meals_recipe
        FOREIGN KEY (recipe_id)
        REFERENCES recipes(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_one_source CHECK (
        (food_item_id IS NOT NULL AND recipe_id IS NULL)
        OR
        (food_item_id IS NULL AND recipe_id IS NOT NULL)
    )
);

CREATE INDEX idx_diet_plan_meals_plan
    ON diet_plan_meals(diet_plan_id);

-- =====================================================
-- 13.DAILY FOOD LOGS (PARTITIONED)
-- =====================================================

CREATE TABLE daily_food_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id UUID NOT NULL,
    date DATE NOT NULL,
    food_item_id UUID,
    recipe_id UUID,
    quantity DECIMAL(10,2) NOT NULL CHECK(quantity > 0),
    nutrition_profile_id UUID NOT NULL,
   -- Snapshot columns (fast aggregation)
    calories NUMERIC(10,2) NOT NULL,
    protein_g NUMERIC(10,2) NOT NULL,
    carbs_g NUMERIC(10,2) NOT NULL,
    fat_g NUMERIC(10,2) NOT NULL,
    fiber_g NUMERIC(10,2) NOT NULL,

    -- Flexible micronutrient snapshot
    nutrition_snapshot JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (date, id),


    CONSTRAINT fk_daily_logs_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_daily_logs_food
        FOREIGN KEY (food_item_id)
        REFERENCES food_items(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_daily_logs_recipe
        FOREIGN KEY (recipe_id)
        REFERENCES recipes(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_daily_logs_nutrition
        FOREIGN KEY (nutrition_profile_id)
        REFERENCES nutrition_profiles(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_log_one_source CHECK (
        (food_item_id IS NOT NULL AND recipe_id IS NULL)
        OR
        (food_item_id IS NULL AND recipe_id IS NOT NULL)
    )
) PARTITION BY RANGE (date);

CREATE INDEX idx_daily_food_logs_user_date ON daily_food_logs (user_id, date DESC);
CREATE INDEX idx_daily_food_logs_date ON daily_food_logs (date);
CREATE INDEX idx_daily_logs_snapshot ON daily_food_logs USING GIN (nutrition_snapshot);

-- =====================================================
-- 14.PARTITIONING FUNCTIONS
-- =====================================================


CREATE OR REPLACE FUNCTION create_monthly_partition(target_date DATE)
RETURNS VOID AS $$
DECLARE
     start_date DATE;
     end_date DATE;
     partition_name TEXT;
BEGIN
   start_date := date_trunc('month', target_date)::DATE;
   end_date := (start_date + INTERVAL '1 month')::DATE;
   partition_name := format( 'daily_food_logs_%s', to_char(start_date, 'YYYY_MM') );
   EXECUTE format( 'CREATE TABLE IF NOT EXISTS %I PARTITION OF daily_food_logs FOR VALUES FROM (%L) TO (%L);', partition_name, start_date, end_date );
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION create_future_partitions(months_ahead INT DEFAULT 12)
RETURNS VOID AS $$
DECLARE
    i INT;
    target_date DATE;
BEGIN
    FOR i IN 0..(months_ahead - 1) LOOP
        target_date := (
            date_trunc('month', CURRENT_DATE)
            + (i * INTERVAL '1 month')
        )::DATE;

        PERFORM create_monthly_partition(target_date);
    END LOOP;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE daily_food_logs_default
PARTITION OF daily_food_logs DEFAULT;

SELECT create_future_partitions(12);


COMMIT;
