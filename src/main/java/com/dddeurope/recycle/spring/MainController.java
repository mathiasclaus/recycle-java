package com.dddeurope.recycle.spring;

import com.dddeurope.recycle.commands.CommandMessage;
import com.dddeurope.recycle.domain.DroppedFraction;
import com.dddeurope.recycle.domain.Fraction;
import com.dddeurope.recycle.events.EventMessage;
import com.dddeurope.recycle.events.FractionWasDropped;
import com.dddeurope.recycle.events.PriceWasCalculated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/validate")
    public String validate() {
        return "Hi!";
    }

    @PostMapping("/handle-command")
    public ResponseEntity<EventMessage> handle(@RequestBody RecycleRequest request) {
        LOGGER.info("Incoming Request: {}", request.asString());

        var cost = calculateTotalCost(request);

        var message = new EventMessage("todo", new PriceWasCalculated("123", cost, "EUR"));

        return ResponseEntity.ok(message);
    }

    private double calculateTotalCost(RecycleRequest request) {
        var history = request.history();
        List<FractionWasDropped> fractionWasDroppedEvents = history.stream()
            .filter(event -> "FractionWasDropped".equals(event.getType()))
            .map(event -> (FractionWasDropped) event.getPayload())
            .toList();

        var droppedFractions = fractionWasDroppedEvents.stream()
            .map(this::mapToDroppedFraction)
            .toList();

        var totalCost = droppedFractions.stream().mapToDouble(DroppedFraction::calculateCost).sum();
        return roundTwoDecimals(totalCost);
    }

    private DroppedFraction mapToDroppedFraction(FractionWasDropped event) {
       return Arrays.stream(Fraction.values())
           .filter(fraction -> fraction.getTyoe().equals(event.fractionType()))
           .findFirst()
           .map(it -> new DroppedFraction(it, event.weight()))
           .orElseThrow();
    }

    private static double roundTwoDecimals(double totalCost) {
        return BigDecimal.valueOf(totalCost).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public record RecycleRequest(List<EventMessage> history, CommandMessage command) {

        public String asString() {
            var historyAsString = history.stream()
                .map(EventMessage::toString)
                .collect(Collectors.joining("\n\t"));

            return String.format("%n%s %nWith History\n\t%s", command, historyAsString);
        }
    }
}
