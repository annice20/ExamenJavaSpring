package com.example.examen.repository;

import com.example.examen.model.Notification;
import com.example.examen.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserOrderByDateDesc(User user);
}