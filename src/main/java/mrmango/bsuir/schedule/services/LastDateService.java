package mrmango.bsuir.schedule.services;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

/**
 * created by Ilya Aleksandrovich
 * on 16-Jun-2018
 */
@Service
@Log4j2
public class LastDateService {
    private final Path lastDateFile = Paths.get("./last_date");
    private LocalDate lastDate;

    @SneakyThrows
    public LastDateService() {
        if (!Files.exists(lastDateFile)) {
            Files.createFile(lastDateFile);
            updateLastDate(LocalDate.MIN);
        } else {
            lastDate = Files.lines(lastDateFile)
                    .filter(line -> line.startsWith("#"))
                    .map(LocalDate::parse)
                    .findFirst()
                    .orElse(LocalDate.MIN);
        }
    }

    public LocalDate getLastDate() {
        return lastDate;
    }

    @SneakyThrows
    public void updateLastDate(LocalDate date) {
        log.info("Updating last date with " + date.toString());
        lastDate = date;
        Files.write(lastDateFile, date.toString().getBytes());
    }
}
