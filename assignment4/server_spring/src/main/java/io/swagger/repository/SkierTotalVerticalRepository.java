package io.swagger.repository;


import io.swagger.model.SkierTotalVertical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface SkierTotalVerticalRepository extends JpaRepository<SkierTotalVertical, Integer> {
  SkierTotalVertical findBySkierId(Integer skierId);
}
