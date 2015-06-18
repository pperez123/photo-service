-- phpMyAdmin SQL Dump
-- version 4.2.7.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jun 18, 2015 at 07:58 PM
-- Server version: 5.7.4-m14-log
-- PHP Version: 5.5.15

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `photosite`
--

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
`user_id` int(11) NOT NULL,
  `user_type` varchar(100) NOT NULL,
  `first_name` varchar(500) DEFAULT NULL,
  `last_name` varchar(500) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `email` varchar(500) DEFAULT NULL,
  `fb_access_token` varchar(2000) DEFAULT NULL,
  `fb_expires_in` int(11) DEFAULT NULL,
  `fb_signed_request` varchar(5000) DEFAULT NULL,
  `fb_user_id` bigint(20) DEFAULT NULL,
  `fb_status` varchar(100) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `user_type`, `first_name`, `last_name`, `password`, `email`, `fb_access_token`, `fb_expires_in`, `fb_signed_request`, `fb_user_id`, `fb_status`, `created`, `updated`) VALUES
(6, 'facebook', 'Philip', 'Perez', NULL, 'pperez@slipsleeve.com', 'CAAJ3VujWHbsBAFwEs7xkuimoeNMDZBMOftSwsEGVqlajASKxitJc9214EXOWbh31D4Fb43nyzHteqm0KJWZBg9YIxTRFi0MfgFttY78LXFDltkXrSGKrbIyyhkLon8uCqFI8V8xYmyP0ZBjyup9mXgkJmVny9ZBgmwnemKrxNPk602NXLF4QR2nVPOT534D5BHVMyMD2MXq3ZCZB5G0C9yQToScY5NMBoZD', 6732, '_sbFQM73Gc8gjqN2Jjnqr8lsKGkicgT064cU0Z1ypT8.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImNvZGUiOiJBUUJSZmZINEN1T0VqTkZNcFV6WmRVek5BcUdybTVqbVpNRS1VVXUwWkRZaG9uaUNscDRGanJWQnN1VTJLbUlkVlFNN0pjNFVsOW9nSlBRbDJiUFloRjg0UFVFeWNza0stOF9xSE8tdFc1dUtJOUNfZXN5ZkZwMzJTU0h3RnNaQmlTVFR5NWFqYzU5VVRzZjZOWkpoak5ta25NeU1JdHhFd2dxTm1YSUZXSnItejJxZkplbk9WempMM21EdWlTTGlHVFp3am1mdjI3X21tMmljLVZHdWRnLWNBSG1YellEanE0OG84MWpwcGlKT3FjVTdXYkt0bmRBQjh5ZE8wNkZRbWo1T1RzNExyN05uYkN5NUZEQkJUQW5GMVl5WTRhY3FKTU14MjZYUTM4bHppNzhBTDhOakNDVkZhWDdINjE3YW50RVI1cUFVZkZjck9RU0I2blNYbGhTVCIsImlzc3VlZF9hdCI6MTQxMjEwMDQ2OCwidXNlcl9pZCI6IjEwMTUyNjU1NTAxMjM5NjQwIn0', 10152655501239640, 'connected', '2014-08-31 07:14:07', '2014-09-30 18:07:50');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
 ADD PRIMARY KEY (`user_id`), ADD UNIQUE KEY `fb_user_id` (`fb_user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=7;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
