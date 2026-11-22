-- Target weight and weekly pace, asked on the onboarding goal step (lose/gain only).
-- Both nullable: maintenance users and profiles created before this migration have none;
-- the calorie calculation then falls back to the goal's default +/-500 kcal/day.
ALTER TABLE user_profiles
    ADD COLUMN target_weight_kg DECIMAL(5, 2),
    ADD COLUMN weekly_pace_kg   DECIMAL(3, 2);

ALTER TABLE user_profiles
    ADD CONSTRAINT chk_user_profiles_weekly_pace
        CHECK (weekly_pace_kg IS NULL OR (weekly_pace_kg >= 0 AND weekly_pace_kg <= 1));
