alter table `loader_initialize`
  add constraint `fk_sessionkey_linitialize` 
  FOREIGN KEY (`_fk_sessionkey`)
  REFERENCES `session` (`_key`)
  ON DELETE CASCADE;
  
 alter table `loader_benchmarkgrades` 
  add CONSTRAINT `fk_sessionkey_lbenchmarkgrades` 
  FOREIGN KEY (`_fk_sessionkey`)
  REFERENCES `session` (`_key`)
  ON DELETE CASCADE;
        
alter table `loader_errors`
	add CONSTRAINT `fk_sessionkey_lerrors` 
    FOREIGN KEY (`_fk_sessionkey`)
    REFERENCES `session` (`_key`)
    ON DELETE CASCADE;
        
alter table `loader_categories`
	add CONSTRAINT `fk_sessionkey_lcategories` 
	FOREIGN KEY (`_fk_sessionkey`)
    REFERENCES `session` (`_key`)
    ON DELETE CASCADE;
    
alter table `loader_publication` 
	add CONSTRAINT `fk_sessionkey_lpublication` 
		FOREIGN KEY (`_fk_sessionkey`)
        REFERENCES `session` (`_key`)
        ON DELETE CASCADE;
        
alter table `loader_socks`
	add CONSTRAINT `fk_sessionkey_lsocks` 
    FOREIGN KEY (`_fk_sessionkey`)
        REFERENCES `session` (`_key`)
        ON DELETE CASCADE;
        
alter table `loader_standard_relationship`
	add CONSTRAINT `fk_sessionkey_lstandard_relationship` 
    FOREIGN KEY (`_fk_sessionkey`)
        REFERENCES `session` (`_key`)
        ON DELETE CASCADE;
        
alter table  `loader_standards`
	add CONSTRAINT `fk_sessionkey_lstandards` 
    FOREIGN KEY (`_fk_sessionkey`)
        REFERENCES `session` (`_key`)
        ON DELETE CASCADE;
        
alter table `publication`
	add CONSTRAINT `fk_publicationpublisher` 
    FOREIGN KEY (`_fk_publisher`)
        REFERENCES `publisher` (`_key`)
        ON DELETE NO ACTION 
        ON UPDATE CASCADE;
        
alter table `publication`
	add CONSTRAINT `fk_publicationsubject` 
    FOREIGN KEY (`_fk_subject`)
        REFERENCES `subject` (`_key`)
        ON DELETE NO ACTION 
        ON UPDATE CASCADE;
        
alter table `sock_drawer`
	add CONSTRAINT `fk_kdpublication` 
    FOREIGN KEY (`_fk_publication`)
        REFERENCES `publication` (`_key`)
        ON DELETE CASCADE 
        ON UPDATE CASCADE;
        
alter table `sock_relationship`
	add CONSTRAINT `fk_kd_a` 
    FOREIGN KEY (`_fk_sock_a`)
        REFERENCES `sock_drawer` (`_key`)
        ON DELETE CASCADE 
        ON UPDATE NO ACTION;
        
alter table `sock_relationship`
	add CONSTRAINT `fk_kd_b` 
    FOREIGN KEY (`_fk_sock_b`)
        REFERENCES `sock_drawer` (`_key`)
        ON DELETE NO ACTION 
        ON UPDATE NO ACTION;
        
alter table `standard`
  add CONSTRAINT `fk_standard` FOREIGN KEY (`_fk_parent`)
        REFERENCES `standard` (`_key`)
        ON DELETE CASCADE ON UPDATE NO ACTION;
        
 alter table `standard`       
    add CONSTRAINT `fk_standard_pub` FOREIGN KEY (`_fk_publication`)
        REFERENCES `publication` (`_key`)
        ON DELETE CASCADE ON UPDATE NO ACTION;
        
alter table `standard_category`
   add CONSTRAINT `fk_categorypub` FOREIGN KEY (`_fk_publication`)
        REFERENCES `publication` (`_key`)
        ON DELETE CASCADE ON UPDATE NO ACTION;
        
alter table `standard_grade`
  add CONSTRAINT `fk_standard_grade_gradelevel` FOREIGN KEY (`_fk_gradelevel`)
        REFERENCES `gradelevel` (`_key`)
        ON DELETE NO ACTION ON UPDATE CASCADE;
        
alter table `standard_grade`
  add CONSTRAINT `fk_standard_grade_standard` FOREIGN KEY (`_fk_standard`)
        REFERENCES `standard` (`_key`)
        ON DELETE CASCADE ON UPDATE NO ACTION;  
        
alter table `standard_relationship`
  add CONSTRAINT `fk_stndrel_a` FOREIGN KEY (`_fk_standard_a`)
        REFERENCES `standard` (`_key`)
        ON DELETE CASCADE ON UPDATE NO ACTION;
        
alter table `standard_relationship`
  add CONSTRAINT `fk_stndrel_b` FOREIGN KEY (`_fk_standard_b`)
        REFERENCES `standard` (`_key`)
        ON DELETE CASCADE ON UPDATE NO ACTION;
        
alter table `standard_relationship`
  add CONSTRAINT `fk_reltype` FOREIGN KEY (`_fk_relationshiptype`)
        REFERENCES `relationshiptype` (`_key`)
        ON DELETE CASCADE ON UPDATE NO ACTION;        
        
    