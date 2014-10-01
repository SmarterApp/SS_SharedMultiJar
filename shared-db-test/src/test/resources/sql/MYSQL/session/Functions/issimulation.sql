DELIMITER $$

drop function if exists `issimulation` $$

create function `issimulation`(
/*
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/26/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
	v_oppkey varbinary(16))
returns bit
begin

	declare v_sim bit;
    set v_sim = 0;

    select 1 into v_sim
    from _externs e, testopportunity o, sim_segment s
    where o._key = v_oppkey and e.clientname = o.clientname and e.environment = 'simulation'
        and s._fk_session = o._fk_session and s._efk_adminsubject = o._efk_adminsubject;

    return v_sim;

end$$

DELIMITER ;
