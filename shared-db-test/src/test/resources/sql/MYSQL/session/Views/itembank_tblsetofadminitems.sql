drop view if exists itembank_tblsetofadminitems;

create view itembank_tblsetofadminitems 
/*
Description: to access tables in itembank database mimicking the the concept of synonmys by creating views.

VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/27/2013		Sai V. 			Converted code from SQL Server to MySQL
*/
as 
	select * 
	from ${ItembankDBName}.tblsetofadminitems;