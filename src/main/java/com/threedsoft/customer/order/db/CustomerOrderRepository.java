package com.threedsoft.customer.order.db;

import java.util.List;
import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long>{

	@Query("select o from CustomerOrder o where o.busName=:busName and o.locnNbr=:locnNbr and o.id=:id")
	public CustomerOrder findById(@Param("busName") String busName, @Param("locnNbr") Integer locnNbr, @Param("id") Long id);

	@Query("select o from CustomerOrder o where o.busName=:busName and o.locnNbr=:locnNbr and o.id=:id")
	public CustomerOrder findByBusNameAndLocnNbrAndOrderNbr(@Param("busName") String busName, @Param("locnNbr") Integer locnNbr, @Param("id") Long id);
	
	@Query("select o from CustomerOrder o inner join fetch o.orderLines ol where o.busName=:busName and o.locnNbr=:locnNbr order by o.id")
	public List<CustomerOrder> findByBusNameAndLocnNbr(@Param("busName") String busName, @Param("locnNbr") Integer locnNbr, Pageable pageRequest);
}
