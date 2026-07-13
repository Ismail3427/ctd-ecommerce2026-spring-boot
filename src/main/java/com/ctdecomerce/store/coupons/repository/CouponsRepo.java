package com.ctdecomerce.store.coupons.repository;

import com.ctdecomerce.store.coupons.model.CouponsModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponsRepo extends JpaRepository<CouponsModel, UUID> {
}
