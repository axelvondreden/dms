package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.Doc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DocRepository extends JpaRepository<Doc, Long> {

    Page<Doc> findByUploadDateAfter(LocalDate filterDate, Pageable pageable);

    long countByUploadDateAfter(LocalDate uploadDate);

    @Query("SELECT month(d.uploadDate) as month, count(*) as docs FROM docs d where year(uploadDate)=?1 group by month(uploadDate)")
    List<Object[]> countPerMonth(int year);

    @Query("SELECT day(d.uploadDate) as day, count(*) as docs FROM docs d where year(uploadDate)=?1 and month(uploadDate)=?2 group by day(uploadDate)")
    List<Object[]> countPerDay(int year, int month);

}
