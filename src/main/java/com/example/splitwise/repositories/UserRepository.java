package com.example.splitwise.repositories;

import com.example.splitwise.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByEmailIn(List<String> emails);

    User findByEmail(String email);
}
