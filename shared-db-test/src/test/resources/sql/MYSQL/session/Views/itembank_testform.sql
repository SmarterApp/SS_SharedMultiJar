drop view if exists itembank_testform;

create view itembank_testform 
/*
Description: to access tables in itembank database mimicking the the concept of synonmys by creating views.

VERSION 	DATE 			AUTHOR 			COMMENTS
001			1/2/2014		Sai V. 			Converted code from SQL Server to MySQL
*/
as 
	select * 
	from ${ItembankDBName}.testform;