package com.fitlogtimer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.fitlogtimer.model.Session;

import jakarta.transaction.Transactional;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Session")
    void deleteAllSessions();

    void deleteById(int id);

    boolean existsById(int id);

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE session ALTER COLUMN id RESTART WITH 1", nativeQuery = true)
    void resetSessionId();
}