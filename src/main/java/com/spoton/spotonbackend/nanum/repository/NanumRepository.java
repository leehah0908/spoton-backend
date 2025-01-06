package com.spoton.spotonbackend.nanum.repository;

import com.spoton.spotonbackend.nanum.entity.Nanum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NanumRepository extends JpaRepository<Nanum, Long> {
    List<Nanum> findTop10ByOrderByCreateTimeDesc();

    Optional<List<Nanum>> findByUser_UserIdOrderByCreateTimeDesc(Long userId);

    @Query("SELECT nl.nanum FROM NanumLike nl WHERE nl.user.userId = :userId ORDER BY nl.nanum.createTime DESC")
    Optional<List<Nanum>> findNanumsLikedByUser(@Param("userId") Long userId);
}
