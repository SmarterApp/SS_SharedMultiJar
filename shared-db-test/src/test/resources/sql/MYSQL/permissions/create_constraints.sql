alter table `role_entity` 
 add CONSTRAINT `roleentity_fk_rid` 
 FOREIGN KEY (`_fk_rid`) REFERENCES `role` (`_id`) ON DELETE CASCADE;
 
 alter table `role_entity` 
 add CONSTRAINT `roleentity_fk_etuk` 
 FOREIGN KEY (`_fk_etuk`) REFERENCES `entitytype` (`unique_key`) ON DELETE CASCADE;
 
 alter table `component` 
 add constraint `component_fk_pid` 
 foreign key (`_fk_pid`) references `permission` (`_id`) on delete cascade;
 
 alter table  `permission_role`
 add constraint `permissionrole_fk_rid` 
 foreign key (`_fk_rid`) references `role` (`_id`) on delete cascade;
 
 alter table  `permission_role`
 add constraint `permissionrole_fk_pid` 
 foreign key (`_fk_pid`) references `permission` (`_id`) on delete cascade;
 
 alter table  `permission_role`
 add constraint `permissionrole_fk_cid` 
 foreign key (`_fk_cid`) references `component` (`_id`) on delete cascade;