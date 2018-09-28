package com.example.customer.order.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long>{

	@Query("select o from CustomerOrder o where o.busName=:busName and o.locnNbr=:locnNbr and o.id=:id")
	public CustomerOrder findById(@Param("busName") String busName, @Param("locnNbr") Integer locnNbr, @Param("id") Long id);

	@Query("select o from CustomerOrder o where o.busName=:busName and o.locnNbr=:locnNbr and o.company=:company and o.division=:division and o.busUnit=:busUnit")
	public List<CustomerOrder> findByUniqueKey(@Param("busName") Integer busName, @Param("locnNbr") Integer locnNbr, @Param("company") String company, @Param("division") String division, @Param("busUnit") String busUnit);
	
	@Query("select o from CustomerOrder o where o.busName=:busName and o.locnNbr=:locnNbr and o.orderNbr=:orderNbr")
	public CustomerOrder findByBusNameAndLocnNbrAndOrderNbr(@Param("busName") String busName, @Param("locnNbr") Integer locnNbr, @Param("orderNbr") String orderNbr);
	
	@Query("select o from CustomerOrder o where o.busName=:busName and o.locnNbr=:locnNbr and o.orderNbr=:orderNbr")
	public List<CustomerOrder> findByUniqueKey(@Param("busName") Integer busName, @Param("locnNbr") Integer locnNbr, @Param("orderNbr") String orderNbr);
}
