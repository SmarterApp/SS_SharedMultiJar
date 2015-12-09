DELIMITER $$

drop function if exists `_aa_issegmentsatisfied` $$

create function `_aa_issegmentsatisfied`(
/*
DESCRIPTION: Determine if all required items have been selected for a segment of a test opp (just selected, not necessarily administered or responded to) 

VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/26/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
	v_oppkey varbinary(16)
  , v_segment int)
returns bit
begin

	declare v_opcnt, v_ftcnt int;
    declare v_segmentkey varchar(250);
    declare v_opneed, v_ftneed int;
    declare v_algorithm varchar(50);
    

    select opitemcnt, ftitemcnt, `algorithm`, _efk_segment into v_opneed, v_ftneed, v_algorithm, v_segmentkey
    from testopportunitysegment 
	where _fk_testopportunity = v_oppkey and segmentposition = v_segment;


    select count(*) into v_opcnt 
	from testeeresponse where _fk_testopportunity = v_oppkey and segment = v_segment and isfieldtest = 0;

    select count(*) into v_ftcnt 
	from testeeresponse where _fk_testopportunity = v_oppkey and segment = v_segment and isfieldtest = 1;

    if (v_algorithm = 'adaptive' and v_opcnt >= v_opneed and 
			(v_ftneed <= v_ftcnt or not exists (select * from ft_opportunityitem 
												where _fk_testopportunity = v_oppkey and _efk_fieldtest = v_segmentkey 
													and coalesce(deleted, 0) = 0 and dateadministered is null))
		) then
        return 1;
	else if (v_algorithm = 'fixedform' and v_opneed = v_opcnt + v_ftcnt) then
		return 1;
	end if;
	end if;

    return 0;

end$$

DELIMITER ;
