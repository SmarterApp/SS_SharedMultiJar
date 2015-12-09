drop view if exists tdsconfigs_statuscodes;

create view tdsconfigs_statuscodes 
/*
Description: to access tables in itembank database mimicking the the concept of synonmys by creating views.

VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/27/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
as 
	select * 
	from ${ConfigsDBName}.statuscodes;