drop view if exists tdsconfigs_geo_clientapplication;

create view tdsconfigs_geo_clientapplication 
/*
Description: to access tables in itembank database mimicking the the concept of synonmys by creating views.

VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/27/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
as 
	select * 
	from ${ConfigsDBName}.geo_clientapplication;