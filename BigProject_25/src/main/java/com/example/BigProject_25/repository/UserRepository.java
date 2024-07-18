package com.example.BigProject_25.repository;

import com.example.BigProject_25.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUserID(String userID);
//    User findByVerificationToken(String token);
}
