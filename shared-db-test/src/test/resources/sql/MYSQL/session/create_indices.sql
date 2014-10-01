create index `ix_abilityestimate`
	on `testoppabilityestimate`(`_fk_testopportunity`, `itempos`, `strand`);

create index `ix_accomoppcode`
	on `testeeaccommodations`(`_fk_testopportunity`, `acccode`, `acctype`);

create index `ix_admineventitem`
	on `admineventitems`(`_fk_adminevent`, `_efk_itsbank`, `_efk_itsitem`);

create index `ix_admineventopp_oppkey`
	on `admineventopportunities`(`_fk_testopportunity`);

create index `ix_clsuser`
	on `tblclsclientsessionstatus`(`username`);

create index `ix_clientalertmsgs`
	on `alertmessages`(`clientname`);

create index `ix_clientlatency_sitekey`
	on `clientlatencyarchive`(`sitekey`);

create index `ix_clientopplatency`
	on `clientlatency`(`_fk_testopportunity`, `page`);

create index `ix_clientopplatencyarchive`
	on `clientlatencyarchive`(`_fk_testopportunity`, `page`);

create index `ix_clientreportid_oppkey`
	on `client_reportingid`(`_fk_testopportunity`);

create unique index `ix_clientreportingid`
	on `testopportunity`(`clientname`, `reportingid`);

create index `ix_clienttestid`
	on `testopportunity`(`_efk_testid`, `clientname`, `status`);

create index `ix_datecompleted`
	on `testopportunity_readonly`(`datecompleted`, `_efk_testid`);

create index `ix_datereported`
	on `testopportunity_readonly`(`datereported`, `_efk_testid`);

create index `ix_datestarted`
	on `testopportunity_readonly`(`datestarted`, `_efk_testid`);

create index `ix_date_alertmessages`
	on `alertmessages`(`dateended`, `datecancelled`);

create index `ix_ftitem_cluster`
	on `ft_opportunityitem`(`_fk_testopportunity`, `groupid`);

create index `ix_ftoppitem_primary`
	on `ft_opportunityitem`(`_fk_testopportunity`, `segment`, `language`, `groupkey`);

create index `ix_ftsamples`
	on `ft_groupsamples`(`_efk_adminsubject`, `groupkey`);

create index `ix_ftsamples_parenttest`
	on `ft_groupsamples`(`_efk_parenttest`);

create index `ix_ft_oppitem_agg1`
	on `ft_opportunityitem`(`_efk_fieldtest`, `language`, `deleted`, `groupkey`)
	-- include (`_fk_testopportunity`)
;

create index `ix_geolatencyreportdistrict`
	on `r_geolatencyreport`(`districtid`, `_date`);

create index `ix_geolatencyreportschool`
	on `r_geolatencyreport`(`schoolid`, `_date`);

create index `ix_historyoppkey`
	on `testeehistory`(`_fk_testopportunity`);

create index `ix_historytesteeid`
	on `testeehistory`(`testeeid`);

create index `ix_hourlygeolatencydistrict`
	on `r_hourlygeolatencytable`(`districtid`, `_date`);

create index `ix_hourlygeolatencyschool`
	on `r_hourlygeolatencytable`(`schoolid`, `_date`);

create index `ix_itemhistoryoppgroup`
	on `testeeitemhistory`(`_fk_testopportunity`, `groupid`);

create index `ix_joined`
	on `testopportunity`(`datejoined`);

create index `ix_joined2`
	on `testopportunity_readonly`(`datejoined`);

create index `ix_maxtestopps`
	on `_maxtestopps`(`_time`, `numopps`);

create index `ix_maxoppsclient`
	on `_maxtestopps`(`clientname`);

create index `ix_missingmessages`
	on `_missingmessages`(`application`, `context`, `contexttype`, `appkey`);

create index `ix_oppcontentcounts_testid`
	on `testopportunitycontentcounts`(`_efk_testid`, `contentlevel`);

create index `ix_oppdatestatus`
	on `testopportunity`(`datechanged`, `status`);

create index `ix_oppdatestatus`
	on `testopportunity_readonly`(`datechanged`, `status`);

create index `ix_participationcount`
	on `r_participationcountstable`(`_date`, `districtid`, `schoolid`, `testid`, `opportunity`);

create index `ix_participationreport`
	on `r_schoolparticipationreport`(`schoolrtskey`, `grade`, `_efk_testid`, `status`, `datecompleted`);

