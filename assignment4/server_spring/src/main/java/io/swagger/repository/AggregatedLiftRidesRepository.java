package io.swagger.repository;
import io.swagger.model.AggregatedLiftRides;
import io.swagger.model.SkierLiftRides;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatedLiftRidesRepository extends JpaRepository<AggregatedLiftRides, Long> {

  @Query("SELECT a.totalVertical FROM AggregatedLiftRides a WHERE a.skierId = :skierId")
  Integer findTotalVerticalBySkierId(@Param("skierId") Integer skierId);

  @Query("SELECT a.totalVertical FROM AggregatedLiftRides a WHERE a.skierId = :skierId AND a.resortId = :resortId AND a.seasonId = :seasonId AND a.dayId = :dayId")
  Integer findTotalVerticalBySkierAndResortAndDay(@Param("skierId") Integer skierId,
      @Param("resortId") Integer resortId,
      @Param("seasonId") Integer seasonId,
      @Param("dayId") Integer dayId);

  @Query("SELECT a.totalVertical FROM AggregatedLiftRides a WHERE a.resortId = :resortId AND a.seasonId = :seasonId AND a.dayId = :dayId")
  Integer findTotalVerticalByResortAndDay(@Param("resortId") Integer resortId,
      @Param("seasonId") Integer seasonId,
      @Param("dayId") Integer dayId);
}
