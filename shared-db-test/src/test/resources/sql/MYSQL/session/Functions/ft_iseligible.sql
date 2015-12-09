DELIMITER $$

drop function if exists `ft_iseligible` $$

create function `ft_iseligible`(
/*
Description: determine if a test opportunity is eligible for field test item administration,
			this function is what should be altered for changing deployment circumstances

VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/25/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
	v_oppkey varbinary(16)
  , v_testkey varchar(200)
  , v_parentkey varchar(250)
  , v_language varchar(20))
returns int
begin
	
	declare v_ftlength, v_langitemcount, v_ftitems int;	
    declare v_testid, v_parentid varchar(200);
	declare v_environment, v_clientname varchar(100);
    declare v_parentokay, v_testokay bit;
	declare v_today datetime(3);

	set v_today = now(3);

    select clientname into v_clientname 
	from testopportunity where _key = v_oppkey;

    select environment into v_environment 
	from externs where clientname = v_clientname;

    select count(*) into v_ftitems -- ftminitems
    from itembank_tblitemprops p, itembank_tblsetofadminitems i
    where p._fk_adminsubject = v_testkey and p.propname = 'language' and p.propvalue = v_language
        and i._fk_adminsubject = v_testkey and i.isfieldtest = 1 and p._fk_item = i._fk_item
        and i.isactive = 1 and p.isactive = 1;

    if (v_ftitems = 0) then return 0; end if;

    select testid, ftminitems into v_testid, v_ftlength
    from itembank_tblsetofadminsubjects s
    where s._key = v_testkey;
    
	-- if no field test items available
    if (v_ftlength = 0) then return 0; end if;   

	-- if there are field test items, then administration is always approved in simulations
    if (v_environment = 'simulation') then return 1; end if;      

    -- in production environment we honor field test start end dates. these may differ for parent test versus test segment. 
    -- first check for parent test
    set v_parentokay = 0;
    set v_testokay = 0;

    if (v_parentkey <> v_testkey) then
        select testid into v_parentid 
		from itembank_tblsetofadminsubjects where _key = v_parentkey;
    else 
		set v_parentid = v_testid;
	end if;
   
    select 1 into v_parentokay
	from tdsconfigs_client_testproperties
    where clientname = v_clientname and testid = v_parentid
		and (ftstartdate is null or ftstartdate <= v_today) and (ftenddate is null or ftenddate > v_today);

	-- if parent dates not met or this is not a test segment, don't go any further
    if (v_parentkey = v_testkey or v_parentokay = 0) then return v_parentokay; end if;   
    
	-- else, parent dates okay and is a test segment, so check the segment too
    select 1 into v_testokay 
	from tdsconfigs_client_segmentproperties
    where clientname = v_clientname and segmentid = v_testid
		and (ftstartdate is null or ftstartdate <= v_today) and (ftenddate is null or ftenddate > v_today);

    return v_testokay;


end$$

DELIMITER ;
