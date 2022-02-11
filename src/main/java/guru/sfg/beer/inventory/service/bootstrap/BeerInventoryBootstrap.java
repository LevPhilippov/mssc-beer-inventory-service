package guru.sfg.beer.inventory.service.bootstrap;

import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.service.InventoryServiceReactiveImpl;
import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Created by jt on 2019-06-07.
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Profile("!reactive")
public class BeerInventoryBootstrap implements CommandLineRunner {
    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";
    public static final UUID BEER_1_UUID = UUID.fromString("0a818933-087d-47f2-ad83-2f986ed087eb");
    public static final UUID BEER_2_UUID = UUID.fromString("a712d914-61ea-4623-8bd0-32c0f6545bfd");
    public static final UUID BEER_3_UUID = UUID.fromString("026cc3c8-3a0c-4083-a05b-e908048c1b08");

    private final InventoryServiceReactiveImpl service;


    @Override
    public void run(String... args) throws Exception {
        System.out.println("BOOOTSTRAPING!");
        Phaser phaser = new Phaser(1);
        AtomicLong atomicLong = new AtomicLong();
        service.count().subscribe(
                new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        System.out.println("Recieved value is: " +  aLong);
                        atomicLong.set(aLong);
                        phaser.arrive();
                    }
                }
        );
        phaser.awaitAdvance(phaser.getPhase());

        if(atomicLong.intValue() == 0) {
            loadInitialInv();
//            bootstrap2();
        }
    }

    private void loadInitialInv() {
        System.out.println("ADDING NEW DATA!");
        service.newInventoryRecord(Mono.just(BeerDto
                .builder()
                .id(BEER_1_UUID)
                .upc(BEER_1_UPC)
                .quantityOnHand(50)
                .build())).subscribe();

        service.newInventoryRecord(Mono.just(BeerDto
                .builder()
                .id(BEER_2_UUID)
                .upc(BEER_2_UPC)
                .quantityOnHand(50)
                .build())).subscribe();

        service.newInventoryRecord(Mono.just(BeerDto
                .builder()
                .id(BEER_3_UUID)
                .upc(BEER_3_UPC)
                .quantityOnHand(50)
                .build())).subscribe();

        log.debug("Loaded Inventory. Record count: " + service.count().block());
    }

//    public void bootstrap2() {
//        try {
//            StringBuilder sb = new StringBuilder();
//            Files.readAllLines(Path.of("src/main/resources/inventory-population.sql")).forEach(sb::append);
//            em.createNativeQuery(sb.toString()).executeUpdate();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
