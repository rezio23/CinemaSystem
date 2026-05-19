-- ============================================================
-- USEFUL QUERIES FOR CINEMA MANAGEMENT SYSTEM
-- Run these ad-hoc for reporting / verification
-- ============================================================

-- ------------------------------------------------------------
-- 1. FULL BOOKING RECEIPT
-- ------------------------------------------------------------
SELECT
    b.booking_id,
    c.full_name AS customer_name,
    m.title AS movie_title,
    h.hall_name,
    ms.show_datetime,
    bs.seat_number,
    bs.seat_type,
    bs.seat_price,
    b.payment_method,
    b.total_amount,
    st.full_name AS served_by
FROM BOOKING b
JOIN CUSTOMER c
    ON c.customer_id = b.customer_id
JOIN MOVIE_SHOW ms
    ON ms.show_id = b.show_id
JOIN MOVIE m
    ON m.movie_id = ms.movie_id
JOIN HALL h
    ON h.hall_id = ms.hall_id
JOIN BOOKING_SEAT bs
    ON bs.booking_id = b.booking_id
LEFT JOIN STAFF st
    ON st.staff_id = b.staff_id
ORDER BY b.booking_id, bs.seat_number;


-- ------------------------------------------------------------
-- 2. TOTAL REVENUE PER MOVIE
-- ------------------------------------------------------------
SELECT
    m.title,
    COUNT(bs.seat_number) AS seats_sold,
    SUM(bs.seat_price) AS total_revenue
FROM MOVIE m
JOIN MOVIE_SHOW ms
    ON ms.movie_id = m.movie_id
JOIN BOOKING b
    ON b.show_id = ms.show_id
JOIN BOOKING_SEAT bs
    ON bs.booking_id = b.booking_id
GROUP BY m.title
ORDER BY total_revenue DESC;


-- ------------------------------------------------------------
-- 3. CUSTOMER BOOKING HISTORY
-- ------------------------------------------------------------
SELECT
    b.booking_id,
    m.title,
    ms.show_datetime,
    h.hall_name,
    b.total_amount,
    b.status
FROM BOOKING b
JOIN CUSTOMER c
    ON c.customer_id = b.customer_id
JOIN MOVIE_SHOW ms
    ON ms.show_id = b.show_id
JOIN MOVIE m
    ON m.movie_id = ms.movie_id
JOIN HALL h
    ON h.hall_id = ms.hall_id
WHERE c.email = 'john.smith@email.com'
ORDER BY ms.show_datetime;


-- ------------------------------------------------------------
-- 4. TAKEN SEATS FOR A SHOW
-- ------------------------------------------------------------
SELECT
    bs.seat_number,
    bs.seat_type,
    c.full_name AS booked_by
FROM BOOKING_SEAT bs
JOIN BOOKING b
    ON b.booking_id = bs.booking_id
JOIN CUSTOMER c
    ON c.customer_id = b.customer_id
WHERE bs.show_id = 1
AND b.status = 'CONFIRMED'
ORDER BY bs.seat_number;


-- ------------------------------------------------------------
-- 5. TABLE ROW COUNTS (verification)
-- ------------------------------------------------------------
SELECT 'MOVIES' AS table_name, COUNT(*) AS rows FROM MOVIE
UNION ALL SELECT 'HALLS', COUNT(*) FROM HALL
UNION ALL SELECT 'STAFF', COUNT(*) FROM STAFF
UNION ALL SELECT 'CUSTOMERS', COUNT(*) FROM CUSTOMER
UNION ALL SELECT 'SHOWS', COUNT(*) FROM MOVIE_SHOW
UNION ALL SELECT 'BOOKINGS', COUNT(*) FROM BOOKING
UNION ALL SELECT 'BOOKING_SEATS', COUNT(*) FROM BOOKING_SEAT;
