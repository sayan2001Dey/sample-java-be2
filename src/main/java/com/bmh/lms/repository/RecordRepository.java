package com.bmh.lms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.bmh.lms.model.Record;
@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    @Query(value="SELECT * FROM record WHERE modified_type='INSERTED'", nativeQuery = true)
    List<Record> findAllActive();

    @Query(value="SELECT * FROM record WHERE rec_id=? AND modified_type='INSERTED'", nativeQuery = true)
    Optional<Record> findByRecId(String id);

}
