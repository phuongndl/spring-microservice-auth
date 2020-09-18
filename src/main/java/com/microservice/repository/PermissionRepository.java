package com.microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservice.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>{

}
