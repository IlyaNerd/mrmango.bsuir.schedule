package mrmango.bsuir.schedule.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class LastDateService {
    private final Path lastDateFile;
    private LocalDate lastDate;

    @Autowired
    @SneakyThrows
    public LastDateService(@Value("${last.date.file}") String lastDateFilePath) {
        lastDateFile = Paths.get(lastDateFilePath);
        if (!Files.exists(lastDateFile)) {
            Files.createFile(lastDateFile);
            updateLastDate(LocalDate.MIN);
        } else {
            lastDate = Files.lines(lastDateFile)
                    .filter(line -> !line.startsWith("#"))
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
