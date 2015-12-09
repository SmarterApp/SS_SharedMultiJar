alter table `tds_fieldtestpriority`
	drop  foreign key `fk_tds_testeeatt`;

alter table `tds_coremessageuser`
	drop  foreign key `fk_msguser_msgobj`;

alter table `system_applicationsettings`
	drop  foreign key `tds_applicationsettings_client_applicationsettings`;

alter table `geo_clientapplication`
	drop  foreign key `fk_geoapp_db`;

alter table `client_testwindow`
	drop  foreign key `fk_timewindow`;

alter table `client_testtool`
	drop  foreign key `fk_clienttool_tooltype`;

alter table `client_tds_rtsattributevalues`
	drop  foreign key `fk_client_tds_rtsattributevalues_client_tds_rtsattribute`;

alter table `client_messagetranslation`
	drop  foreign key `fk_clientmsgtranslation`;

alter table `client_fieldtestpriority`
	drop  foreign key `fk_ft_testeeatt`;