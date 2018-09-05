package mrmango.bsuir.schedule.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by Ilya Aleksandrovich
 * on 09-Jun-2018
 */
@Service
@Slf4j
public class MarkedFileService {
    private final PdfParser pdfParser;
    private final String dir;

    @Autowired
    public MarkedFileService(PdfParser pdfParser,
                             @Value("${download.dir}") String dir) {
        this.pdfParser = pdfParser;
        this.dir = dir;
    }

    @SneakyThrows
    public List<File> getMarkedPdfFiles() {
        log.info("Searching for marked pdf files in " + dir);
        return Files.list(Paths.get(dir))
                .filter(path -> path.toString().endsWith(".pdf"))
                .map(Path::toFile)
                .filter(pdfParser::pdfContainsAnnotations)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public void moveParsedFiles(List<File> markedFiles) {
        log.info("Moving parsed files to ./parsed dir");
        try {
            Files.createDirectory(Paths.get(dir + "/parsed"));
        } catch (FileAlreadyExistsException ignored) {
        }
        for (File file : markedFiles) {
            try {
                Files.move(file.toPath(), Paths.get(dir + "/parsed/" + file.getName()));
            } catch (IOException e) {
                Thread.sleep(5000); //file still might be busy, lets wait and move it after a sec
                Files.move(file.toPath(), Paths.get(dir + "/parsed/" + file.getName()));
            }
        }
    }
}
