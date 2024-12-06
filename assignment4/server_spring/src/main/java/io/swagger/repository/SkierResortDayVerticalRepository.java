package io.swagger.repository;


import io.swagger.model.SkierResortDayVertical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkierResortDayVerticalRepository extends JpaRepository<SkierResortDayVertical, Long> {
  SkierResortDayVertical findBySkierIdAndResortIdAndSeasonIdAndDayId(
      Integer skierId, Integer resortId, Integer seasonId, Integer dayId
  );
}
