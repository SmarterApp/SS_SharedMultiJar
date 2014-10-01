alter table `affinitygroup`
	add constraint `fk_affgrouptest`
	foreign key(`_fk_adminsubject`)
	references `tblsetofadminsubjects`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `affinitygroupitem`
	add constraint `fk_itemaffinitygroup`
	foreign key(`_fk_adminsubject`, `groupid`)
	references `affinitygroup`(`_fk_adminsubject`, `groupid`)
	on delete cascade 
	on update cascade ;

alter table `itemmeasurementparameter`
	add constraint `fk_parameterscoredimension`
	foreign key(`_fk_itemscoredimension`)
	references `itemscoredimension`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `itemscoredimension`
	add constraint `fk_itemdim_test`
	foreign key(`_fk_adminsubject`)
	references `tblsetofadminsubjects`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `setoftestgrades`
	add constraint `fk_testgrades_test`
	foreign key(`_fk_adminsubject`)
	references `tblsetofadminsubjects`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `testform`
	add constraint `fk_testform_tblsetofadminsubjects`
	foreign key(`_fk_adminsubject`)
	references `tblsetofadminsubjects`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `testformitem`
	add constraint `fk_formitemform`
	foreign key(`_fk_testform`)
	references `testform`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `tbladminstimulus`
	add constraint `fk_adminstimulus_adminsubject`
	foreign key(`_fk_adminsubject`)
	references `tblsetofadminsubjects`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `tbladminstrand`
	add constraint `fk_adminstrand_adminsubject`
	foreign key(`_fk_adminsubject`)
	references `tblsetofadminsubjects`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `tblitembank`
	add constraint `fk_itembank_client`
	foreign key(`_fk_client`)
	references `tblclient`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `tblitemprops`
	add constraint `fk_tblitemprops_tblitem`
	foreign key(`_fk_item`)
	references `tblitem`(`_key`)
	on delete cascade 
	on update no action ;

alter table `tblsetofadminitems`
	add constraint `fk_tblsetofadminitems_tblsetofadminsubjects`
	foreign key(`_fk_adminsubject`)
	references `tblsetofadminsubjects`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `tblsetofadminitems`
	add constraint `fk_tblsetofadminitems_tblitem`
	foreign key(`_fk_item`)
	references `tblitem`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `tblsetofadminsubjects`
	add constraint `fk_tblsetofadminsubjects_tblsetofadminsubjects`
	foreign key(`_fk_testadmin`)
	references `tbltestadmin`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `tblsetofitemstimuli`
	add constraint `fk_tblsetofitemstimuli_tblstimulus`
	foreign key(`_fk_stimulus`)
	references `tblstimulus`(`_key`)
	on delete cascade 
	on update no action ;

alter table `tblsetofitemstimuli`
	add constraint `fk_tblsetofitemstimuli_tblitem`
	foreign key(`_fk_item`)
	references `tblitem`(`_key`)
	on delete cascade 
	on update no action ;

alter table `tblsetofitemstrands`
	add constraint `fk_tblsetofitemstrands_tblitem`
	foreign key(`_fk_item`)
	references `tblitem`(`_key`)
	on delete cascade 
	on update no action ;

alter table `tblstrand`
	add constraint `fk_tblstrand_tblstrand`
	foreign key(`_fk_subject`)
	references `tblsubject`(`_key`)
	on delete cascade 
	on update no action ;

alter table `tblsubject`
	add constraint `fk_subject_client`
	foreign key(`_fk_client`)
	references `tblclient`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `tbltestadmin`
	add constraint `fk_testadmin_client`
	foreign key(`_fk_client`)
	references `tblclient`(`_key`)
	on delete cascade 
	on update cascade ;


