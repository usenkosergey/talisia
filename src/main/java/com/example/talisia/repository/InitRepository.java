package com.example.talisia.repository;

import com.example.talisia.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Prokopenko Andrey
 * @since 04.08.2024
 */
@Repository
public interface InitRepository extends JpaRepository<ChatEntity, Long> {
}
