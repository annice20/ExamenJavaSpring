package com.example.examen.service;

import com.example.examen.model.Notification;
import com.example.examen.model.User;
import com.example.examen.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public void envoyer(User user, String message) {
        Notification n = new Notification();
        n.setUser(user);
        n.setMessage(message);
        n.setDate(LocalDateTime.now());
        repo.save(n);
    }

    public List<Notification> getUserNotifications(User user) {
        return repo.findByUserOrderByDateDesc(user);
    }
}