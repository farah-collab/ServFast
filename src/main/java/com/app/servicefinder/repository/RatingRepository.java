package com.app.servicefinder.repository;
 
import com.app.servicefinder.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
 
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByServiceId(Long serviceId);
    boolean existsByReviewerIdAndServiceId(Long reviewerId, Long serviceId);
 
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.service.id = :serviceId")
    Long countByServiceId(@Param("serviceId") Long serviceId);
}