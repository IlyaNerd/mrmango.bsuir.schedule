package mrmango.bsuir.schedule.services;

import com.google.api.services.calendar.model.Event;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by Ilya Aleksandrovich
 * on 09-Jun-2018
 */
@Service
@Log4j2
public class PdfParser {
    private final TextToEventParser pdfTextToEventService;

    @Autowired
    public PdfParser(TextToEventParser pdfTextToEventService) {
        this.pdfTextToEventService = pdfTextToEventService;
    }

    public List<Event> parsePdf(File file) {
        return pdfTextToEventService.parseTextToEvents(
                getAnnotationsFromPdf(file).stream()
                        .map(PDAnnotation::getContents)
                        .collect(Collectors.toList()));
    }

    public boolean pdfContainsAnnotations(File file) {
        log.info("Checking if pdf contains annotations. file: " + file.getPath());
        return !getAnnotationsFromPdf(file).isEmpty();
    }

    @SneakyThrows
    private List<PDAnnotation> getAnnotationsFromPdf(File file) {
        log.info("Parsing file: " + file.getPath());
        List<PDAnnotation> annotations = new ArrayList<>();
        try (PDDocument document = PDDocument.load(file)) {
            for (PDPage page : document.getPages()) {
                annotations.addAll(page.getAnnotations()
                        .stream()
                        .filter(ann -> ann != null && ann.getContents() != null)
                        .collect(Collectors.toList()));
            }
        }
        return annotations;
    }
}
