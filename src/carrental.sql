-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 30, 2025 at 09:16 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `carrental`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `addCar` (IN `p_model` VARCHAR(50), IN `p_brand` VARCHAR(50), IN `p_type` VARCHAR(50), IN `p_price_per_hour` DECIMAL(10,2), IN `p_seats` INT, IN `p_availability` BOOLEAN)   BEGIN
    INSERT INTO car (model, brand, type, price_per_hour, seats, availability)
    VALUES (p_model, p_brand, p_type, p_price_per_hour, p_seats, p_availability);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `calculate_rental_cost` (IN `p_start` DATETIME, IN `p_expected_end` DATETIME, IN `p_actual_return` DATETIME, IN `p_price_per_hour` DECIMAL(10,2), IN `p_late_fee_per_hour` DECIMAL(10,2), OUT `p_total_hours` INT, OUT `p_late_hours` INT, OUT `p_rental_cost` DECIMAL(10,2), OUT `p_late_fee` DECIMAL(10,2), OUT `p_total_cost` DECIMAL(10,2))   BEGIN
    DECLARE total_minutes INT;
    DECLARE rental_hours INT;
    DECLARE late_hours INT DEFAULT 0;

    -- Total rental duration (minutes)
    SET total_minutes = TIMESTAMPDIFF(MINUTE, p_start, p_actual_return);

    -- Round up to next hour if not exact
    SET rental_hours = CEIL(total_minutes / 60);

    -- Calculate late hours (only if returned late)
    IF p_actual_return > p_expected_end THEN
        SET late_hours = TIMESTAMPDIFF(HOUR, p_expected_end, p_actual_return);
    END IF;

    -- Costs
    SET p_total_hours = rental_hours;
    SET p_late_hours = late_hours;
    SET p_rental_cost = rental_hours * p_price_per_hour;
    SET p_late_fee = late_hours * p_late_fee_per_hour;
    SET p_total_cost = p_rental_cost + p_late_fee;
END$$

--
-- Functions
--
CREATE DEFINER=`root`@`localhost` FUNCTION `getAverageRating` (`p_car_id` INT) RETURNS DECIMAL(3,2) DETERMINISTIC READS SQL DATA BEGIN
    RETURN (
        SELECT COALESCE(AVG(rating), 0)
        FROM ratings
        WHERE car_id = p_car_id
    );
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `a_id` int(10) NOT NULL,
  `adminname` varchar(20) NOT NULL,
  `password` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`a_id`, `adminname`, `password`) VALUES
(1, 'admin', 'admin123'),
(2, 'super1', 'superpass'),
(3, 'owner99', 'car2024'),
(4, 'manager', 'rentme'),
(5, 'fleetadmin', 'pass456');

-- --------------------------------------------------------

--
-- Table structure for table `bookings`
--

