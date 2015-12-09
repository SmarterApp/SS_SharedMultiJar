DELIMITER $$

drop function if exists `bigtoint` $$

create function `bigtoint`(n BIGINT) RETURNS int(11)
RETURN n$$

DELIMITER ;
