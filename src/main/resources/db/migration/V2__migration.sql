-- Drop existing check constraint
ALTER TABLE ingredients
DROP CONSTRAINT ingredients_default_unit_check;

-- Add updated check constraint
ALTER TABLE ingredients
ADD CONSTRAINT ingredients_default_unit_check
CHECK (default_unit IN ('g', 'ml', 'piece', 'kg','tsp'));
