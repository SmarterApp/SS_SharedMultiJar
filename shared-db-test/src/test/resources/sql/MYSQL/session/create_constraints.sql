alter table `admineventitems`
	add constraint `fk_admineventitems`
	foreign key(`_fk_adminevent`)
	references `adminevent`(`_key`)
	on delete cascade 
	on update no action ;

alter table `admineventopportunities`
	add constraint `fk_event_testopp`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `admineventopportunities`
	add constraint `fk_admineventopp`
	foreign key(`_fk_adminevent`)
	references `adminevent`(`_key`)
	on delete cascade 
	on update cascade ;

alter table `clientlatency`
	add constraint `fk_clientlatency`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `clientlatencyarchive`
	add constraint `fk_clientlatencyarchive`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `ft_opportunityitem`
	add constraint `fk_ftoppitem_oppkey`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `qc_validationexception`
	add constraint `fk_validation`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete no action 
	on update no action ;

alter table `simp_itemgroup`
	add constraint `fk_simpsegmentitemgroup`
	foreign key(`_fk_session`, `_efk_segment`)
	references `simp_segment`(`_fk_session`, `_efk_segment`)
	on delete cascade 
	on update no action ;

alter table `simp_segment`
	add constraint `fk_simpsegmenttest`
	foreign key(`_fk_session`, `_efk_adminsubject`)
	references `sessiontests`(`_fk_session`, `_efk_adminsubject`)
	on delete cascade 
	on update no action ;

alter table `simp_segment`
	add constraint `fk_simpsegementtest`
	foreign key(`_fk_session`, `_efk_adminsubject`)
	references `simp_sessiontests`(`_fk_session`, `_efk_adminsubject`)
	on delete cascade 
	on update no action ;

alter table `simp_segmentcontentlevel`
	add constraint `fk_simpsegmentcl`
	foreign key(`_fk_session`, `_efk_segment`)
	references `simp_segment`(`_fk_session`, `_efk_segment`)
	on delete cascade 
	on update no action ;

alter table `simp_segmentitem`
	add constraint `fk_simpsegmentitem`
	foreign key(`_fk_session`, `_efk_segment`)
	references `simp_segment`(`_fk_session`, `_efk_segment`)
	on delete cascade 
	on update no action ;

alter table `simp_session`
	add constraint `fk_session`
	foreign key(`_key`)
	references `session`(`_key`)
	on delete no action 
	on update no action ;

alter table `simp_sessiontests`
	add constraint `fk_simpsessiontests`
	foreign key(`_fk_session`)
	references `simp_session`(`_key`)
	on delete cascade 
	on update no action ;

alter table `sim_itemgroup`
	add constraint `fk_segmentitemgroup`
	foreign key(`_fk_session`, `_efk_segment`)
	references `sim_segment`(`_fk_session`, `_efk_segment`)
	on delete cascade 
	on update no action ;

alter table `sim_segment`
	add constraint `fk_simsegmenttest`
	foreign key(`_fk_session`, `_efk_adminsubject`)
	references `sessiontests`(`_fk_session`, `_efk_adminsubject`)
	on delete cascade 
	on update no action ;

alter table `sim_segmentcontentlevel`
	add constraint `fk_simsegmentcl`
	foreign key(`_fk_session`, `_efk_segment`)
	references `sim_segment`(`_fk_session`, `_efk_segment`)
	on delete cascade 
	on update no action ;

alter table `sim_segmentitem`
	add constraint `fk_simsegmentitem`
	foreign key(`_fk_session`, `_efk_segment`)
	references `sim_segment`(`_fk_session`, `_efk_segment`)
	on delete cascade 
	on update no action ;

alter table `sim_userclient`
	add constraint `fk_simclientuser`
	foreign key(`_fk_simuser`)
	references `sim_user`(`userid`)
	on delete cascade 
	on update cascade ;

alter table `sirve_audit`
	add constraint `fk_sirveaudit_session`
	foreign key(`_fk_sirvesession`)
	references `sirve_session`(`_key`)
	on delete cascade 
	on update no action ;

alter table `sessiontests`
	add constraint `fk_sessiontests`
	foreign key(`_fk_session`)
	references `session`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testopportunitycontentcounts`
	add constraint `fk_testoppcontentcnts`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testopportunityscores`
	add constraint `fk_testoppscores`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testopportunitysegment`
	add constraint `fk_testoppsegment`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testopportunitysegmentcounts`
	add constraint `fk_testoppsegmentcnts`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testeeaccommodations`
	add constraint `fk_testeeaccommodations`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testeeattribute`
	add constraint `fk_testeeatt_opp`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testeecomment`
	add constraint `fk_commenttestopp`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testeerelationship`
	add constraint `fk_testeerel_opp`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testeeresponse`
	add constraint `fk_testoppresponse`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete no action 
	on update no action ;

alter table `testeeresponsearchive`
	add constraint `fk_testoppresponsearchive`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete no action 
	on update no action ;

alter table `testeeresponseaudit`
	add constraint `fk_responseaudit_testop`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testeeresponsescore`
	add constraint `fk_responsescore_opp`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testoppabilityestimate`
	add constraint `fk_abilityest_testopp`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testopprequest`
	add constraint `fk_testopprequest`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

alter table `testopptoolsused`
	add constraint `fk_opportunitytool`
	foreign key(`_fk_testopportunity`)
	references `testopportunity`(`_key`)
	on delete cascade 
	on update no action ;

