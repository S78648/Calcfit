-- V1 created gender_enum with an unused 'OTHER' label (the Java enum never had it, so no rows use it).
-- The onboarding UI offers "Non-binary"; expose it under a name that matches Gender.NON_BINARY.
ALTER TYPE gender_enum RENAME VALUE 'OTHER' TO 'NON_BINARY';
