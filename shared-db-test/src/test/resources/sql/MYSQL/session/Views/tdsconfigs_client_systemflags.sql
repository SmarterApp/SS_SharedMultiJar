drop view if exists tdsconfigs_client_systemflags;

create view tdsconfigs_client_systemflags 
/*
Description: to access tables in itembank database mimicking the the concept of synonmys by creating views.

VERSION 	DATE 			AUTHOR 			COMMENTS
001			1/2/2014		Sai V. 			Converted code from SQL Server to MySQL
*/
as 
	select * 
	from ${ConfigsDBName}.client_systemflags;