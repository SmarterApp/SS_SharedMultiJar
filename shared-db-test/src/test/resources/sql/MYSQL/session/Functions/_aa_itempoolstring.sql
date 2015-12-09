DELIMITER $$

drop function if exists `_aa_itempoolstring` $$

create function `_aa_itempoolstring`(
/*
Description: at the start of a new test opportunity, establish the custom itempool to use for a segment's non-fieldtest items
			it is the responsibility of the item selection processes to determine specific applicability of each item (isactive, previously administered, etc.)
			since an itempool cannot be altered (by subtraction) this procedure only needs to be run at the start of a new test opp
			however, it can be run to add to an itempool while the test is in progress, though this is almost never required.
			the custom itempool is not required for fixed form tests.

VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/25/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
	v_oppkey varbinary(16)
  , v_segmentkey varchar(250))
returns text
begin

    declare v_itemstring 	text;
	declare v_testid 		varchar(150);
	declare v_clientname 	varchar(100);
	declare v_starttime 	datetime(3);
	declare v_session 		varbinary(16);
	declare v_language 		varchar(50);
    declare v_itemkey 		varchar(50);    

	set v_itemstring = null;
	set v_starttime = now();

    select _efk_testid, clientname, _fk_session into v_testid, v_clientname, v_session
    from testopportunity 
	where _key = v_oppkey;

    select acccode into v_language 
	from testeeaccommodations 
	where _fk_testopportunity = v_oppkey and acctype = 'language';

	-- checking to see if the table already exists.
	-- if it already exists deleting the table content instead of dropping the table, b'coz 
	-- mysql does not allow explicit or implicit commits within a function.
    create temporary table if not exists tblpool (itemkey varchar(50) primary key not null);
	delete from tblpool;

	set transaction isolation level read uncommitted;

    insert into tblpool (itemkey)
    select distinct i._fk_item 
    from itembank_tblsetofadminitems i, tdsconfigs_client_test_itemconstraint c1, 
        testeeaccommodations a1, itembank_tblitemprops p1
    where i._fk_adminsubject = v_segmentkey 
        and c1.clientname = v_clientname and c1.testid = v_testid and c1.item_in = 1
        and a1._fk_testopportunity = v_oppkey and a1.acctype = c1.tooltype and a1.acccode = c1.toolvalue
        and p1._fk_adminsubject = v_segmentkey and p1._fk_item  = i._fk_item and p1.propname = c1.propname and p1.propvalue = c1.propvalue and p1.isactive = 1
		and not exists    -- exclude any item possessing an 'item_out' constraint
				  ( select * 
					from tdsconfigs_client_test_itemconstraint c2, testeeaccommodations a2, itembank_tblitemprops p2
					where a2._fk_testopportunity = v_oppkey 
						and c2.clientname = v_clientname and c2.testid = v_testid and c2.item_in = 0
						and a2.acctype = c2.tooltype and a2.acccode = c2.toolvalue
						and p2._fk_adminsubject = v_segmentkey and p2._fk_item  = i._fk_item and p2.propname = c2.propname and p2.propvalue = c2.propvalue and p2.isactive = 1
				)
	 -- there should be the same number of records per item as there are item_in constraints, check in the 'having' clause
     group by i._fk_item
     having count(*) = (select count(*) 
						from tdsconfigs_client_test_itemconstraint c1, testeeaccommodations a1
						where c1.clientname = v_clientname and c1.testid = v_testid and c1.item_in = 1
							and a1._fk_testopportunity = v_oppkey and a1.acctype = c1.tooltype and a1.acccode = c1.toolvalue
					);


    -- logic to concatinate all the rows in tblpool as a comma-seperated value
    while (exists (select * from tblpool)) do
        select itemkey into v_itemkey 
		from tblpool limit 1;
        
		delete from tblpool where itemkey = v_itemkey;

        if (v_itemstring is null) then
            set v_itemstring = v_itemkey;
        else 
			set v_itemstring = concat(v_itemstring, ',', v_itemkey);
		end if;

    end while;

	-- return final result
    return v_itemstring;

end$$

DELIMITER ;
