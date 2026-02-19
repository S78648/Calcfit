BEGIN;

-- =====================================================
-- 1️⃣ CREATE MEAL TYPE TABLE
-- =====================================================

CREATE TABLE meal_type (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50) UNIQUE NOT NULL,
    display_name    VARCHAR(100) NOT NULL,
    description     TEXT,
    sort_order      INT NOT NULL UNIQUE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- 2️⃣ SEED DEFAULT MEAL TYPES
-- =====================================================

INSERT INTO meal_type (code, display_name, sort_order)
VALUES
('BREAKFAST', 'Breakfast', 1),
('MID_MORNING_SNACK', 'Mid-Morning Snack', 2),
('LUNCH', 'Lunch', 3),
('AFTERNOON_SNACK', 'Afternoon Snack', 4),
('DINNER', 'Dinner', 5),
('SNACK', 'Snack', 6),
('LATE_NIGHT_SNACK', 'Late Night Snack', 7);

-- =====================================================
-- 3️⃣ ADD NEW COLUMN TO diet_plan_meals
-- =====================================================

ALTER TABLE diet_plan_meals
ADD COLUMN meal_type_id BIGINT;

-- =====================================================
-- 4️⃣ MIGRATE DATA FROM ENUM TO LOOKUP TABLE
-- =====================================================

UPDATE diet_plan_meals dpm
SET meal_type_id = mt.id
FROM meal_type mt
WHERE mt.code = dpm.meal_type::text;

-- =====================================================
-- 5️⃣ ADD FOREIGN KEY CONSTRAINT
-- =====================================================

ALTER TABLE diet_plan_meals
ADD CONSTRAINT fk_diet_plan_meals_meal_type
FOREIGN KEY (meal_type_id)
REFERENCES meal_type(id)
ON DELETE RESTRICT;

-- =====================================================
-- 6️⃣ MAKE COLUMN NOT NULL (AFTER DATA MIGRATION)
-- =====================================================

ALTER TABLE diet_plan_meals
ALTER COLUMN meal_type_id SET NOT NULL;

-- =====================================================
-- 7️⃣ DROP OLD ENUM COLUMN
-- =====================================================

ALTER TABLE diet_plan_meals
DROP COLUMN meal_type;

-- =====================================================
-- 8️⃣ DROP ENUM TYPE (ONLY IF UNUSED)
-- =====================================================

DROP TYPE IF EXISTS meal_type_enum;

-- =====================================================
-- 9️⃣ INDEXING (CRITICAL FOR PLAN GENERATION)
-- =====================================================

CREATE INDEX idx_diet_plan_meals_meal_type_id
ON diet_plan_meals(meal_type_id);

-- Composite index (important for plan fetch)
CREATE INDEX idx_diet_plan_meals_plan_meal_type
ON diet_plan_meals(diet_plan_id, meal_type_id);

COMMIT;
