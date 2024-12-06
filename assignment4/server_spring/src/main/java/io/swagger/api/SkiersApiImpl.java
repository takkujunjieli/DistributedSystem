package io.swagger.api;

import io.swagger.model.ResortDayVertical;
import io.swagger.model.SkierLiftRides;
import io.swagger.model.SkierResortDayVertical;
import io.swagger.model.SkierTotalVertical;
import io.swagger.repository.ResortDayVerticalRepository;
import io.swagger.repository.SkierLiftRidesRepository;
import io.swagger.repository.AggregatedLiftRidesRepository;
import io.swagger.repository.SkierResortDayVerticalRepository;
import io.swagger.repository.SkierTotalVerticalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class SkiersApiImpl implements SkiersApi {

    private final SkierTotalVerticalRepository skierTotalVerticalRepository;
    private final SkierResortDayVerticalRepository skierResortDayVerticalRepository;
    private final ResortDayVerticalRepository resortDayVerticalRepository;

    public SkiersApiImpl(SkierTotalVerticalRepository skierTotalVerticalRepository,
        SkierResortDayVerticalRepository skierResortDayVerticalRepository,
        ResortDayVerticalRepository resortDayVerticalRepository) {
        this.skierTotalVerticalRepository = skierTotalVerticalRepository;
        this.skierResortDayVerticalRepository = skierResortDayVerticalRepository;
        this.resortDayVerticalRepository = resortDayVerticalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Integer> getSkierResortTotals(Integer skierID) {
        SkierTotalVertical result = skierTotalVerticalRepository.findBySkierId(skierID);
        return new ResponseEntity<>(result != null ? result.getTotalVertical() : 0, HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Integer> getSkierResortVertical(Integer resortID, Integer seasonID, Integer dayID, Integer skierID) {
        SkierResortDayVertical result = skierResortDayVerticalRepository.findBySkierIdAndResortIdAndSeasonIdAndDayId(
            skierID, resortID, seasonID, dayID
        );
        return new ResponseEntity<>(result != null ? result.getTotalVertical() : 0, HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Integer> getSkierDayVertical(Integer resortID, Integer seasonID, Integer dayID) {
        ResortDayVertical result = resortDayVerticalRepository.findByResortIdAndSeasonIdAndDayId(
            resortID, seasonID, dayID
        );
        return new ResponseEntity<>(result != null ? result.getTotalVertical() : 0, HttpStatus.OK);
    }
}



//@RestController
//public class SkiersApiImpl implements SkiersApi {
//
//    private final SkierLiftRidesRepository skierLiftRidesRepository;
//
//    public SkiersApiImpl(SkierLiftRidesRepository skierLiftRidesRepository) {
//        this.skierLiftRidesRepository = skierLiftRidesRepository;
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public ResponseEntity<Integer> getSkierDayVertical(Integer resortID, Integer seasonID, Integer dayID) {
//        List<SkierLiftRides> rides = skierLiftRidesRepository.findByResortIdAndSeasonIdAndDayId(
//            resortID, seasonID, dayID
//        );
//
//
//        int totalVertical = rides.stream().mapToInt(ride -> ride.getLiftId() * 10).sum();
//        return new ResponseEntity<>(totalVertical, HttpStatus.OK);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public ResponseEntity<Integer> getSkierResortTotals(Integer skierID) {
//        List<SkierLiftRides> rides = skierLiftRidesRepository.findBySkierId(skierID);
//        int totalVertical = rides.stream().mapToInt(ride -> ride.getLiftId() * 10).sum();
//        return new ResponseEntity<>(totalVertical, HttpStatus.OK);
//    }
//
//
//    @Override
//    @Transactional(readOnly = true)
//    public ResponseEntity<Integer> getSkierResortVertical(Integer resortID, Integer seasonID, Integer dayID, Integer skierID) {
//        List<SkierLiftRides> rides = skierLiftRidesRepository.findByResortIdAndSeasonIdAndDayIdAndSkierId(
//            resortID, seasonID, dayID, skierID
//        );
//
//        int totalVertical = rides.stream().mapToInt(ride -> ride.getLiftId() * 10).sum();
//        return new ResponseEntity<>(totalVertical, HttpStatus.OK);
//    }
//}