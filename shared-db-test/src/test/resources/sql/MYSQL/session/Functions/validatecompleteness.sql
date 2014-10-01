DELIMITER $$

drop function if exists `validatecompleteness` $$

create function `validatecompleteness`(
/*
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/1/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
	v_testkey varchar(250)) 
RETURNS bit(1)
begin

    declare v_validate bit;
    set v_validate = 0;
    
    select validatecompleteness into v_validate
    from ${ConfigsDBName}.client_testproperties p, ${ConfigsDBName}.client_testmode m
    where m.testkey = v_testkey 
		and p.clientname = m.clientname 
		and p.testid = m.testid
	limit 1;

    return v_validate;

end$$

DELIMITER ;
