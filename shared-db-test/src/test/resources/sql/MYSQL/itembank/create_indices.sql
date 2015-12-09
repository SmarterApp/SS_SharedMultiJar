create index `ix_aaitemkey`
	on `aa_itemcl`(`_fk_item`);

create index `ix_adminitemgroup2`
	on `tblsetofadminitems`(`_fk_adminsubject`, `groupid`, `blockid`);

create index `ix_adminitem_ftitemgroup`
	on `tblsetofadminitems`(`_fk_adminsubject`, `isfieldtest`, `groupkey`);

create index `ix_adminstrand_test`
	on `tbladminstrand`(`_fk_adminsubject`);

create index `ix_adminsubjects_testid`
	on `tblsetofadminsubjects`(`testid`);

create index `ix_adminsubjects_virtualtest`
	on `tblsetofadminsubjects`(`virtualtest`);

create index `ix_contentlevelkey`
	on `loader_contentlevels`(`clkey`, `testkey`);

create unique index `ix_formitemposition`
	on `testformitem`(`_fk_testform`, `formposition`);

create unique index `ix_formitemunique`
	on `testformitem`(`_fk_testform`, `_fk_item`);

create index `ix_formitem_test`
	on `testformitem`(`_fk_adminsubject`);

create index `ix_ldritemdim_key`
	on `loader_itemscoredimension`(`_itemkey`);

create index `ix_ldritemprops_key`
	on `loader_itemproperties`(`_itemkey`);

create index `ix_ldritems_key`
	on `loader_items`(`_itemkey`);

create index `ix_ldrstimulus`
	on `loader_stimuli`(`_stimuluskey`);

create index `ix_ldrtests_key`
	on `loader_tests`(`testkey`);

create index `ix_loadererrors`
	on `loader_errors`(`configid`, `test`);

create index `ix_loaderitemdim`
	on `loader_itemscoredimension`(`testname`, `itskey`);

create index `ix_logicaltests`
	on `loader_logicaltests`(`testkey`);

create index `ix_scoredimensionitem`
	on `itemscoredimension`(`_fk_item`);

create index `ix_scoredim_test`
	on `itemscoredimension`(`_fk_adminsubject`);

create index `ix_testgrades_testid`
	on `setoftestgrades`(`testid`);

create index `ix_ldritems_test`
	on `loader_items`(`_testkey`);

create index `ix_tbladminstrand`
	on `tbladminstrand`(`loaderid`);

create unique index `ix_tblitem`
	on `tblitem`(`_efk_itembank`, `_efk_item`);

create index `ix_tblstimulus`
	on `tblstimulus`(`_efk_itembank`, `_efk_itskey`);

create index `ix_tblstrand`
	on `tblstrand`(`name`);

