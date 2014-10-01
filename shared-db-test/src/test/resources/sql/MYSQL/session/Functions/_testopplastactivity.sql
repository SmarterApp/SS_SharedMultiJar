DELIMITER $$

drop function if exists `_testopplastactivity` $$

create function `_testopplastactivity`(
/*
DESCRIPTION: Determine the datetime of the last overt activity on the part of the testee

VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/26/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
	v_oppkey varbinary(16))
returns datetime(3)
begin

	declare v_fromtime datetime(3);	

	-- checking to see if the table already exists.
	-- if it already exists deleting the table content instead of dropping the table, b'coz 
	-- mysql does not allow explicit or implicit commits within a function.
	create temporary table if not exists tblfromtimes (lasttime datetime(3));	
	delete from tblfromtimes;

    insert into tblfromtimes (lasttime)
    select datepaused from testopportunity where _key = v_oppkey;

	set transaction isolation level read uncommitted;

	insert into tblfromtimes (lasttime)
	select  max(datesubmitted) 
	from testeeresponse
	where _fk_testopportunity = v_oppkey and datesubmitted is not null;

	insert into tblfromtimes (lasttime)
	select max(dategenerated) 
	from testeeresponse
	where _fk_testopportunity = v_oppkey and dategenerated is not null;

	set v_fromtime = (select max(lasttime) from tblfromtimes);

	return v_fromtime;

end$$

DELIMITER ;