DELIMITER $$

drop procedure if exists `ib_listtests` $$

create procedure `ib_listtests`(
/*
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/1/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
	v_clientname varchar(100)
  , v_sessiontype int)
begin

	if v_sessiontype is null then set v_sessiontype = 0; end if;

	select distinct p.testid, p.gradetext as gradecode, p.subjectname as `subject`, l.`code` as languagecode
		, l.`value` as `language`, selectionalgorithm, p.label as displayname, p.sortorder, p.accommodationfamily, p.isselectable
        , scorebytds(v_clientname, p.testid) as scorebytds
		, validatecompleteness(s._key) as validatecompleteness
		, maxopportunities, minitems, maxitems, prefetch, startability
        , case when w.startdate is null then now() else date_add(w.startdate, interval shiftwindowstart day) end as windowstart
        , case when w.enddate is null then now() else date_add(w.enddate, interval shiftwindowend day) end as windowend
        , case when ftstartdate is null then now() else date_add(ftstartdate, interval shiftftstart day) end as ftstartdate
        , case when ftenddate is null then now() else date_add(coalesce(ftenddate, now()), interval shiftftend day) end as ftenddate
        , ftminitems, ftmaxitems, m.issegmented, m.testkey as _key
		, case when requirertsform = 1 or requirertsformwindow = 1 then 'predetermined' else 'algorithmic' end as formselection
    from ${ConfigsDBName}.client_testproperties p, ${ConfigsDBName}.client_testmode m
		, ${ConfigsDBName}.client_testwindow w, ${ConfigsDBName}.tblsetofadminsubjects s
		, ${ConfigsDBName}.client_testtool l, _externs e
	where w.clientname = v_clientname and (w.sessiontype = -1 or w.sessiontype = v_sessiontype) 
		and e.clientname = v_clientname
        and now() between case when w.startdate is null then now() else date_add(w.startdate, interval shiftwindowstart day) end
        and case when w.enddate is null then now() else date_add(w.enddate, interval shiftwindowend day) end
        and p.clientname = v_clientname and p.testid = w.testid
        and m.clientname = v_clientname and m.testid = p.testid and (m.sessiontype = -1 or m.sessiontype = v_sessiontype)
        and m.testkey = s._key and l.clientname = v_clientname 
		and l.`type` = 'language' and l.contexttype = 'test' and l.`context` = p.testid	
    order by p.sortorder;

end$$

DELIMITER ;
