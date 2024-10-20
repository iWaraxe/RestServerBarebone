-- Insert initial zip codes
INSERT INTO zip_codes (code) VALUES ('10001');
INSERT INTO zip_codes (code) VALUES ('20002');
INSERT INTO zip_codes (code) VALUES ('30003');

-- Insert a user to test duplicate user scenario
-- Assuming '10001' zip code has id 1
INSERT INTO users (name, email, sex, zip_code_id) VALUES ('John Doe', 'john.doe@example.com', 'Male', 1);
