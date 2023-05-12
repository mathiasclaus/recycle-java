package com.dddeurope.recycle.spring;

import com.dddeurope.recycle.commands.CommandMessage;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final double CONSTRUCTION_WASTE_COST = 0.15;

    @GetMapping("/validate")
    public String validate() {
        return "Hi!";
    }

    @PostMapping("/handle-command")
    public ResponseEntity<EventMessage> handle(@RequestBody RecycleRequest request) {
        LOGGER.info("Incoming Request: {}", request.asString());

        var cost = processRequest(request);

        var message = new EventMessage("todo", new PriceWasCalculated("123", cost, "EUR"));

        return ResponseEntity.ok(message);
    }

    private double processRequest(RecycleRequest request) {
        var history = request.history();
        List<FractionWasDropped> fractionWasDroppedEvents = history.stream()
            .filter(event -> "FractionWasDropped".equals(event.getType()))
            .map(event -> (FractionWasDropped) event.getPayload())
            .toList();

        var constructionWasteWeight = fractionWasDroppedEvents.stream()
            .filter(event -> "Construction waste".equals(event.fractionType()))
            .mapToDouble(FractionWasDropped::weight)
            .sum();

        return constructionWasteWeight * CONSTRUCTION_WASTE_COST;
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
