-- ============================================================
-- CINEMA MANAGEMENT SYSTEM — SAMPLE DATA (Oracle)
-- Run this AFTER running schema_oracle.sql
-- ============================================================

-- ------------------------------------------------------------
-- 1. MOVIE
-- ------------------------------------------------------------
INSERT INTO MOVIE (title, genre, director, duration_min, rating, release_year)
VALUES ('Avengers: Endgame', 'Action / Sci-Fi', 'Anthony Russo', 181, 'PG-13', 2019);

INSERT INTO MOVIE (title, genre, director, duration_min, rating, release_year)
VALUES ('Dune: Part Two', 'Sci-Fi / Adventure', 'Denis Villeneuve', 166, 'PG-13', 2024);

INSERT INTO MOVIE (title, genre, director, duration_min, rating, release_year)
VALUES ('Spider-Man: No Way Home', 'Action / Fantasy', 'Jon Watts', 148, 'PG-13', 2021);


-- ------------------------------------------------------------
-- 2. HALL
-- ------------------------------------------------------------
INSERT INTO HALL (hall_name, capacity, hall_type)
VALUES ('Grand Hall A', 120, 'STANDARD');

INSERT INTO HALL (hall_name, capacity, hall_type)
VALUES ('IMAX Theater', 80, 'IMAX');

INSERT INTO HALL (hall_name, capacity, hall_type)
VALUES ('VIP Lounge', 40, 'VIP');


-- ------------------------------------------------------------
-- 3. STAFF
-- ------------------------------------------------------------
INSERT INTO STAFF (full_name, role, hire_date)
VALUES ('Sokha Dara', 'Manager', DATE '2022-03-15');

INSERT INTO STAFF (full_name, role, hire_date)
VALUES ('Chanda Rith', 'Ticket Seller', DATE '2023-01-10');

INSERT INTO STAFF (full_name, role, hire_date)
VALUES ('Vannak Chea', 'Projectionist', DATE '2023-06-01');


-- ------------------------------------------------------------
-- 4. CUSTOMER
-- ------------------------------------------------------------
INSERT INTO CUSTOMER (full_name, email, phone, created_at)
VALUES ('John Smith', 'john.smith@email.com', '012345678', TO_TIMESTAMP('2026-01-10 14:30:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO CUSTOMER (full_name, email, phone, created_at)
VALUES ('Sarah Johnson', 'sarah.j@email.com', '098765432', TO_TIMESTAMP('2026-02-15 09:15:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO CUSTOMER (full_name, email, phone, created_at)
VALUES ('Michael Brown', 'mbrown@email.com', '011223344', TO_TIMESTAMP('2026-03-20 18:45:00', 'YYYY-MM-DD HH24:MI:SS'));


-- ------------------------------------------------------------
-- 5. MOVIE_SHOW (future dates so they appear as Upcoming)
-- ------------------------------------------------------------
INSERT INTO MOVIE_SHOW (movie_id, hall_id, show_datetime, base_price)
VALUES (1, 2, TO_TIMESTAMP('2026-06-20 19:00:00', 'YYYY-MM-DD HH24:MI:SS'), 12.50);

INSERT INTO MOVIE_SHOW (movie_id, hall_id, show_datetime, base_price)
VALUES (2, 1, TO_TIMESTAMP('2026-06-21 20:30:00', 'YYYY-MM-DD HH24:MI:SS'), 8.50);

INSERT INTO MOVIE_SHOW (movie_id, hall_id, show_datetime, base_price)
VALUES (3, 3, TO_TIMESTAMP('2026-06-22 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), 18.00);


-- ------------------------------------------------------------
-- 6. BOOKING
-- ------------------------------------------------------------
INSERT INTO BOOKING (customer_id, show_id, staff_id, booking_date, payment_method, total_amount, status)
VALUES (1, 1, 2, TO_TIMESTAMP('2026-05-18 10:20:00', 'YYYY-MM-DD HH24:MI:SS'), 'CARD', 37.50, 'CONFIRMED');

INSERT INTO BOOKING (customer_id, show_id, staff_id, booking_date, payment_method, total_amount, status)
VALUES (2, 2, 2, TO_TIMESTAMP('2026-05-18 14:45:00', 'YYYY-MM-DD HH24:MI:SS'), 'CASH', 17.00, 'CONFIRMED');

INSERT INTO BOOKING (customer_id, show_id, staff_id, booking_date, payment_method, total_amount, status)
VALUES (3, 3, 1, TO_TIMESTAMP('2026-05-18 16:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'QR', 54.00, 'CONFIRMED');


-- ------------------------------------------------------------
-- 7. BOOKING_SEAT
-- ------------------------------------------------------------
-- Booking 1 (Show 1 — IMAX, 3 seats)
INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (1, 1, 'A1', 12.50, 'STANDARD');

INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (1, 1, 'A2', 12.50, 'STANDARD');

INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (1, 1, 'A3', 12.50, 'STANDARD');

-- Booking 2 (Show 2 — Grand Hall A, 2 seats)
INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (2, 2, 'B5', 8.50, 'STANDARD');

INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (2, 2, 'B6', 8.50, 'STANDARD');

-- Booking 3 (Show 3 — VIP Lounge, 3 seats)
INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (3, 3, 'C1', 18.00, 'VIP');

INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (3, 3, 'C2', 18.00, 'VIP');

INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (3, 3, 'C3', 18.00, 'VIP');


COMMIT;
