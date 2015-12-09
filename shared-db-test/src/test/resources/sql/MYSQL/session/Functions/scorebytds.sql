DELIMITER $$

drop function if exists `scorebytds` $$

create function `scorebytds`(
/*
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/1/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
	v_client varchar(100)
  , v_testid varchar(150)) RETURNS bit(1)
begin

	declare v_score bit;

    if (exists (select * from tdscore_dev_configs2012_sandbox.client_testscorefeatures 
				where clientname = v_client and testid = v_testid 
					and (reporttostudent = 1 or reporttoproctor = 1 or reporttoparticipation = 1 or useforability = 1))) then
       set v_score = 1;
    else 
		set v_score = 0;
	end if;
    
	return v_score;

end$$

DELIMITER ;
