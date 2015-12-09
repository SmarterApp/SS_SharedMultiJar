DELIMITER $$

drop procedure if exists `_createresponseset` $$

create procedure `_createresponseset`(
/*
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/1/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
	v_testoppkey varbinary(16)
  , v_maxitems int
  , v_reset int)
proc: begin

	declare v_numitems int;
    declare v_i int;
	
	if v_reset is null then set v_reset = 0; end if;

	select count(*) into v_numitems
	from testeeresponse
	where _fk_testopportunity = v_testoppkey; -- unhex(replace(v_testoppkey, '-', '')); 
	
	if (v_numitems > 0)	then -- even though possibly less than v_maxitems, insertitem will make up the difference automatically
		if (v_reset <> 0) then -- delete existing to make sure the item count is correct or to start the opportunity fresh
		begin
			delete 
			from testeeresponse 
			where _fk_testopportunity = v_testoppkey; -- unhex(replace(v_testoppkey, '-', ''));

			set v_numitems = 0;
		end;
		else 
			leave proc;	-- the set exists and reset is not desired, so just return
		end if;
	end if;
	
	drop table if exists tmptbl;
	create temporary table tmptbl(position int) engine = memory;

	set v_i = 1;

	while v_i <= v_maxitems do
	begin
		insert into tmptbl (position) values ( v_i);
		set v_i = v_i + 1;
	end;
	end while;

	insert into testeeresponse (_fk_testopportunity, position)
	select v_testoppkey, position 
	from tmptbl;

	drop table tmptbl;
	
end$$

DELIMITER ;
