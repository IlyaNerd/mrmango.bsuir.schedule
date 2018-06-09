package mrmango.bsuir.schedule.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
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
        return Files.list(Paths.get(dir))
                .filter(path -> path.toString().endsWith(".pdf"))
                .map(Path::toFile)
                .filter(pdfParser::pdfContainsAnnotations)
                .collect(Collectors.toList());
    }
}
