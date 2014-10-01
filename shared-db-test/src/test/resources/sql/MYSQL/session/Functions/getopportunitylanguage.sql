DELIMITER $$

drop function if exists `getopportunitylanguage` $$

create function `getopportunitylanguage`(
/*
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/25/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
	v_oppkey varbinary(16))
returns varchar(20)
begin

	declare v_lang varchar(20);

    select acccode into v_lang
    from testeeaccommodations 
	where acctype = 'language' and _fk_testopportunity = v_oppkey;

    return v_lang;

end$$

DELIMITER ;
