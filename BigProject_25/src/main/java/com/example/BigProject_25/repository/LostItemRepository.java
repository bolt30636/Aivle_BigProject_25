package com.example.BigProject_25.repository;

import com.example.BigProject_25.model.LostItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostItemRepository extends JpaRepository<LostItem, Integer> {
    Page<LostItem> findByCategory(String category, Pageable pageable);
}
