package com.enzo.url.shortener.repository;

import com.enzo.url.shortener.domain.UrlMapping;
import com.enzo.url.shortener.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);

    List<UrlMapping> findByUser(User user);
}
