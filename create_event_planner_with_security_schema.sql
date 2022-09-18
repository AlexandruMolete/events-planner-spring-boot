DROP SCHEMA IF EXISTS `event_planner_with_security`;

CREATE SCHEMA `event_planner_with_security`;

use `event_planner_with_security`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `account`;

CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(128) NOT NULL,
  `password` char(68) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

INSERT INTO `account` (email,password,first_name,last_name)
VALUES 
('john.doe@gmail.com','$2a$04$eFytJDGtjbThXa80FyOOBuFdK2IwjyWefYkMpiBEFlpBwDH.5PM0K','John','Doe');

DROP TABLE IF EXISTS `event`;

CREATE TABLE `event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `time` time DEFAULT NULL,
  -- `account_id` int(11) DEFAULT NULL,  
  PRIMARY KEY (`id`)
--   
--   KEY `FK_ACCOUNT_idx` (`account_id`),
--   
--   CONSTRAINT `FK_ACCOUNT` 
--   FOREIGN KEY (`account_id`) 
--   REFERENCES `account` (`id`) 
--   ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

INSERT INTO `event` (name,date,time)
VALUES
('Daily review','2022-08-31','09:30:00'),
('Sprint review','2022-09-01','12:00:00'),
('Dental appointment','2022-09-06','16:30:00');

DROP TABLE IF EXISTS `accounts_events`;

CREATE TABLE `accounts_events` (
  `account_id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL,
  
  PRIMARY KEY (`account_id`,`event_id`),
  
  KEY `FK_EVENTS_idx` (`event_id`),
  
  CONSTRAINT `FK_ACCOUNTS` FOREIGN KEY (`account_id`) 
  REFERENCES `account` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION,
  
  CONSTRAINT `FK_EVENTS` FOREIGN KEY (`event_id`) 
  REFERENCES `event` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `accounts_events` (account_id,event_id)
VALUES
(1,1),
(1,2),
(1,3);

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

INSERT INTO `role` (name)
VALUES 
('ROLE_HOST'),('ROLE_GUEST');

DROP TABLE IF EXISTS `reminder`;

CREATE TABLE `reminder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time` time DEFAULT NULL,
  `event_id` int(11) DEFAULT NULL,
  
  PRIMARY KEY (`id`),
  
  KEY `FK_EVENT_idx` (`event_id`),
  
  CONSTRAINT `FK_EVENT` 
  FOREIGN KEY (`event_id`) 
  REFERENCES `event` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

INSERT INTO `reminder` (time,event_id)
VALUES
('00:15:00',1),
('00:15:00',2),
('01:00:00',3),
('00:30:00',2);

DROP TABLE IF EXISTS `account_role`;

CREATE TABLE `account_role` (
  `account_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  
  PRIMARY KEY (`account_id`,`role_id`),
  
  KEY `FK_ROLE_idx` (`role_id`),
  
  CONSTRAINT `FK_ACCOUNT` FOREIGN KEY (`account_id`) 
  REFERENCES `account` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION,
  
  CONSTRAINT `FK_ROLES` FOREIGN KEY (`role_id`) 
  REFERENCES `role` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `account_role` (account_id,role_id)
VALUES
(1,1);

SET FOREIGN_KEY_CHECKS = 1;
