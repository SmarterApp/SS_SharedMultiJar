alter table `admineventitems`
	drop foreign key `fk_admineventitems`
	 ;

alter table `admineventopportunities`
	drop foreign key `fk_event_testopp`
	 ;

alter table `admineventopportunities`
	drop foreign key `fk_admineventopp`
	;

alter table `clientlatency`
	drop foreign key `fk_clientlatency`
	;

alter table `clientlatencyarchive`
	drop foreign key `fk_clientlatencyarchive`
	 ;

alter table `ft_opportunityitem`
	drop foreign key `fk_ftoppitem_oppkey`
	 ;

alter table `qc_validationexception`
	drop foreign key `fk_validation`
	 ;

alter table `simp_itemgroup`
	drop foreign key `fk_simpsegmentitemgroup`
	 ;

alter table `simp_segment`
	drop foreign key `fk_simpsegmenttest`
	;

alter table `simp_segment`
	drop foreign key `fk_simpsegementtest`
	;

alter table `simp_segmentcontentlevel`
	drop foreign key `fk_simpsegmentcl`
	;

alter table `simp_segmentitem`
	drop foreign key `fk_simpsegmentitem`
	 ;

alter table `simp_session`
	drop foreign key `fk_session`
	 ;

alter table `simp_sessiontests`
	drop foreign key `fk_simpsessiontests`
	 ;

alter table `sim_itemgroup`
	drop foreign key `fk_segmentitemgroup`
	;

alter table `sim_segment`
	drop foreign key `fk_simsegmenttest`
	 ;

alter table `sim_segmentcontentlevel`
	drop foreign key `fk_simsegmentcl`
	 ;

alter table `sim_segmentitem`
	drop foreign key `fk_simsegmentitem`
	 ;

alter table `sim_userclient`
	drop foreign key `fk_simclientuser`
	 ;

alter table `sirve_audit`
	drop foreign key `fk_sirveaudit_session`
	 ;

alter table `sessiontests`
	drop foreign key `fk_sessiontests`
	;

alter table `testopportunitycontentcounts`
	drop foreign key `fk_testoppcontentcnts`
	 ;

alter table `testopportunityscores`
	drop foreign key `fk_testoppscores`
	;

alter table `testopportunitysegment`
	drop foreign key `fk_testoppsegment`
	;

alter table `testopportunitysegmentcounts`
	drop foreign key `fk_testoppsegmentcnts`
	;

alter table `testeeaccommodations`
	drop foreign key `fk_testeeaccommodations`
	 ;

alter table `testeeattribute`
	drop foreign key `fk_testeeatt_opp`
	 ;

alter table `testeecomment`
	drop foreign key `fk_commenttestopp`
	 ;

alter table `testeerelationship`
	drop foreign key `fk_testeerel_opp`
	;

alter table `testeeresponse`
	drop foreign key `fk_testoppresponse`
	 ;

alter table `testeeresponsearchive`
	drop foreign key `fk_testoppresponsearchive`
	;

alter table `testeeresponseaudit`
	drop foreign key `fk_responseaudit_testop`
	;

alter table `testeeresponsescore`
	drop foreign key `fk_responsescore_opp`
	 ;

alter table `testoppabilityestimate`
	drop foreign key `fk_abilityest_testopp`
	 ;

alter table `testopprequest`
	drop foreign key `fk_testopprequest`
	 ;

alter table `testopptoolsused`
	drop foreign key `fk_opportunitytool`
	 ;

