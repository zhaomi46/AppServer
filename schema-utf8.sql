CREATE TABLE `user` (
`userId`  int(11) NOT NULL AUTO_INCREMENT ,
`userType`  int(11) NULL DEFAULT NULL ,
`stuId`  int(11) NULL DEFAULT NULL ,
`name`  varchar(32)  NULL DEFAULT NULL ,
`avator`  varchar(128)  NULL DEFAULT NULL ,
`nickName`  varchar(32)  NULL DEFAULT NULL ,
`age`  int(11) NULL DEFAULT NULL ,
`sex`  int(11) NULL DEFAULT NULL ,
`grade`  int(11) NULL DEFAULT NULL ,
`major`  varchar(32) NULL DEFAULT NULL ,
`mailAddr`  varchar(64)  NULL DEFAULT NULL ,
`phoneNum`  varchar(32)  NULL DEFAULT NULL ,
`creditVal`  int(11) NULL DEFAULT NULL ,
`balance`  int(11) NULL DEFAULT NULL ,
`tags`  varchar(255)  NULL DEFAULT NULL ,
`password`  varchar(255) NULL DEFAULT NULL ,
PRIMARY KEY (`userId`)
)
ENGINE=InnoDB
ROW_FORMAT=DYNAMIC
;

CREATE TABLE `mission` (
`missionId`  int(11) NOT NULL AUTO_INCREMENT ,
`missionType`  int(11) NULL DEFAULT NULL ,
`publishTime`  varchar(32)  NULL DEFAULT NULL ,
`missionStatus`  int(11) NULL DEFAULT NULL ,
`title`  varchar(64)  NULL DEFAULT NULL ,
`deadLine`  varchar(32)  NULL DEFAULT NULL ,
`tags`  varchar(255)  NULL DEFAULT NULL ,
`money`  int(11) NULL DEFAULT NULL ,
`userId`  int(11) NULL DEFAULT NULL ,
`taskNum`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`missionId`),
FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `mission_ibfk_1` (`userId`) USING BTREE 
)
ENGINE=InnoDB
ROW_FORMAT=DYNAMIC
;

CREATE TABLE `task` (
`taskId`  int(11) NOT NULL AUTO_INCREMENT ,
`taskType`  int(11) NULL DEFAULT NULL ,
`taskStatus`  int(11) NULL DEFAULT NULL ,
`finishTime`  varchar(32)  NULL DEFAULT NULL ,
`pubUserId`  int(11) NULL DEFAULT NULL ,
`missionId`  int(11) NULL DEFAULT NULL ,
`accUserId`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`taskId`),
FOREIGN KEY (`accUserId`) REFERENCES `user` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`pubUserId`) REFERENCES `user` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`missionId`) REFERENCES `mission` (`missionId`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `tast_ibfk_1` (`pubUserId`) USING BTREE ,
INDEX `tast_ibfk_2` (`missionId`) USING BTREE ,
INDEX `accUserId` (`accUserId`) USING BTREE 
)
ENGINE=InnoDB
AUTO_INCREMENT=1
ROW_FORMAT=DYNAMIC
;

CREATE TABLE `questionare` (
`questionareId`  int(11) NOT NULL AUTO_INCREMENT ,
`taskId`  int(11) NULL DEFAULT NULL ,
`questionNum`  int(11) NULL DEFAULT NULL ,
`description`  varchar(255)  NULL DEFAULT NULL ,
`title`  varchar(255)  NULL DEFAULT NULL ,
PRIMARY KEY (`questionareId`),
FOREIGN KEY (`taskId`) REFERENCES `task` (`taskId`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `taskId` (`taskId`) USING BTREE 
)
ENGINE=InnoDB
ROW_FORMAT=DYNAMIC
;

CREATE TABLE `errand` (
`errandId`  int(11) NOT NULL AUTO_INCREMENT ,
`description`  varchar(255)  NULL DEFAULT NULL ,
`pic`  varchar(255)  NULL DEFAULT NULL ,
`taskId`  int(11) NULL DEFAULT NULL ,
`privateInfo`  varchar(255) NULL DEFAULT NULL ,
PRIMARY KEY (`errandId`),
FOREIGN KEY (`taskId`) REFERENCES `task` (`taskId`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `taskId` (`taskId`) USING BTREE 
)
ENGINE=InnoDB
ROW_FORMAT=DYNAMIC
;

CREATE TABLE `question` (
`questionId`  int(11) NOT NULL AUTO_INCREMENT ,
`questionType`  int(11) NULL DEFAULT NULL ,
`question`  varchar(255)  NULL DEFAULT NULL ,
`answer`  varchar(255)  NULL DEFAULT NULL ,
`choiceNum`  int(11) NULL DEFAULT NULL ,
`choiceStr`  varchar(255)  NULL DEFAULT NULL ,
`questionareId`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`questionId`),
FOREIGN KEY (`questionareId`) REFERENCES `questionare` (`questionareId`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `questionareId` (`questionareId`) USING BTREE 
)
ENGINE=InnoDB
ROW_FORMAT=DYNAMIC
;

CREATE TABLE `report` (
`reportId`  int(11) NOT NULL AUTO_INCREMENT ,
`reportStatus`  int(11) NULL DEFAULT NULL ,
`reason`  varchar(255)  NULL DEFAULT NULL ,
`result`  varchar(255)  NULL DEFAULT NULL ,
`userId`  int(11) NULL DEFAULT NULL ,
`missionId`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`reportId`),
FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`missionId`) REFERENCES `mission` (`missionId`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `report_ibfk_1` (`userId`) USING BTREE ,
INDEX `report_ibfk_2` (`missionId`) USING BTREE 
)
ENGINE=InnoDB
ROW_FORMAT=DYNAMIC
;

CREATE TABLE `judge` (
`judgeId`  int(11) NOT NULL AUTO_INCREMENT ,
`judgeStatus`  int(11) NULL DEFAULT NULL ,
`description`  varchar(255)  NULL DEFAULT NULL ,
`result`  varchar(255) NULL DEFAULT NULL ,
`taskId`  int(11) NULL DEFAULT NULL ,
`appUserId`  int(11) NULL DEFAULT NULL ,
`isPubliserApply`  bit(1) NULL DEFAULT NULL ,
`accUserId`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`judgeId`),
FOREIGN KEY (`taskId`) REFERENCES `task` (`taskId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`appUserId`) REFERENCES `user` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`accUserId`) REFERENCES `user` (`userId`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `taskId` (`taskId`) USING BTREE ,
INDEX `appUserId` (`appUserId`) USING BTREE ,
INDEX `accUserId` (`accUserId`) USING BTREE 
)
ENGINE=InnoDB
ROW_FORMAT=DYNAMIC
;




