package com.fitlogtimer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlogtimer.model.Session;
import com.fitlogtimer.repository.SessionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public Session createSessionIfNotExists(Session session) {

        Optional<Session> existingSession = sessionRepository.findById(session.getId());
        if (existingSession.isPresent()) {
            return existingSession.get();
        } else {
            return sessionRepository.save(session);
        }
    }
}
