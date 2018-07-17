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
import java.time.LocalDate;
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

    @SneakyThrows
    public List<Event> parsePdf(File file) {
        List<PDAnnotation> annotations = getAnnotationsFromPdf(file);
        LocalDate[] dates = pdfTextToEventService.parseDatesFromTo(annotations.get(0).getContents().trim());
        LocalDate from = dates[0];
        LocalDate to = dates[1];

        return annotations.stream()
                .skip(1) //date from to
                .map(an -> pdfTextToEventService.parsePdfTextToEvent(an.getContents(), from, to))
                .collect(Collectors.toList());
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
