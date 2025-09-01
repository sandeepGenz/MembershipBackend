-- Benefits
INSERT INTO benefit (name, details, configurable) VALUES
('FREE_DELIVERY', '{"eligible":true}', false),
('EXTRA_DISCOUNT', '{"percent":5}', true),
('EXCLUSIVE_DEALS', '{"coupons":["WELCOME10"]}', false),
('PRIORITY_SUPPORT', '{}', false);

-- Plans (amountMinor is in paise; e.g. INR 199.00 -> 19900)
INSERT INTO membership_plan (code, duration_days, amount_minor, currency) VALUES
('MONTHLY', 30, 19900, 'INR'),
('QUARTERLY', 90, 49900, 'INR'),
('YEARLY', 365, 149900, 'INR');

-- Link plan benefits to all plans (FREE_DELIVERY + EXCLUSIVE_DEALS)
INSERT INTO plan_benefit (plan_id, benefit_id)
 SELECT p.id, b.id FROM membership_plan p, benefit b
 WHERE b.name IN ('FREE_DELIVERY','EXCLUSIVE_DEALS');

-- Tiers
INSERT INTO tier (code, priority, active) VALUES ('PLATINUM', 100, true), ('GOLD', 50, true), ('SILVER', 10, true);

-- Assign EXTRA_DISCOUNT to tiers (so tier can boost discount)
INSERT INTO tier_benefit (tier_id, benefit_id)
 SELECT t.id, b.id FROM tier t, benefit b WHERE b.name = 'EXTRA_DISCOUNT';