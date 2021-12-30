package guru.sfg.beer.inventory.service.service.beer.listener;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.service.InventoryService;
import guru.sfg.brewery.model.events.NewInventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewBeerInventoryListener {

    private final InventoryService inventoryService;

    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(@Payload NewInventoryEvent event){
        log.debug("New inventory event!");
        inventoryService.newInventoryRecord(event.getBeerDto());
    }

    //    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    //    public void listen(Message message){
    //        try {
    //            NewInventoryEvent newInventoryEvent = objectMapper.readValue(message.getBody(String.class), NewInventoryEvent.class);
    //            BeerDto beerDto = newInventoryEvent.getBeerDto();
    //            inventoryService.newInventoryRecord(beerDto);
    //        } catch (JsonProcessingException | JMSException e) {
    //            try {
    //                message.acknowledge();
    //            } catch (JMSException ex) {
    //                ex.printStackTrace();
    //            }
    //            e.printStackTrace();
    //        }
    //    }
}