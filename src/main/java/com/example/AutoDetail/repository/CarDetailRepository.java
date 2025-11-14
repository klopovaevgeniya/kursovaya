package com.example.AutoDetail.repository;

import com.example.AutoDetail.entity.CarDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarDetailRepository extends JpaRepository<CarDetail, Long> {
    Optional<CarDetail> findByVinCode(String vinCode);
    boolean existsByVinCode(String vinCode);
}