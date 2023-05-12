package com.dddeurope.recycle.spring;

import com.dddeurope.recycle.commands.CalculatePrice;
import com.dddeurope.recycle.commands.CommandMessage;
import com.dddeurope.recycle.domain.ContainerParks;
import com.dddeurope.recycle.domain.ContainerPark;
import com.dddeurope.recycle.domain.DroppedFraction;
import com.dddeurope.recycle.domain.Fraction;
import com.dddeurope.recycle.events.EventMessage;
import com.dddeurope.recycle.events.FractionWasDropped;
import com.dddeurope.recycle.events.IdCardRegistered;
import com.dddeurope.recycle.events.PriceWasCalculated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

        var cardId = getCardId(request);
        var containerPark = determineContainerPark(request, cardId);
        var droppedFractions = getDroppedFractions(request);

        var cost = containerPark.calculateTotalCost(droppedFractions);

        var message = new EventMessage("todo", new PriceWasCalculated(cardId, cost, "EUR"));

        return ResponseEntity.ok(message);
    }

    private String getCardId(RecycleRequest request) {
        var payload = request.command.getPayload();
        if (payload instanceof CalculatePrice calculatePrice) {
            return calculatePrice.cardId();
        }
        throw new RuntimeException("Could not find card id");
    }

    private ContainerPark determineContainerPark(RecycleRequest request, String cardId) {
        return request.history.stream()
            .filter(event -> "IdCardRegistered".equals(event.getType()))
            .map(event -> (IdCardRegistered) event.getPayload())
            .filter(event -> event.cardId().equals(cardId))
            .findFirst()
            .map(payload -> ContainerParks.find(payload.city()))
            .orElseThrow();
    }

    private List<DroppedFraction> getDroppedFractions(RecycleRequest request) {
        var fractionWasDroppedEvents = request.history.stream()
            .filter(event -> "FractionWasDropped".equals(event.getType()))
            .map(event -> (FractionWasDropped) event.getPayload())
            .toList();

        return fractionWasDroppedEvents.stream()
            .map(this::mapToDroppedFraction)
            .toList();
    }

    private DroppedFraction mapToDroppedFraction(FractionWasDropped event) {
        return Arrays.stream(Fraction.values())
            .filter(fraction -> fraction.getType().equals(event.fractionType()))
            .findFirst()
            .map(fraction -> new DroppedFraction(fraction, event.weight()))
            .orElseThrow();
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
