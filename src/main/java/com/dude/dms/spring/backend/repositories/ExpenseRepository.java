package com.dude.dms.spring.backend.repositories;

import com.dude.dms.spring.backend.data.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

  long countByTimestampAfter(LocalDateTime timestamp);

  @Query("SELECT month(timestamp) as month, count(*) as transactions FROM Transaction o where year(timestamp)=?1 group by month(timestamp)")
  List<Object[]> countPerMonth(int year);

  //@Query("SELECT year(o.timestamp) as y, month(o.timestamp) as m, sum(oi.quantity*p.price) as deliveries FROM OrderInfo o JOIN o.items oi JOIN oi.product p where year(o.dueDate)<=?1 AND year(o.dueDate)>=(?1-3) group by year(o.dueDate), month(o.dueDate) order by y desc, month(o.dueDate)")
  //List<Object[]> sumPerMonthLastThreeYears(int year);

  @Query("SELECT day(timestamp) as day, count(*) as transactions FROM Transaction o where year(timestamp)=?1 and month(timestamp)=?2 group by day(timestamp)")
  List<Object[]> countPerDay(int year, int month);

}
