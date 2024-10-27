-- Insert initial zip codes with 'available' status
INSERT INTO zip_codes (code, available) VALUES ('10001', TRUE);
INSERT INTO zip_codes (code, available) VALUES ('20002', TRUE);
INSERT INTO zip_codes (code, available) VALUES ('30003', TRUE);

-- Insert a user and set the zip code to unavailable
INSERT INTO users (name, email, sex, zip_code_id, age)
VALUES ('John Doe', 'john.doe@example.com', 'Male', (SELECT id FROM zip_codes WHERE code='10001'), 35);

-- Set zip code '10001' to unavailable as it's assigned to a user
UPDATE zip_codes SET available = FALSE WHERE code = '10001';
