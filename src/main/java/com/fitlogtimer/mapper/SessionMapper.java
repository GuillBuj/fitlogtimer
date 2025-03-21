package com.fitlogtimer.mapper;

import org.springframework.stereotype.Component;

import com.fitlogtimer.dto.SessionInDTO;
import com.fitlogtimer.model.Session;

@Component
public class SessionMapper {
    
    public Session toSession(SessionInDTO sessionInDTO){
        
        Session session = new Session();
        session.setDate(sessionInDTO.date());
        session.setBodyWeight(sessionInDTO.bodyWeight());
        session.setComment(sessionInDTO.comment());

        return session;
    }
}
