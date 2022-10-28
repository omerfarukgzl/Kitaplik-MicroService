package com.Omer.repository;

import com.Omer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

//User findByFirstname(String firstname);
//User findByFirstnameAndLastname(String firstame,String lastname);



}
