ALTER TABLE recipes
ADD COLUMN meal_type meal_type_enum,
ADD COLUMN prep_time_minutes INT CHECK (prep_time_minutes >= 0),
ADD COLUMN cook_time_minutes INT CHECK (cook_time_minutes >= 0),
ADD COLUMN servings INT CHECK (servings > 0),
ADD COLUMN difficulty VARCHAR(20)
    CHECK (difficulty IN ('EASY','MEDIUM','HARD')),
ADD COLUMN cover_image_url TEXT,
ADD COLUMN health_score INT CHECK (health_score BETWEEN 0 AND 100);


CREATE TABLE recipe_steps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipe_id UUID NOT NULL,
    step_number INT NOT NULL,
    description TEXT NOT NULL,

    CONSTRAINT fk_recipe_steps_recipe
        FOREIGN KEY (recipe_id)
        REFERENCES recipes(id)
        ON DELETE CASCADE,

    CONSTRAINT unique_recipe_step
        UNIQUE (recipe_id, step_number)
);


CREATE TABLE dietary_labels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    is_system_generated BOOLEAN DEFAULT TRUE
);

CREATE TABLE recipe_dietary_labels (
    recipe_id UUID NOT NULL,
    dietary_label_id UUID NOT NULL,

    PRIMARY KEY (recipe_id, dietary_label_id),

    FOREIGN KEY (recipe_id)
        REFERENCES recipes(id)
        ON DELETE CASCADE,

    FOREIGN KEY (dietary_label_id)
        REFERENCES dietary_labels(id)
        ON DELETE CASCADE
);
