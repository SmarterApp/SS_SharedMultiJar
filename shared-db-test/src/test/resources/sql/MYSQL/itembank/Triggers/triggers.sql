DELIMITER $$

drop trigger if exists `clientdelete` $$

create trigger `clientdelete`
 /*
 Description: set up an internal 'replication subscriber' so that proctor reads and participation reports
			can be isolated from the student testing performance requirements
 
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/25/2013		Sai V. 			
*/
   after delete
   on tblclient
 for each row
 begin
 
    update configsloaded 
	set _date = now(3)
	  , _error = 'client deleted' 
    where clientname = old.`name` 
		and _error is null;

 end$$

DELIMITER ;