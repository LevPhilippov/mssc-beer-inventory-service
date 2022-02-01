package guru.sfg.beer.inventory.service.service;

import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.brewery.model.BeerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.Phaser;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class InventoryServiceReactiveImplIT {

    @Autowired
    InventoryReactiveService inventoryReactiveService;

    @Test
    void findAllInventoryRecordsByBeerId() {
        Phaser phaser = new Phaser(1);
        String uuid = "a712d914-61ea-4623-8bd0-32c0f6545bfd";
        inventoryReactiveService.findAllInventoryRecordsByBeerId(UUID.fromString(uuid))
                .subscribe(
                System.out::println,
                throwable -> System.out.println(throwable.getMessage()),
                ()->{
                    System.out.println("Done!");
                    phaser.arrive();
                });
        phaser.awaitAdvance(phaser.getPhase());
    }

    @Test
    void newInventoryRecord() {
        Phaser phaser = new Phaser(1);
        final UUID beerId = UUID.randomUUID();
        BeerInventory beerInventory = BeerInventory.builder().beerId(beerId).quantityOnHand(30).upc("12345").id(UUID.randomUUID()).build();
        BeerDto dto = BeerDto.builder().id(beerId).quantityOnHand(30).upc("123456").build();
        inventoryReactiveService.newInventoryRecord(Mono.just(dto)).subscribe(Void -> {
            System.out.println("Gotcha!");
        }, throwable -> {
            System.out.println("Error!");
            System.out.println(throwable);;
            phaser.arrive();
        }, ()->{
            System.out.println("Done!");
            phaser.arrive();
        });
        phaser.awaitAdvance(0);

    }
}