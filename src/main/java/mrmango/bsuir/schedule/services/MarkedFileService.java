package mrmango.bsuir.schedule.services;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
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
@Log4j2
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
    public void moveMarkedFiles(List<File> markedFiles) {
        log.info("Moving parsed files to ./parsed dir");
        try {
            Files.createDirectory(Paths.get(dir + "/parsed"));
        } catch (FileAlreadyExistsException ignored) {
        }
        for (File file : markedFiles) {
            Files.move(file.toPath(), Paths.get(dir + "/parsed/" + file.getName()));
        }
    }
}
