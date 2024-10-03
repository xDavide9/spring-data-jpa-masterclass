package com.xdavide9.springdatajpamasterclass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// can extend JpaRepository, PagingAndSortingRepository, CrudRepository
// JpaRepository extends PagingAndSortingRepository which extends CrudRepository
@Repository
public interface StudentRepository  extends JpaRepository<Student, Long> {
}
