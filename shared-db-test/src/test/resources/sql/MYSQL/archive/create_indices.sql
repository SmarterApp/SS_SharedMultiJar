create index `ix_auditaccommodations`
	on `auditaccommodations`(`dbname`, `_fk_testopportunity`);

create index `ix_opportunityaudit`
	on `opportunityaudit`(`dbname`, `_fk_testopportunity`);

create index `ix_opportunityclient`
	on `opportunityclient`(`dbname`, `_fk_testopportunity`);

create index `ix_serverlatency1`
	on `serverlatency`(`_fk_testopportunity`, `_date`);

create index `ix_serverlatencyarchive`
	on `serverlatencyarchive`(`operation`, `client`);

create index `ix_sessionaudit`
	on `sessionaudit`(`dbname`, `_fk_session`);

create index `ix_systemclient`
	on `systemclient`(`dbname`, `clientname`, `application`);

create index `ix_errors_db`
	on `systemerrors`(`dbname`, `daterecorded`);

create index `ix_sessionerrors`
	on `systemerrors`(`_fk_session`);

create index `ix_syserrsoppkey`
	on `systemerrors`(`_fk_testopportunity`);

create index `ix_systemerrors_2`
	on `systemerrors`(`application`, `procname`);

create index `ix_errorsarchiveclient`
	on `systemerrorsarchive`(`client`, `application`);

create index `ix_systemerrorsarchive`
	on `systemerrorsarchive`(`procname`, `daterecorded` desc);

create index `ix_oppscoresaudit`
	on `testopportunityscores_audit`(`dbname`, `_fk_testopportunity`);

create index `ix_dblatency`
	on `_dblatency`(`procname`);

create index `ix_dblatency_db`
	on `_dblatency`(`dbname`, `starttime`);

create index `ix_dblatencyarchive`
	on `_dblatencyarchive`(`procname`, `client`);

create index `ix_dblatencyarchivesource`
	on `_dblatencyarchive`(`dbname`);

create index `ix_dblatencyarchivedate`
	on `_dblatencyarchive`(`starttime`);

create index `ix_dblatencyreports`
	on `_dblatencyreports`(`procname`, `client`);