CREATE TABLE `bookings` (
  `booking_id` int(11) NOT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `car_id` int(11) DEFAULT NULL,
  `start_location` varchar(100) DEFAULT NULL,
  `end_location` varchar(100) DEFAULT NULL,
  `start_datetime` datetime DEFAULT NULL,
  `end_datetime` datetime DEFAULT NULL,
  `total_hours` double DEFAULT NULL,
  `total_cost` double DEFAULT NULL,
  `status` varchar(20) DEFAULT 'Booked'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bookings`
--

INSERT INTO `bookings` (`booking_id`, `customer_id`, `car_id`, `start_location`, `end_location`, `start_datetime`, `end_datetime`, `total_hours`, `total_cost`, `status`) VALUES
(1, 1, 1, 'home', 'college', '2025-08-18 07:00:00', '2025-08-18 10:49:14', 4, 1300, 'Returned'),
(2, 2, 8, 'home', 'office', '2025-08-18 07:00:00', '2025-08-22 13:14:32', 103, 97350, 'Returned'),
(3, 1, 5, 'andscbjhaesbvjh', 'skjdgcjw', '2025-08-18 11:00:00', '2025-08-21 05:59:12', 67, 27950, 'Returned'),
(4, 4, 10, 'jnckjan', 'jacn', '2025-08-21 07:00:00', '2025-08-22 13:12:05', 31, 19150, 'Returned'),
(5, 4, 2, 'knck', 'lkndacn', '2025-08-21 07:00:00', '2025-08-21 09:55:32', 3, 660, 'Returned'),
(7, 6, 11, 'home', 'office', '2025-08-23 07:00:00', '2025-08-23 19:00:00', 12, 1200, 'Cancelled'),
(8, 6, 11, 'vapi', 'ahm', '2025-08-23 08:00:00', '2025-08-23 19:00:00', 11, 1100, 'Booked'),
(9, 4, 10, 'home', 'LJ Uni', '2025-08-23 10:00:00', '2025-08-23 12:00:00', 2, 800, 'Overdue'),
(10, 3, 15, 'surat', 'ahm', '2025-08-23 13:00:00', '2025-08-23 13:12:01', 1, 100000, 'Returned'),
(11, 7, 3, 'shdyhd', 'sjsyd', '2025-08-23 17:00:00', '2025-08-24 17:00:00', 24, 7200, 'Booked');

--
-- Triggers `bookings`
--
DELIMITER $$
CREATE TRIGGER `set_car_unavailable_after_booking` AFTER INSERT ON `bookings` FOR EACH ROW BEGIN
    UPDATE car
    SET availability = 0
    WHERE car_id = NEW.car_id;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `car`
--

CREATE TABLE `car` (
  `car_id` int(11) NOT NULL,
  `model` varchar(20) NOT NULL,
  `brand` varchar(20) NOT NULL,
  `type` varchar(10) NOT NULL,
  `seats` int(10) NOT NULL,
  `price_per_hour` double NOT NULL,
  `availability` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `car`
--

INSERT INTO `car` (`car_id`, `model`, `brand`, `type`, `seats`, `price_per_hour`, `availability`) VALUES
(1, 'Swift', 'Maruti Suz', 'Hatchback', 5, 190, 1),
(2, 'Baleno', 'Maruti Suz', 'Hatchback', 5, 220, 1),
(3, 'City', 'Honda', 'Sedan', 5, 300, 0),
(4, 'Amaze', 'Honda', 'Sedan', 5, 280, 1),
(5, 'Creta', 'Hyundai', 'SUV', 5, 350, 1),
(6, 'Venue', 'Hyundai', 'SUV', 5, 330, 1),
(7, 'Innova Crysta', 'Toyota', 'MUV', 7, 500, 1),
(8, 'Fortuner', 'Toyota', 'SUV', 7, 700, 1),
(9, 'XUV500', 'Mahindra', 'SUV', 7, 450, 1),
(10, 'Thar', 'Mahindra', 'SUV', 4, 400, 0),
(11, 'Alto K10', 'Maruti Suz', 'Hatchback', 5, 100, 0),
(12, 'i10 NIOS', 'Hyundai', 'Hatchback', 5, 130, 0),
(13, 'Ertiga', 'Maruti Suz', 'MUV', 7, 200, 1),
(14, 'Scorpio-N', 'Mahindra', 'SUV', 7, 400, 1),
(15, 'aventador', 'lamborghini', 'super car', 2, 100000, 1),
(16, 'Tiago', 'Tata', 'Hatchback', 5, 115, 1),
(17, 'Nexon', 'Tata', 'SUV', 5, 210, 1);

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `customer_id` int(10) NOT NULL,
  `name` varchar(20) NOT NULL,
  `email` varchar(25) NOT NULL,
  `phone_no` varchar(20) NOT NULL,
  `address` varchar(100) NOT NULL,
  `dob` date NOT NULL,
  `password_hash` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`customer_id`, `name`, `email`, `phone_no`, `address`, `dob`, `password_hash`) VALUES
(1, 'Priya Shah', 'priyas@gmail.com', '987123645', 'Ahmedabad', '2006-11-18', 2742),
(2, 'Raj Sharma', 'rajs@gmail.com', '9993457834', 'Baroda', '2001-09-17', 2458),
(3, 'het', 'hetmewada01@gmail.com', '9898249999', 'Gandhinagar', '2007-03-01', 2228),
(4, 'Prithvi Patel', 'prithvip1811@gmail.com', '6789012345', 'Ahmedabad', '2006-11-18', 2716),
(6, 'Mehul Mehta', 'mehulm@gmail.in', '7890123456', 'vapi', '1975-10-20', 2758),
(7, 'prithvi', 'prithvip1811@gmail.com', '6789012345', 'ahmdabad', '2006-11-18', 3619);

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `payment_id` int(11) NOT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `payment_method` varchar(20) DEFAULT NULL,
  `payment_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `bill_text` longtext DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payment`
--

INSERT INTO `payment` (`payment_id`, `booking_id`, `customer_id`, `amount`, `payment_method`, `payment_date`, `bill_text`) VALUES
(1, 1, 1, 1300, 'cash', '2025-08-18 05:19:19', '\n========== 🧾 RENTAL BILL ==========\n📄 Booking ID     : 1\n🚗 Car ID         : 1\n🔤 Model          : Swift\n🏷️ Brand          : Maruti Suz\n🚘 Type           : Hatchback\n🪑 Seats          : 5\n-----------------------------------\n📍 Start Time     : 2025-08-18 07:00\n📍 Expected Return: 2025-08-18 08:00\n📍 Actual Return  : 2025-08-18 10:49\n-----------------------------------\n⏱ Total Hours     : 4.0 hrs\n💰 Base Price      : ₹800.00\n⏰ Late Hours      : 2.0 hrs\n🔻 Late Fee        : ₹500.00\n-----------------------------------\n💳 Total Paid      : ₹1300.00\n💳 Payment Method  : cash\n✅ Thank you for choosing CarVerse!\n=====================================\n'),
(2, 3, 1, 27950, 'upi', '2025-08-21 00:26:23', '\n========== 🧾 RENTAL BILL ==========\n📄 Booking ID     : 3\n🚗 Car ID         : 5\n🔤 Model          : Creta\n🏷️ Brand          : Hyundai\n🚘 Type           : SUV\n🪑 Seats          : 5\n-----------------------------------\n📍 Start Time     : 2025-08-18 11:00\n📍 Expected Return: 2025-08-20 11:00\n📍 Actual Return  : 2025-08-21 05:56\n-----------------------------------\n⏱ Total Hours     : 67.0 hrs\n💰 Base Price      : ₹23450.00\n⏰ Late Hours      : 18.0 hrs\n🔻 Late Fee        : ₹4500.00\n-----------------------------------\n💳 Total Paid      : ₹27950.00\n💳 Payment Method  : upi\n✅ Thank you for choosing CarVerse!\n=====================================\n'),
(3, 3, 1, 27950, 'upi', '2025-08-21 00:29:30', '\n========== 🧾 RENTAL BILL ==========\n📄 Booking ID     : 3\n🚗 Car ID         : 5\n🔤 Model          : Creta\n🏷️ Brand          : Hyundai\n🚘 Type           : SUV\n🪑 Seats          : 5\n-----------------------------------\n📍 Start Time     : 2025-08-18 11:00\n📍 Expected Return: 2025-08-20 11:00\n📍 Actual Return  : 2025-08-21 05:59\n-----------------------------------\n⏱ Total Hours     : 67.0 hrs\n💰 Base Price      : ₹23450.00\n⏰ Late Hours      : 18.0 hrs\n🔻 Late Fee        : ₹4500.00\n-----------------------------------\n💳 Total Paid      : ₹27950.00\n💳 Payment Method  : upi\n✅ Thank you for choosing CarVerse!\n=====================================\n'),
(4, 5, 4, 660, 'upi', '2025-08-21 04:25:55', '\n========== 🧾 RENTAL BILL ==========\n📄 Booking ID     : 5\n🚗 Car ID         : 2\n🔤 Model          : Baleno\n🏷️ Brand          : Maruti Suz\n🚘 Type           : Hatchback\n🪑 Seats          : 5\n-----------------------------------\n📍 Start Time     : 2025-08-21 07:00\n📍 Expected Return: 2025-08-21 09:00\n📍 Actual Return  : 2025-08-21 09:55\n-----------------------------------\n⏱ Total Hours     : 3.0 hrs\n💰 Base Price      : ₹660.00\n⏰ Late Hours      : 0.0 hrs\n🔻 Late Fee        : ₹0.00\n-----------------------------------\n💳 Total Paid      : ₹660.00\n💳 Payment Method  : upi\n✅ Thank you for choosing CarVerse!\n=====================================\n'),
(5, 4, 4, 19150, 'card', '2025-08-22 07:42:31', '\n========== 🧾 RENTAL BILL ==========\n📄 Booking ID     : 4\n🚗 Car ID         : 10\n🔤 Model          : Thar\n🏷️ Brand          : Mahindra\n🚘 Type           : SUV\n🪑 Seats          : 4\n-----------------------------------\n📍 Start Time     : 2025-08-21 07:00\n📍 Expected Return: 2025-08-21 10:00\n📍 Actual Return  : 2025-08-22 13:12\n-----------------------------------\n⏱ Total Hours     : 31.0 hrs\n💰 Base Price      : ₹12400.00\n⏰ Late Hours      : 27.0 hrs\n🔻 Late Fee        : ₹6750.00\n-----------------------------------\n💳 Total Paid      : ₹19150.00\n💳 Payment Method  : card\n✅ Thank you for choosing CarVerse!\n=====================================\n'),
(6, 2, 2, 97350, 'upi', '2025-08-22 07:45:46', '\n========== 🧾 RENTAL BILL ==========\n📄 Booking ID     : 2\n🚗 Car ID         : 8\n🔤 Model          : Fortuner\n🏷️ Brand          : Toyota\n🚘 Type           : SUV\n🪑 Seats          : 7\n-----------------------------------\n📍 Start Time     : 2025-08-18 07:00\n📍 Expected Return: 2025-08-18 08:00\n📍 Actual Return  : 2025-08-22 13:14\n-----------------------------------\n⏱ Total Hours     : 103.0 hrs\n💰 Base Price      : ₹72100.00\n⏰ Late Hours      : 101.0 hrs\n🔻 Late Fee        : ₹25250.00\n-----------------------------------\n💳 Total Paid      : ₹97350.00\n💳 Payment Method  : upi\n✅ Thank you for choosing CarVerse!\n=====================================\n'),
(7, 10, 3, 100000, 'cash', '2025-08-23 07:42:13', '\n=====================================\n           🧾  RENTAL BILL  🧾\n=====================================\n📄 Booking ID      : 10\n🚗 Car ID          : 15\n🔤 Model           : aventador\n🏷️ Brand           : lamborghini\n🚘 Type            : super car\n🪑 Seats           : 2\n-------------------------------------\n📍 Start Time      : 2025-08-23 13:00\n📍 Expected Return : 2025-08-25 13:00\n📍 Actual Return   : 2025-08-23 13:12\n-------------------------------------\n⏱ Total Hours     : 1.0 hrs\n💰 Base Price      : ₹100000.00\n⏰ Late Hours      : 0.0 hrs\n🔻 Late Fee        : ₹0.00\n-------------------------------------\n💳 Total Paid      : ₹100000.00\n💳 Payment Method  : cash\n\n✅ Thank you for choosing CarVerse!\n=====================================\n');

-- --------------------------------------------------------

--
-- Table structure for table `ratings`
--

CREATE TABLE `ratings` (
  `rating_id` int(11) NOT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `car_id` int(11) DEFAULT NULL,
  `rating` int(11) DEFAULT NULL CHECK (`rating` between 1 and 5),
  `rating_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ratings`
--

INSERT INTO `ratings` (`rating_id`, `customer_id`, `car_id`, `rating`, `rating_date`) VALUES
(1, 2, 2, 4, '2025-08-18 00:37:58'),
(2, 2, 3, 3, '2025-08-18 00:38:18'),
(3, 2, 4, 5, '2025-08-18 00:38:33'),
(4, 2, 5, 5, '2025-08-18 00:38:43'),
(5, 2, 4, 4, '2025-08-18 00:38:57'),
(6, 2, 5, 4, '2025-08-18 00:39:07'),
(7, 2, 6, 5, '2025-08-18 00:39:20'),
(8, 2, 7, 2, '2025-08-18 00:39:35'),
(9, 2, 8, 5, '2025-08-18 00:39:48'),
(10, 2, 8, 5, '2025-08-18 00:40:01'),
(11, 2, 9, 3, '2025-08-18 00:40:19'),
(12, 2, 9, 5, '2025-08-18 00:40:44'),
(13, 2, 10, 5, '2025-08-18 00:40:58'),
(14, 1, 3, 4, '2025-08-18 00:43:03'),
(15, 1, 4, 5, '2025-08-18 00:45:46'),
(16, 1, 5, 4, '2025-08-18 00:46:00'),
(17, 1, 6, 3, '2025-08-18 00:47:40'),
(18, 1, 9, 3, '2025-08-18 00:48:22'),
(19, 1, 10, 4, '2025-08-18 00:48:40'),
(20, 1, 2, 5, '2025-08-18 00:49:00'),
(21, 1, 5, 4, '2025-08-18 00:49:22'),
(22, 1, 2, 4, '2025-08-18 00:49:48'),
(23, 1, 7, 5, '2025-08-18 00:50:13'),
(24, 1, 9, 4, '2025-08-18 00:51:18'),
(25, 1, 9, 5, '2025-08-18 00:51:30'),
(26, 1, 1, 4, '2025-08-21 00:32:45'),
(27, 3, 15, 5, '2025-08-23 07:44:07');

-- --------------------------------------------------------

--
-- Table structure for table `rental`
--

CREATE TABLE `rental` (
  `rental_id` int(11) NOT NULL,
  `car_id` int(11) DEFAULT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `rent_date` date DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `return_date` date DEFAULT NULL,
  `total_price` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `rental`
--

INSERT INTO `rental` (`rental_id`, `car_id`, `customer_id`, `rent_date`, `due_date`, `return_date`, `total_price`) VALUES
(1, 1, 1, '2025-08-18', '2025-08-18', '2025-08-18', 1300),
(2, 5, 1, '2025-08-18', '2025-08-20', '2025-08-21', 27950),
(3, 2, 4, '2025-08-21', '2025-08-21', '2025-08-21', 660),
(4, 10, 4, '2025-08-21', '2025-08-21', '2025-08-22', 19150),
(5, 8, 2, '2025-08-18', '2025-08-18', '2025-08-22', 97350),
(6, 15, 3, '2025-08-23', '2025-08-25', '2025-08-23', 100000);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`a_id`);

--
-- Indexes for table `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `fk_bookings_customer` (`customer_id`),
  ADD KEY `fk_bookings_car` (`car_id`);

--
-- Indexes for table `car`
--
ALTER TABLE `car`
  ADD PRIMARY KEY (`car_id`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`customer_id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`payment_id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `customer_id` (`customer_id`);

--
-- Indexes for table `ratings`
--
ALTER TABLE `ratings`
  ADD PRIMARY KEY (`rating_id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `car_id` (`car_id`);

--
-- Indexes for table `rental`
--
ALTER TABLE `rental`
  ADD PRIMARY KEY (`rental_id`),
  ADD KEY `car_id` (`car_id`),
  ADD KEY `customer_id` (`customer_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `a_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `car`
--
ALTER TABLE `car`
  MODIFY `car_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `customer_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
  MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `ratings`
--
ALTER TABLE `ratings`
  MODIFY `rating_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT for table `rental`
--
ALTER TABLE `rental`
  MODIFY `rental_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `fk_bookings_car` FOREIGN KEY (`car_id`) REFERENCES `car` (`car_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_bookings_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE;

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
  ADD CONSTRAINT `payment_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`);

--
-- Constraints for table `ratings`
--
ALTER TABLE `ratings`
  ADD CONSTRAINT `ratings_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `ratings_ibfk_2` FOREIGN KEY (`car_id`) REFERENCES `car` (`car_id`) ON DELETE CASCADE;

--
-- Constraints for table `rental`
--
ALTER TABLE `rental`
  ADD CONSTRAINT `rental_ibfk_1` FOREIGN KEY (`car_id`) REFERENCES `car` (`car_id`),
  ADD CONSTRAINT `rental_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
