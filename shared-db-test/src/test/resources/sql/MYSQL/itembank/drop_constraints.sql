
alter table `affinitygroup`
	drop foreign key `fk_affgrouptest`
	 ;	 

alter table `affinitygroupitem`
	drop foreign key `fk_itemaffinitygroup`
 ;

alter table `itemmeasurementparameter`
	drop foreign key `fk_parameterscoredimension`
 ;

alter table `itemscoredimension`
	drop foreign key `fk_itemdim_test`
 ;

alter table `setoftestgrades`
	drop foreign key `fk_testgrades_test`
 ;

alter table `testform`
	drop foreign key `fk_testform_tblsetofadminsubjects`
;

alter table `testformitem`
	drop foreign key `fk_formitemform`
 ;

alter table `tbladminstimulus`
	drop foreign key `fk_adminstimulus_adminsubject`
 ;

alter table `tbladminstrand`
	drop foreign key `fk_adminstrand_adminsubject`
;

alter table `tblitembank`
	drop foreign key `fk_itembank_client`
 ;

alter table `tblitemprops`
	drop foreign key `fk_tblitemprops_tblitem`
 ;

alter table `tblsetofadminitems`
	drop foreign key `fk_tblsetofadminitems_tblsetofadminsubjects`
;

alter table `tblsetofadminitems`
	drop foreign key `fk_tblsetofadminitems_tblitem`
 ;

alter table `tblsetofadminsubjects`
	drop foreign key `fk_tblsetofadminsubjects_tblsetofadminsubjects`
;

alter table `tblsetofitemstimuli`
	drop foreign key `fk_tblsetofitemstimuli_tblstimulus`
;

alter table `tblsetofitemstimuli`
	drop foreign key `fk_tblsetofitemstimuli_tblitem`
 ;

alter table `tblsetofitemstrands`
	drop foreign key `fk_tblsetofitemstrands_tblitem`
;

alter table `tblstrand`
	drop foreign key `fk_tblstrand_tblstrand`
 ;

alter table `tblsubject`
	drop foreign key `fk_subject_client`
;

alter table `tbltestadmin`
	drop foreign key `fk_testadmin_client`
 ;

