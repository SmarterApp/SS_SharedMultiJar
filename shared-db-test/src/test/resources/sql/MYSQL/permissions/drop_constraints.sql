alter table `role_entity` 
 drop foreign key `roleentity_fk_rid`; 
 
 alter table `role_entity` 
 drop foreign key `roleentity_fk_etuk`; 
 
 alter table `component` 
 drop foreign key `component_fk_pid` ;

 alter table  `permission_role`
 drop foreign key  `permissionrole_fk_rid` ;
 
 alter table  `permission_role`
 drop foreign key `permissionrole_fk_pid`;
 
 alter table  `permission_role`
 drop foreign key `permissionrole_fk_cid` ;
 