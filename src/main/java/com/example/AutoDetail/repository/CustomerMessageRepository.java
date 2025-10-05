package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.CustomerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerMessageRepository extends JpaRepository<CustomerMessage, Long> {

    // Непрочитанные сообщения
    List<CustomerMessage> findByIsReadFalse();

    // Сообщения от конкретного клиента
    List<CustomerMessage> findByClientId(Long clientId);

    // Сообщения отсортированные по дате (новые сначала)
    List<CustomerMessage> findAllByOrderByCreatedAtDesc();
}
