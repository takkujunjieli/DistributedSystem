package io.swagger.api;

import io.swagger.model.LiftRide;
import io.swagger.model.LiftRideRequest;
import io.swagger.model.SkierVertical;
import io.swagger.RabbitMQSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-10-11T23:58:45.751066896Z[GMT]")
@RestController
@Validated
public class SkiersApiController implements SkiersApi {

    private static final Logger log = LoggerFactory.getLogger(SkiersApiController.class);
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final RabbitMQSender rabbitMQSender;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Autowired
    public SkiersApiController(ObjectMapper objectMapper, HttpServletRequest request,
        RabbitMQSender rabbitMQSender) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.rabbitMQSender = rabbitMQSender;
    }

    public ResponseEntity<Integer> getSkierDayVertical(
              @PathVariable("resortID") @NotNull @Min(1) Integer resortID,
              @PathVariable("seasonID") @NotBlank String seasonID,
              @PathVariable("dayID") @NotNull @Min(1) @Max(366) String dayID,
              @PathVariable("skierID") @NotNull @Min(1) Integer skierID) {
//            @Parameter(in = ParameterIn.PATH, description = "ID of the resort the skier is at", required = true, schema = @Schema()) @PathVariable("resortID") Integer resortID,
//            @Parameter(in = ParameterIn.PATH, description = "ID of the ski season", required = true, schema = @Schema()) @PathVariable("seasonID") String seasonID,
//            @DecimalMin("1") @DecimalMax("366") @Parameter(in = ParameterIn.PATH, description = "ID number of ski day in the ski season", required = true, schema = @Schema()) @PathVariable("dayID") String dayID,
//            @Parameter(in = ParameterIn.PATH, description = "ID of the skier riding the lift", required = true, schema = @Schema()) @PathVariable("skierID") Integer skierID)
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Integer>(objectMapper.readValue("34507", Integer.class),
                        HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Integer>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Integer>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<SkierVertical> getSkierResortTotals(
            @PathVariable("skierID") @NotNull @Min(1) Integer skierID,
            @NotNull @RequestParam(value = "resort", required = true) @NotNull @Size(min = 1) List<@NotBlank String> resort,
            @RequestParam(value = "season", required = false) List<@NotBlank String> season) {
//            @Parameter(in = ParameterIn.PATH, description = "ID the skier to retrieve data for", required = true, schema = @Schema()) @PathVariable("skierID") Integer skierID,
//            @NotNull @Parameter(in = ParameterIn.QUERY, description = "resort to filter by", required = true, schema = @Schema()) @Valid @RequestParam(value = "resort", required = true) List<String> resort,
//            @Parameter(in = ParameterIn.QUERY, description = "season to filter by, optional", schema = @Schema()) @Valid @RequestParam(value = "season", required = false) List<String> season) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<SkierVertical>(objectMapper.readValue(
                        "{\n  \"resorts\" : [ {\n    \"seasonID\" : \"seasonID\",\n    \"totalVert\" : 0\n  }, {\n    \"seasonID\" : \"seasonID\",\n    \"totalVert\" : 0\n  } ]\n}",
                        SkierVertical.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<SkierVertical>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<SkierVertical>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> writeNewLiftRide(
            @PathVariable("resortID") Integer resortID,
            @PathVariable("seasonID") String seasonID,
            @DecimalMin("1") @DecimalMax("366") @PathVariable("dayID") String dayID,
            @PathVariable("skierID") @NotNull @Min(1) Integer skierID,
            @Valid @RequestBody LiftRide body) {
        // Validate input values
        if (resortID == null || seasonID == null || dayID == null || skierID == null || body == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), "Required parameters are missing");
        }

        if (resortID <= 0 || skierID <= 0 || Integer.parseInt(dayID) < 1 || Integer.parseInt(dayID) > 366) {
            throw new  ApiException(HttpStatus.BAD_REQUEST.value(), "Required parameters are invalid");
        }

        // Log the received data (for debugging purposes)
        log.info("Received lift ride for skierID: " + skierID + " at resortID: " + resortID);
        LiftRideRequest liftRideRequest = new LiftRideRequest(resortID, seasonID, dayID, skierID, body);
        rabbitMQSender.sendLiftRide(liftRideRequest);

        // Simulate success response
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
