package io.swagger.repository;

import io.swagger.model.ResortDayVertical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ResortDayVerticalRepository extends JpaRepository<ResortDayVertical, Long> {
  ResortDayVertical findByResortIdAndSeasonIdAndDayId(Integer resortId, Integer seasonId, Integer dayId);
}
