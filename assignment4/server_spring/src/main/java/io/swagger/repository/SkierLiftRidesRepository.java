package io.swagger.repository;

import io.swagger.model.SkierLiftRides;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkierLiftRidesRepository extends JpaRepository<SkierLiftRides, Long> {
  List<SkierLiftRides> findByResortIdAndSeasonIdAndDayIdAndSkierId(int resortId, int seasonId, int dayId, int skierId);
  List<SkierLiftRides> findByResortIdAndSeasonIdAndDayId(int resortId, int seasonId, int dayId);
  List<SkierLiftRides> findBySkierId(int skierId);
}
