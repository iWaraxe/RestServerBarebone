package com.coherentsolutions.restful.repository;

import com.coherentsolutions.restful.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNameAndSex(String name, String sex);
    List<User> findAllByZipCodeIsNotNull();
}