create index `ix_proctor_messages`
	on `setofproctoralertmessages`(`_efk_proctor`, `_fk_alertmessages`);

create index `ix_qcexcept_oppkey`
	on `qc_validationexception`(`_fk_testopportunity`);

create index `ix_rtestcounts`
	on `r_testcounts`(`_date`, `_efk_testid`, `status`);

create index `ix_r_proctorpackage_proctorkey`
	on `r_proctorpackage`(`proctorkey`);

create unique index `ix_studentid_clientname`
	on `r_studentkeyid`(`studentid`, `clientname`);
	
create index `ix_r_studentpackage_studentkey`
	on `r_studentpackage`(`studentkey`);

create index `ix_reportabnormaldate`
	on `r_abnormallogins`(`_date`, `clientname`);

create index `ix_reportabnormaldistrict`
	on `r_abnormallogins`(`districtid`, `_date`, `clientname`);

create index `ix_reportabnormalschool`
	on `r_abnormallogins`(`schoolid`, `_date`, `clientname`);

create index `ix_responseitem`
	on `testeeresponse`(`_fk_testopportunity`, `_efk_itemkey`);

create index `ix_responsepage`
	on `testeeresponse`(`_fk_testopportunity`, `page`, `segment`);

create index `ix_responsescore`
	on `testeeresponsescore`(`scorestatus`, `scoringdate`)
	-- include (`_fk_testopportunity`, `position`, `responsesequence`, `scoreattempts`, `scoremark`)
;

create index `ix_sbmessages`
	on `_sb_messages`(`conversationhandle`);

create index `ix_sbmsgsarchivedate`
	on `_sb_messagesarchive`(`_datearchived`);

create index `ix_schoolkey`
	on `rtsschoolgrades`(`schoolkey`);

create unique index `ix_sessionid`
	on `session`(`sessionid`, `clientname`);

create index `ix_sessionproctor`
	on `session`(`_efk_proctor`, `clientname`);

create index `ix_syncresponseitem`
	on `testeeresponse`(`_fk_testopportunity`, `_efk_itemkey`, `geosyncid`, `position`);

create index `ix_testeeid`
	on `testopportunity_readonly`(`testeeid`);

create index `ix_testoppcontentlevel`
	on `testopportunitycontentcounts`(`contentlevel`);

create index `ix_testopportunity2`
	on `testopportunity`(`reportingid`)
	-- include (`_key`)
;

create index `ix_testopportunity2`
	on `testopportunity_readonly`(`reportingid`);

create index `ix_testeeatt_idval`
	on `testeeattribute`(`tds_id`, `attributevalue`);

create index `ix_testeeatt_idval2`
	on `testeeattribute`(`tds_id`, `context`, `attributevalue`);

create index `ix_testeeattribute_oppkey`
	on `testeeattribute`(`_fk_testopportunity`, `tds_id`);

create index `ix_testeecomment_oppkey`
	on `testeecomment`(`_fk_testopportunity`);

create index `ix_testeehistory`
	on `testeehistory`(`_efk_testee`, `clientname`, `subject`)
	-- include (`initialability`, `itemgroupstring`)
;

create index `ix_testeeitemhistory`
	on `testeeitemhistory`(`clientname`, `_efk_testee`, `_fk_testopportunity`, `groupid`);

create index `ix_testeerelationship_oppkey`
	on `testeerelationship`(`_fk_testopportunity`, `tds_id`);

create index `ix_testeeresponseaudit`
	on `testeeresponseaudit`(`_fk_testopportunity`, `position`, `sequence`);

create index `ix_testeesession2`
	on `testopportunity`(`_fk_session`, `datechanged`);

create index `ix_testeesession2`
	on `testopportunity_readonly`(`_fk_session`, `datechanged`);

create unique index `ix_testoppkey`
	on `testopportunity`(`_efk_testee`, `_efk_testid`, `opportunity`, `clientname`, `_version`)
	-- include (`_key`)
;

create index `ix_testopprequest`
	on `testopprequest`(`_fk_session`, `_fk_testopportunity`);

create index `ix_toolopp`
	on `testopptoolsused`(`_fk_testopportunity`, `itempage`, `tooltype`);

create index `itemdist_testitem`
	on `itemdistribution`(`_efk_adminsubject`, `_efk_item`, `_fk_session`);
