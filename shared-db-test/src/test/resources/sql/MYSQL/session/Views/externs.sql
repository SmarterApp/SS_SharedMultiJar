drop view if exists externs;

create view externs 
/*
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/1/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
as 
select distinct x.clientname
	 , testeedb
	 , proctordb
	 , '${SessionDBName}' as sessiondb
	 -- , db_name() as sessiondb
	 , testeetype
	 , proctortype
	 , clientstylepath
	 , x.environment
	 , ispracticetest
	 , (select url
		from ${ConfigsDBName}.geo_clientapplication a, _externs e
		where e.clientname = x.clientname and a.clientname = x.clientname 
			and a.environment = e.environment 
			and a.servicetype = 'CHECKIN' and a.appname = 'STUDENT') as testeecheckin
	-- , checkinurl(x.clientname, 'STUDENT') as testeecheckin
	 , (select url
		from ${ConfigsDBName}.geo_clientapplication a, _externs e
		where e.clientname = x.clientname and a.clientname = x.clientname 
			and a.environment = e.environment 
			and a.servicetype = 'CHECKIN' and a.appname = 'PROCTOR') as proctorcheckin
	 -- , checkinurl(x.clientname, 'PROCTOR') as proctorcheckin
	 , timezoneoffset
	 , publishurl
	 , initialreportingid
	 , initialsessionid
	 , s.dbname as testdb
	 , qabrokerguid
from ${ConfigsDBName}.client_externs e, _externs x, _synonyms s
where e.clientname = x.clientname 
	and e.environment = x.environment 
	and s.prefix = 'ITEMBANK_';