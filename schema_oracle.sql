-- ============================================================
-- CINEMA MANAGEMENT SYSTEM — ORACLE SCHEMA
-- Run this AFTER drop_tables.sql (if needed) and BEFORE sample_data_oracle.sql
-- ============================================================

-- ------------------------------------------------------------
-- 1. MOVIE
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

-- ------------------------------------------------------------
-- 2. HALL
-- ------------------------------------------------------------
CREATE TABLE HALL (
    hall_id       NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    hall_name     VARCHAR2(255) NOT NULL UNIQUE,
    capacity      NUMBER NOT NULL CHECK (capacity > 0),
    hall_type     VARCHAR2(50)
);

-- ------------------------------------------------------------
-- 3. STAFF
-- ------------------------------------------------------------
CREATE TABLE STAFF (
    staff_id      NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name     VARCHAR2(255) NOT NULL,
    role          VARCHAR2(100),
    hire_date     DATE
);

-- ------------------------------------------------------------
-- 4. CUSTOMER
-- ------------------------------------------------------------
CREATE TABLE CUSTOMER (
    customer_id   NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name     VARCHAR2(255) NOT NULL,
    email         VARCHAR2(255) NOT NULL UNIQUE,
    phone         VARCHAR2(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 5. MOVIE_SHOW
-- ------------------------------------------------------------
CREATE TABLE MOVIE_SHOW (
    show_id       NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    movie_id      NUMBER NOT NULL,
    hall_id       NUMBER NOT NULL,
    show_datetime TIMESTAMP NOT NULL,
    base_price    NUMBER(10,2) NOT NULL CHECK (base_price >= 0),

    CONSTRAINT uq_show_slot
        UNIQUE (hall_id, show_datetime),

    CONSTRAINT fk_show_movie
        FOREIGN KEY (movie_id) REFERENCES MOVIE(movie_id),

    CONSTRAINT fk_show_hall
        FOREIGN KEY (hall_id)  REFERENCES HALL(hall_id)
);

-- ------------------------------------------------------------
-- 6. BOOKING
-- ------------------------------------------------------------
CREATE TABLE BOOKING (
    booking_id     NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id    NUMBER NOT NULL,
    show_id        NUMBER NOT NULL,
    staff_id       NUMBER,
    booking_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR2(50)
                     CHECK (payment_method IN ('CASH','CARD','QR','ONLINE')),
    total_amount   NUMBER(10,2) CHECK (total_amount >= 0),
    status         VARCHAR2(50)
                     DEFAULT 'CONFIRMED'
                     CHECK (status IN ('CONFIRMED','CANCELLED','PENDING')),

    CONSTRAINT fk_booking_customer
        FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id),

    CONSTRAINT fk_booking_show
        FOREIGN KEY (show_id)     REFERENCES MOVIE_SHOW(show_id),

    CONSTRAINT fk_booking_staff
        FOREIGN KEY (staff_id)    REFERENCES STAFF(staff_id)
);

-- ------------------------------------------------------------
-- 7. BOOKING_SEAT
-- ------------------------------------------------------------
CREATE TABLE BOOKING_SEAT (
    booking_seat_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_id      NUMBER NOT NULL,
    show_id         NUMBER NOT NULL,
    seat_number     VARCHAR2(10) NOT NULL,
    seat_price      NUMBER(10,2) NOT NULL CHECK (seat_price >= 0),
    seat_type       VARCHAR2(50) DEFAULT 'STANDARD',

    CONSTRAINT uq_show_seat
        UNIQUE (show_id, seat_number),

    CONSTRAINT fk_bs_booking
        FOREIGN KEY (booking_id) REFERENCES BOOKING(booking_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_bs_show
        FOREIGN KEY (show_id)    REFERENCES MOVIE_SHOW(show_id)
);

COMMIT;
