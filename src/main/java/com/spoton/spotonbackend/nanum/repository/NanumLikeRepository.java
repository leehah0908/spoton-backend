package com.spoton.spotonbackend.nanum.repository;

import com.spoton.spotonbackend.nanum.entity.NanumLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NanumLikeRepository extends JpaRepository<NanumLike, Long> {
    boolean existsByUser_UserIdAndNanum_NanumId(Long userId, Long nanumId);

    Optional<NanumLike> findByUser_UserIdAndNanum_NanumId(Long userId, Long nanumId);
}
