drop view if exists statuscodes;

create view statuscodes
/*
VERSION 	DATE 			AUTHOR 			COMMENTS
001			11/1/2013		Sai V. 			Converted code from SQL Server to MySQL
*/ 
as 
select `usage`, `status`, description, stage
from ${ConfigsDBName}.statuscodes;