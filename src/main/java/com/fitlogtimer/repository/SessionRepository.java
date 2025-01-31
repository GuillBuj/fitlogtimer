package com.fitlogtimer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitlogtimer.model.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {

}