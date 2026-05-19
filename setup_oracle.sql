-- ============================================================
-- CINEMA MANAGEMENT SYSTEM — FULL SETUP SCRIPT (Oracle)
-- Run this ONE script to create tables + insert sample data
-- Or run the individual scripts in order:
--   1. drop_tables.sql   (optional, if resetting)
--   2. schema_oracle.sql (CREATE TABLE)
--   3. sample_data_oracle.sql (INSERT)
--   4. queries.sql       (optional, for reporting)
-- ============================================================

-- Drop existing tables (clean start)
DROP TABLE BOOKING_SEAT CASCADE CONSTRAINTS;
DROP TABLE BOOKING CASCADE CONSTRAINTS;
DROP TABLE MOVIE_SHOW CASCADE CONSTRAINTS;
DROP TABLE CUSTOMER CASCADE CONSTRAINTS;
DROP TABLE STAFF CASCADE CONSTRAINTS;
DROP TABLE HALL CASCADE CONSTRAINTS;
DROP TABLE MOVIE CASCADE CONSTRAINTS;

-- ------------------------------------------------------------
-- TABLES
-- ------------------------------------------------------------

CREATE TABLE MOVIE (
    movie_id      NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title         VARCHAR2(255) NOT NULL,
    genre         VARCHAR2(100),
    director      VARCHAR2(255),
    duration_min  NUMBER CHECK (duration_min > 0),
    rating        VARCHAR2(20),
    release_year  NUMBER
);

CREATE TABLE HALL (
    hall_id       NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    hall_name     VARCHAR2(255) NOT NULL UNIQUE,
    capacity      NUMBER NOT NULL CHECK (capacity > 0),
    hall_type     VARCHAR2(50)
);

CREATE TABLE STAFF (
    staff_id      NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name     VARCHAR2(255) NOT NULL,
    role          VARCHAR2(100),
    hire_date     DATE
);

