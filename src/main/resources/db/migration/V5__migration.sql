DROP INDEX IF EXISTS idx_food_items_type;

ALTER TABLE food_items
DROP COLUMN IF EXISTS food_type;

DROP TYPE IF EXISTS food_type_enum;

CREATE TABLE food_labels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    is_system BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_label UNIQUE (name, category)
);

CREATE TABLE food_item_labels (
    food_item_id UUID NOT NULL,
    label_id UUID NOT NULL,

    PRIMARY KEY (food_item_id, label_id),

    CONSTRAINT fk_fil_food
        FOREIGN KEY (food_item_id)
        REFERENCES food_items(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_fil_label
        FOREIGN KEY (label_id)
        REFERENCES food_labels(id)
        ON DELETE CASCADE
);

