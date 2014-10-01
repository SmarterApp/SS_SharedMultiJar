drop view if exists tdsconfigs_client_timelimits;

create view tdsconfigs_client_timelimits
/*
Description: to access tables in itembank database mimicking the the concept of synonmys by creating views.

VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/27/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
as 
	select * 
	from ${ConfigsDBName}.client_timelimits;