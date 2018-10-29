package com.threedsoft.customer.order.db;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderLineRepository extends JpaRepository<CustomerOrderLine, Long>{
//
//	@Query("select ol from CustomerOrderLine ol join ol.order o where o.busName=:busName and o.locnNbr=:locnNbr and o.id=:orderId order by ol.id")
//	public List<CustomerOrderLine> findByBusNameAndLocnNbrAndOrderId(@Param("busName") String busName, @Param("locnNbr") Integer locnNbr, @Param("orderId") Long orderId);
}
