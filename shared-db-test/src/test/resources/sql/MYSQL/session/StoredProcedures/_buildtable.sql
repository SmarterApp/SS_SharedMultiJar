DELIMITER $$

drop procedure if exists `_buildtable` $$

create procedure `_buildtable`(
/*
Description: build a table from a delimiter list

VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/25/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
	v_list text
  ,	v_delimit char)
begin  
  
	declare v_modifiedlist text;
	declare v_searchposition, v_commaposition, v_recordcount int;
	
	drop temporary table if exists resulttable;
	create temporary table resulttable (idx int, record varchar(255));
  	
	select trim(both v_delimit from v_list) into v_modifiedlist;
  	
	set v_searchposition = 1;
	set v_recordcount = 1;
	set v_commaposition = locate(v_delimit, v_modifiedlist, v_searchposition);
  
	while (v_commaposition > 1) do
	begin
		insert into resulttable (idx, record) values
		(v_recordcount, substring(v_modifiedlist from v_searchposition for (v_commaposition - v_searchposition)));
    		
		set v_recordcount = v_recordcount + 1;
		set v_searchposition = v_commaposition + 1;

		if (v_searchposition = length(v_modifiedlist)) then    			
			set v_commaposition = 0;
		else  
			set v_commaposition = locate(v_delimit, v_modifiedlist, v_searchposition);
		end if;

	end;
	end while;

	insert into resulttable (idx, record) values
	(v_recordcount, substring(v_modifiedlist from v_searchposition));
     
	select idx, record 
	from resulttable;

end$$

DELIMITER ;


