package com.coherentsolutions.restful.repository;

import com.coherentsolutions.restful.model.ZipCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZipCodeRepository extends JpaRepository<ZipCode, Long> {
    boolean existsByCode(String code);
    List<ZipCode> findByCodeIn(List<String> codes);
}