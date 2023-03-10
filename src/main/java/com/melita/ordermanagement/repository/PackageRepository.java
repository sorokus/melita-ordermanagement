package com.melita.ordermanagement.repository;

import com.melita.ordermanagement.model.entity.Package;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/*
 * @author sorokus.dev@gmail.com
 */

@Repository
public interface PackageRepository extends CrudRepository<Package, Long> {
}
