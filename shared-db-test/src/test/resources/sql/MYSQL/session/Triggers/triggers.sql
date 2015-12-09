DELIMITER $$

drop trigger if exists `updatemachinescore` $$

create trigger `updatemachinescore`
 /*
 Description: this trigger will populate the table testeeresponsescore with a reference to any item that is waiting for machine score.
			-- it works in tandem with s_getscoreitems and s_updateitemscore.
			-- the trigger is the most reliable method to ensure that item score status that are modified by any means are picked up.
 
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/25/2013		Sai V. 			
*/
   after update
   on testeeresponse
 for each row
 begin
 
    update testeeresponsescore 
	set scorestatus 	= new.scorestatus
	  , scoringdate 	= new.scoringdate
	  , scoreddate 		= null
	  , scoremark 		= new.scoremark
	  , scoreattempts 	= new.scoreattempts
	  , responsesequence = new.responsesequence
	where new.scorestatus = 'waitingformachinescore' 
		and _fk_testopportunity = new._fk_testopportunity
		and `position` = new.`position`;

	if (not exists (select * from testeeresponsescore 
					where _fk_testopportunity = new._fk_testopportunity and `position` = new.`position` ) and new.scorestatus = 'waitingformachinescore') 
	then
		insert into testeeresponsescore (_fk_testopportunity, `position`, responsesequence, scorestatus, scoringdate, scoreddate, scoremark, scoreattempts)
		values (new._fk_testopportunity, new.`position`, new.responsesequence, new.scorestatus, new.scoringdate, new.scoreddate, new.scoremark, new.scoreattempts);
	end if;

 end$$

DELIMITER ;

DELIMITER $$

drop trigger if exists `testoppdelete` $$

create trigger `testoppdelete`
 /*
 Description: set up an internal 'replication subscriber' so that proctor reads and participation reports
			-- can be isolated from the student testing performance requirements
 
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/25/2013		Sai V. 			
*/
   after delete
   on testopportunity
for each row
begin
 
    delete 
    from testopportunity_readonly
	where _fk_testopportunity = old._key;

 end$$

DELIMITER ;


DELIMITER $$

drop trigger if exists `testoppupdate` $$

create trigger `testoppupdate`
 /*
 Description: set up an internal 'replication subscriber' so that proctor reads and participation reports
			-- can be isolated from the student testing performance requirements
 
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/25/2013		Sai V. 			
*/
   after update
   on testopportunity
for each row
begin

    --  remove 'deleted' opportunities from the readonly table ??
    delete 
    from testopportunity_readonly
    where _fk_testopportunity = new._key 
        and new.datedeleted is not null;

	if (not exists (select * from testopportunity_readonly 
					where _fk_testopportunity = new._key) and new.datedeleted is null)  
	then
		insert into testopportunity_readonly (_fk_testopportunity) 
		values (new._key);
	end if;

    update testopportunity_readonly
	set _efk_testee     = new._efk_testee
	  , _efk_testid     = new._efk_testid
	  , opportunity     = new.opportunity
	  , _fk_session     = new._fk_session
	  , _fk_browser     = new._fk_browser
	  , testeeid        = new.testeeid
	  , testeename      = new.testeename
	  , `status`		= new.`status`
	  , restart			= new.restart
	  , graceperiodrestarts		= new.graceperiodrestarts
	  , datechanged		= new.datechanged
	  , datejoined		= new.datejoined
	  , datestarted		= new.datestarted
	  , daterestarted	= new.daterestarted
	  , datecompleted	= new.datecompleted
	  , datescored		= new.datescored
	  , dateapproved	= new.dateapproved
	  , dateexpired		= new.dateexpired
	  , datesubmitted	= new.datesubmitted
	  , datereported	= new.datereported
	  , abnormalstarts	= new.abnormalstarts
	  , reportingid		= new.reportingid
	  , `subject`       = new.`subject`
	  , maxitems		= new.maxitems
	  , numitems		= new.numitems
	  , numresponses    = new.numresponses
	  , dateinvalidated	= new.dateinvalidated
	  , invalidatedby	= new.invalidatedby
	  , daterescored	= new.daterescored
	  , datepaused      = new.datepaused
	  , _efk_adminsubject   	= new._efk_adminsubject
	  , expirefrom      = new.expirefrom
	  , sessid          = new.sessid
	  , proctorname     = new.proctorname
	  , `language`      = new.`language`
	  , clientname      = new.clientname
	  , _version        = new._version
	  , windowid        = new.windowid
	  , `mode`          = new.`mode`
	  , customaccommodations 	= new.customaccommodations
	  , insegment           	= new.insegment
	  , waitingforsegment   	= new.waitingforsegment
	  , environment        	 	= new.environment
	where _fk_testopportunity = new._key;


end$$

DELIMITER ;

