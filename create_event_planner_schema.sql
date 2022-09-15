DROP SCHEMA IF EXISTS `event_planner`;

CREATE SCHEMA `event_planner`;

use `event_planner`;

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
('john.doe@gmail.com','fun123','John','Doe'),
('mary.public@gmail.com','test123','Mary','Public');

DROP TABLE IF EXISTS `event`;

CREATE TABLE `event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(128) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `time` time DEFAULT NULL,
  `account_id` int(11) DEFAULT NULL, 
  PRIMARY KEY (`id`),
  
  KEY `FK_ACCOUNT_idx` (`account_id`),
  
  CONSTRAINT `FK_ACCOUNT` 
  FOREIGN KEY (`account_id`) 
  REFERENCES `account` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

INSERT INTO `event` (title,date,time,account_id)
VALUES
('Daily review','2022-08-31','09:30:00',1),
('Sprint review','2022-09-01','12:00:00',1),
('Dental appointment','2022-09-06','16:30:00',2);

-- DROP TABLE IF EXISTS `role`;

-- CREATE TABLE `role` (
--   `id` int(11) NOT NULL AUTO_INCREMENT,
--   `name` varchar(50) DEFAULT NULL,
--   PRIMARY KEY (`id`)
-- ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- INSERT INTO `role` (name)
-- VALUES 
-- ('ROLE_HOST'),('ROLE_GUEST');

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

-- DROP TABLE IF EXISTS `user_event`;

-- CREATE TABLE `user_event` (
--   `account_id` int(11) NOT NULL,
--   `event_id` int(11) NOT NULL,
--   `role_id` int(11) NOT NULL,
--   
--   PRIMARY KEY (`account_id`,`event_id`,`role_id`),
--   
--   KEY `FK_EVENT_ID_idx` (`event_id`),
--   
--   CONSTRAINT `FK_ACCOUNT_INVITED` FOREIGN KEY (`account_id`) 
--   REFERENCES `account` (`id`) 
--   ON DELETE NO ACTION ON UPDATE NO ACTION,
--   
--   CONSTRAINT `FK_EVENT_INVITATION` FOREIGN KEY (`event_id`) 
--   REFERENCES `event` (`id`) 
--   ON DELETE NO ACTION ON UPDATE NO ACTION,
--   
--   CONSTRAINT `FK_ROLE` FOREIGN KEY (`role_id`) 
--   REFERENCES `role` (`id`) 
--   ON DELETE NO ACTION ON UPDATE NO ACTION
-- ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- INSERT INTO `user_event` (account_id,event_id,role_id)
-- VALUES
-- (1,1,1),
-- (1,2,1),
-- (2,3,1),
-- (2,2,2);

SET FOREIGN_KEY_CHECKS = 1;
