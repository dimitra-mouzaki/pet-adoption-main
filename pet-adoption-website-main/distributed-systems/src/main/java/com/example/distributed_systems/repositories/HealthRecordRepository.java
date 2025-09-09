package com.example.distributed_systems.repositories;

import com.example.distributed_systems.entities.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
}
