create table `role` (
	`_id` INT AUTO_INCREMENT,
	`name` varchar(50) not null,
    primary key (`_id`)
)  default charset = UTF8;

create table  `entitytype` (
        `_id` INT AUTO_INCREMENT,
        `name` VARCHAR(50) NOT NULL,
        `unique_key` VARCHAR(50) NOT NULL,
        PRIMARY KEY (`_id`),
        UNIQUE (`unique_key`)
)  default charset = UTF8;

create table `role_entity` (
        `_fk_rid` INT NOT NULL,
        `_fk_etuk` VARCHAR(50) NOT NULL
        
) default charset = UTF8;

create table  `permission` (
	`_id` int auto_increment,
	`name` varchar(50) not null,
	primary key (`_id`)
) default charset = UTF8;

create table  `component` (
        `_id` INT AUTO_INCREMENT,
        `name` VARCHAR(50) NOT NULL,
		`_fk_pid` INT,
		PRIMARY KEY (`_id`)
		
) default charset = UTF8;

create table `permission_role` (
	`_id` int AUTO_INCREMENT,
	`_fk_rid` INT,
	`_fk_cid` INT NOT NULL,
	`_fk_pid` INT NOT NULL,
	primary key (`_id`)
	
)  default charset = UTF8;