CREATE TABLE CUSTOMER (
    customer_id   NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name     VARCHAR2(255) NOT NULL,
    email         VARCHAR2(255) NOT NULL UNIQUE,
    phone         VARCHAR2(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE MOVIE_SHOW (
    show_id       NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    movie_id      NUMBER NOT NULL,
    hall_id       NUMBER NOT NULL,
    show_datetime TIMESTAMP NOT NULL,
    base_price    NUMBER(10,2) NOT NULL CHECK (base_price >= 0),
    CONSTRAINT uq_show_slot UNIQUE (hall_id, show_datetime),
    CONSTRAINT fk_show_movie FOREIGN KEY (movie_id) REFERENCES MOVIE(movie_id),
    CONSTRAINT fk_show_hall  FOREIGN KEY (hall_id)  REFERENCES HALL(hall_id)
);

CREATE TABLE BOOKING (
    booking_id     NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id    NUMBER NOT NULL,
    show_id        NUMBER NOT NULL,
    staff_id       NUMBER,
    booking_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR2(50) CHECK (payment_method IN ('CASH','CARD','QR','ONLINE')),
    total_amount   NUMBER(10,2) CHECK (total_amount >= 0),
    status         VARCHAR2(50) DEFAULT 'CONFIRMED' CHECK (status IN ('CONFIRMED','CANCELLED','PENDING')),
    CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id),
    CONSTRAINT fk_booking_show     FOREIGN KEY (show_id)     REFERENCES MOVIE_SHOW(show_id),
    CONSTRAINT fk_booking_staff    FOREIGN KEY (staff_id)    REFERENCES STAFF(staff_id)
);

CREATE TABLE BOOKING_SEAT (
    booking_seat_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id      NUMBER NOT NULL,
    show_id         NUMBER NOT NULL,
    seat_number     VARCHAR2(10) NOT NULL,
    seat_price      NUMBER(10,2) NOT NULL CHECK (seat_price >= 0),
    seat_type       VARCHAR2(50) DEFAULT 'STANDARD',
    CONSTRAINT uq_show_seat UNIQUE (show_id, seat_number),
    CONSTRAINT fk_bs_booking FOREIGN KEY (booking_id) REFERENCES BOOKING(booking_id) ON DELETE CASCADE,
    CONSTRAINT fk_bs_show    FOREIGN KEY (show_id)    REFERENCES MOVIE_SHOW(show_id)
);

-- ------------------------------------------------------------
-- SAMPLE DATA
-- ------------------------------------------------------------

-- Movies
INSERT INTO MOVIE (title, genre, director, duration_min, rating, release_year)
VALUES ('Avengers: Endgame', 'Action / Sci-Fi', 'Anthony Russo', 181, 'PG-13', 2019);

INSERT INTO MOVIE (title, genre, director, duration_min, rating, release_year)
VALUES ('Dune: Part Two', 'Sci-Fi / Adventure', 'Denis Villeneuve', 166, 'PG-13', 2024);

INSERT INTO MOVIE (title, genre, director, duration_min, rating, release_year)
VALUES ('Spider-Man: No Way Home', 'Action / Fantasy', 'Jon Watts', 148, 'PG-13', 2021);

-- Halls
INSERT INTO HALL (hall_name, capacity, hall_type)
VALUES ('Grand Hall A', 120, 'STANDARD');

INSERT INTO HALL (hall_name, capacity, hall_type)
VALUES ('IMAX Theater', 80, 'IMAX');

INSERT INTO HALL (hall_name, capacity, hall_type)
VALUES ('VIP Lounge', 40, 'VIP');

-- Staff
INSERT INTO STAFF (full_name, role, hire_date)
VALUES ('Sokha Dara', 'Manager', DATE '2022-03-15');

INSERT INTO STAFF (full_name, role, hire_date)
VALUES ('Chanda Rith', 'Ticket Seller', DATE '2023-01-10');

INSERT INTO STAFF (full_name, role, hire_date)
VALUES ('Vannak Chea', 'Projectionist', DATE '2023-06-01');

-- Customers
INSERT INTO CUSTOMER (full_name, email, phone, created_at)
VALUES ('John Smith', 'john.smith@email.com', '012345678', TO_TIMESTAMP('2026-01-10 14:30:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO CUSTOMER (full_name, email, phone, created_at)
VALUES ('Sarah Johnson', 'sarah.j@email.com', '098765432', TO_TIMESTAMP('2026-02-15 09:15:00', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO CUSTOMER (full_name, email, phone, created_at)
VALUES ('Michael Brown', 'mbrown@email.com', '011223344', TO_TIMESTAMP('2026-03-20 18:45:00', 'YYYY-MM-DD HH24:MI:SS'));

-- Shows (future dates so they appear as Upcoming)
INSERT INTO MOVIE_SHOW (movie_id, hall_id, show_datetime, base_price)
VALUES (1, 2, TO_TIMESTAMP('2026-06-20 19:00:00', 'YYYY-MM-DD HH24:MI:SS'), 12.50);

INSERT INTO MOVIE_SHOW (movie_id, hall_id, show_datetime, base_price)
VALUES (2, 1, TO_TIMESTAMP('2026-06-21 20:30:00', 'YYYY-MM-DD HH24:MI:SS'), 8.50);

INSERT INTO MOVIE_SHOW (movie_id, hall_id, show_datetime, base_price)
VALUES (3, 3, TO_TIMESTAMP('2026-06-22 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), 18.00);

-- Bookings
INSERT INTO BOOKING (customer_id, show_id, staff_id, booking_date, payment_method, total_amount, status)
VALUES (1, 1, 2, TO_TIMESTAMP('2026-05-18 10:20:00', 'YYYY-MM-DD HH24:MI:SS'), 'CARD', 37.50, 'CONFIRMED');

INSERT INTO BOOKING (customer_id, show_id, staff_id, booking_date, payment_method, total_amount, status)
VALUES (2, 2, 2, TO_TIMESTAMP('2026-05-18 14:45:00', 'YYYY-MM-DD HH24:MI:SS'), 'CASH', 17.00, 'CONFIRMED');

INSERT INTO BOOKING (customer_id, show_id, staff_id, booking_date, payment_method, total_amount, status)
VALUES (3, 3, 1, TO_TIMESTAMP('2026-05-18 16:30:00', 'YYYY-MM-DD HH24:MI:SS'), 'QR', 54.00, 'CONFIRMED');

-- Booking Seats
INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (1, 1, 'A1', 12.50, 'STANDARD');

INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (1, 1, 'A2', 12.50, 'STANDARD');

INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (1, 1, 'A3', 12.50, 'STANDARD');

INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (2, 2, 'B5', 8.50, 'STANDARD');

INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (2, 2, 'B6', 8.50, 'STANDARD');

INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (3, 3, 'C1', 18.00, 'VIP');

INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (3, 3, 'C2', 18.00, 'VIP');

INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type)
VALUES (3, 3, 'C3', 18.00, 'VIP');

COMMIT;

-- Verification
SELECT 'MOVIES' AS table_name, COUNT(*) AS rows FROM MOVIE
UNION ALL SELECT 'HALLS', COUNT(*) FROM HALL
UNION ALL SELECT 'STAFF', COUNT(*) FROM STAFF
UNION ALL SELECT 'CUSTOMERS', COUNT(*) FROM CUSTOMER
UNION ALL SELECT 'SHOWS', COUNT(*) FROM MOVIE_SHOW
UNION ALL SELECT 'BOOKINGS', COUNT(*) FROM BOOKING
UNION ALL SELECT 'BOOKING_SEATS', COUNT(*) FROM BOOKING_SEAT;
