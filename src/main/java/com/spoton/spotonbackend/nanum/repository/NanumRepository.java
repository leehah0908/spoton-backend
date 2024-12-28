package com.spoton.spotonbackend.nanum.repository;

import com.spoton.spotonbackend.nanum.entity.Nanum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NanumRepository extends JpaRepository<Nanum, Long> {
    List<Nanum> findTop10ByOrderByCreateTimeDesc();
}
