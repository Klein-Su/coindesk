package com.cub.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cub.entity.CurrencyEntity;

@Repository
public interface CurrencyRepo extends JpaRepository<CurrencyEntity, String> {
	
	public CurrencyEntity findByEname(String ename);
}