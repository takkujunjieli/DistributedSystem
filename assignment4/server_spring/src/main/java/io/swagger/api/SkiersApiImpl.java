package io.swagger.api;

import io.swagger.model.LiftRide;
import io.swagger.model.ResponseMsg;
import io.swagger.model.SkierVertical;
import io.swagger.model.SkierLiftRides;
import io.swagger.repository.SkierLiftRidesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@Service
public class SkiersApiImpl implements SkiersApi {

    private final SkierLiftRidesRepository skierLiftRidesRepository;

    public SkiersApiImpl(SkierLiftRidesRepository skierLiftRidesRepository) {
        this.skierLiftRidesRepository = skierLiftRidesRepository;
    }

    @Override
    public ResponseEntity<Integer> getSkierDayVertical(Integer resortID, String seasonID, String dayID) {
        List<SkierLiftRides> rides = skierLiftRidesRepository.findByResortIdAndSeasonIdAndDayId(
            resortID, Integer.parseInt(seasonID), Integer.parseInt(dayID)
        );

        int totalVertical = rides.stream().mapToInt(ride -> ride.getLiftId() * 10).sum(); // Assuming liftId * 10 is vertical
        return new ResponseEntity<>(totalVertical, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Integer> getSkierResortTotals(Integer skierID) {
        List<SkierLiftRides> rides = skierLiftRidesRepository.findBySkierId(skierID);
        int totalVertical = rides.stream().mapToInt(ride -> ride.getLiftId() * 10).sum();
        return new ResponseEntity<>(totalVertical, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Integer> getSkierResortVertical(Integer resortID, String seasonID, String dayID, Integer skierID) {
        List<SkierLiftRides> rides = skierLiftRidesRepository.findByResortIdAndSeasonIdAndDayIdAndSkierId(
            resortID, Integer.parseInt(seasonID), Integer.parseInt(dayID), skierID
        );

        int totalVertical = rides.stream().mapToInt(ride -> ride.getLiftId() * 10).sum();
        return new ResponseEntity<>(totalVertical, HttpStatus.OK);
    }
}




