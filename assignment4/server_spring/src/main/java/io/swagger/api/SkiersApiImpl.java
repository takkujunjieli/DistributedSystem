package io.swagger.api;

import io.swagger.model.SkierLiftRides;
import io.swagger.repository.SkierLiftRidesRepository;
import io.swagger.repository.AggregatedLiftRidesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

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




@RestController
public class SkiersApiImpl implements SkiersApi {

    private final AggregatedLiftRidesRepository aggregatedLiftRidesRepository;

    public SkiersApiImpl(AggregatedLiftRidesRepository aggregatedLiftRidesRepository) {
        this.aggregatedLiftRidesRepository = aggregatedLiftRidesRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Integer> getSkierResortTotals(Integer skierID) {
        Integer totalVertical = aggregatedLiftRidesRepository.findTotalVerticalBySkierId(skierID);
        return new ResponseEntity<>(totalVertical, HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Integer> getSkierResortVertical(Integer resortID, Integer seasonID, Integer dayID, Integer skierID) {
        Integer totalVertical = aggregatedLiftRidesRepository.findTotalVerticalBySkierAndResortAndDay(
            skierID, resortID, seasonID, dayID
        );
        return new ResponseEntity<>(totalVertical, HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Integer> getSkierDayVertical(Integer resortID, Integer seasonID, Integer dayID) {
        Integer totalVertical = aggregatedLiftRidesRepository.findTotalVerticalByResortAndDay(
            resortID, seasonID, dayID
        );
        return new ResponseEntity<>(totalVertical, HttpStatus.OK);
    }
}
