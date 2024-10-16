-- Create the database if it does not exist
CREATE DATABASE IF NOT EXISTS `SubwayScreen`;

-- Use the created database
USE `SubwayScreen`;

--
-- Table structure for table `advertisements`
--
DROP TABLE IF EXISTS `advertisements`;

CREATE TABLE `advertisements` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` varchar(255) DEFAULT NULL,
  `description` text,
  `media_type` enum('PDF', 'MPG', 'JPEG', 'BMP') DEFAULT NULL,
  `media_path` varchar(255) DEFAULT NULL
);

--
-- Inserting data for table `advertisements`
--
INSERT INTO `advertisements` (`title`, `description`, `media_type`, `media_path`)
VALUES
('Pure Protein', 'Pure protein milk and bar', 'JPEG', 'media//pureprotien.jpg'),
('Neymar Puma', 'All new Puma Football Boots', 'JPEG', 'media//neymar.jpg'),
('LeBron Sprite', 'Wanna Sprite?', 'JPEG', 'media//lebron.jpg'),
('Messi Adidas', 'Impossible is nothing', 'JPEG', 'media//messi.jpg');

--
-- Selecting everything from advertisements
--
SELECT * FROM `advertisements`;
