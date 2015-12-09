 alter table `loader_initialize`
  drop foreign key `fk_sessionkey_linitialize` 
  ;
 call drop_index_if_exists ('loader_initialize', 'fk_sessionkey_linitialize');
  
 alter table `loader_benchmarkgrades` 
  drop foreign key `fk_sessionkey_lbenchmarkgrades` 
  ;
 call drop_index_if_exists ('loader_benchmarkgrades', 'fk_sessionkey_lbenchmarkgrades'); 
 
alter table `loader_errors`
	drop foreign key `fk_sessionkey_lerrors` 
    ;
call drop_index_if_exists ('loader_errors', 'fk_sessionkey_lerrors');

alter table `loader_categories`
	drop foreign key `fk_sessionkey_lcategories` 
	;
call drop_index_if_exists ('loader_categories', 'fk_sessionkey_lcategories');  

alter table `loader_publication` 
	drop foreign key `fk_sessionkey_lpublication` 
		;
call drop_index_if_exists ('loader_publication', 'fk_sessionkey_lpublication'); 

alter table `loader_socks`
	drop foreign key `fk_sessionkey_lsocks` 
    ;    
call drop_index_if_exists ('loader_socks', 'fk_sessionkey_lsocks'); 

alter table `loader_standard_relationship`
	drop foreign key `fk_sessionkey_lstandard_relationship` 
    ;
call drop_index_if_exists ('loader_standard_relationship', 'fk_sessionkey_lstandard_relationship'); 

alter table  `loader_standards`
	drop foreign key `fk_sessionkey_lstandards` 
    ;
call drop_index_if_exists ('loader_standards', 'fk_sessionkey_lstandards');

alter table `publication`
	drop foreign key `fk_publicationpublisher` 
    ;
call drop_index_if_exists ('publication', 'fk_publicationpublisher');

alter table `publication`
	drop foreign key `fk_publicationsubject` 
    ;
call drop_index_if_exists ('publication', 'fk_publicationsubject');

alter table `sock_drawer`
	drop foreign key `fk_kdpublication` 
    ;
call drop_index_if_exists ('sock_drawer', 'fk_kdpublication');

alter table `sock_relationship`
	drop foreign key `fk_kd_a` 
    ;
call drop_index_if_exists ('sock_relationship', 'fk_kd_a');

alter table `sock_relationship`
	drop foreign key `fk_kd_b` 
    ;
call drop_index_if_exists ('sock_relationship', 'fk_kd_b');

alter table `standard`
  drop foreign key `fk_standard` ;
call drop_index_if_exists ('standard', 'fk_standard');

 alter table `standard`       
    drop foreign key `fk_standard_pub` ;
call drop_index_if_exists ('standard', 'fk_standard_pub');

alter table `standard_category`
   drop foreign key `fk_categorypub` ;
call drop_index_if_exists ('standard_category', 'fk_categorypub');
        
alter table `standard_grade`
  drop foreign key `fk_standard_grade_gradelevel` ;
call drop_index_if_exists ('standard_grade', 'fk_standard_grade_gradelevel');

alter table `standard_grade`
  drop foreign key `fk_standard_grade_standard` ;  
call drop_index_if_exists ('standard_grade', 'fk_standard_grade_standard');

alter table `standard_relationship`
  drop foreign key `fk_stndrel_a` ;
call drop_index_if_exists ('standard_relationship', 'fk_stndrel_a');
        
alter table `standard_relationship`
  drop foreign key `fk_stndrel_b` ;
call drop_index_if_exists ('standard_relationship', 'fk_stndrel_b');
          
alter table `standard_relationship`
  drop foreign key `fk_reltype` ;  
call drop_index_if_exists ('standard_relationship', 'fk_reltype');  

        