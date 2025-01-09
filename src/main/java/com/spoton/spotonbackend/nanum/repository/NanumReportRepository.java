package com.spoton.spotonbackend.nanum.repository;

import com.spoton.spotonbackend.nanum.entity.NanumReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NanumReportRepository extends JpaRepository<NanumReport, Long> {

    boolean existsByUser_UserIdAndNanum_NanumId(Long userId, Long nanumId);
}
