package com.app.salty.user.repository;

import com.app.salty.user.common.AuthProvider;
import com.app.salty.user.entity.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialRepository extends JpaRepository<SocialProvider,Long> ,SocialRepositoryCustom{

}
