drop view if exists timelimits;

create view timelimits
/*
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/1/2013		Sai V. 			Converted code from SQL Server to MySQL
*/ 
as
select l._efk_testid
	, l.oppexpire
	, l.opprestart
	, l.oppdelay
	, l.interfacetimeout
	, l.requestinterfacetimeout
	, e.clientname
	, e.environment
	, l.ispracticetest
	, l.refreshvalue
	, l.tainterfacetimeout
	, l.tacheckintime
	, l.datechanged
	, l.datepublished
	,  l.sessionexpire
	, l.refreshvaluemultiplier
from ${ConfigsDBName}.client_timelimits l, _externs e
where e.clientname = l.clientname;

