DELIMITER $$

drop trigger if exists `testdelete` $$

create trigger `testdelete`
 /*
 Description: remove orphan tests config from other tables
 
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/25/2013		Sai V. 			
*/
   after delete
   on client_testproperties
for each row
begin
 
	delete 
	from client_timelimits 
	where clientname = old.clientname
		and _efk_testid = old.testid; 
 
	delete 
	from client_test_itemtypes 
	where clientname = old.clientname 
		and testid = old.testid; 
 
	delete 
	from client_pilotschools 
	where clientname = old.clientname 
		and _efk_testid = old.testid; 
  
	delete 
	from client_testtool 
	where clientname = old.clientname 
		and `context` = old.testid 
		and contexttype = 'test'; 
  
	delete 
	from client_testtooltype 
	where clientname = old.clientname 
		and `context` = old.testid 
		and contexttype = 'test';
  
	delete 
	from client_testscorefeatures 
	where clientname = old.clientname 
		and testid = old.testid; 
 
	delete 
	from client_fieldtestpriority 
	where clientname = old.clientname 
		and testid = old.testid;
 
	delete 
	from client_segmentproperties 
	where clientname = old.clientname 
		and parenttest = old.testid; 
 
	delete 
	from client_testwindow 
	where clientname = old.clientname 
		and testid = old.testid; 
 
	delete 
	from client_testmode 
	where clientname = old.clientname 
		and testid = old.testid;
 
	delete 
	from client_testformproperties 
	where clientname = old.clientname 
		and testid = old.testid; 
 
	delete 
	from client_testgrades 
	where clientname = old.clientname 
		and testid = old.testid;
 
 end$$

DELIMITER ;
