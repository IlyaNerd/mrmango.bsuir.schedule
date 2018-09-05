package mrmango.bsuir.schedule.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Ilya Aleksandrovich
 * created on 2018-07-18
 **/
@Service
@Slf4j
public class JsoupConnectorImpl implements JsoupConnector {

    private final Connection connection;

    public JsoupConnectorImpl(@Value("${page.url}") String url) {
        log.info("Establishing connection to [" + url + "]");
        connection = Jsoup.connect(url);
    }

    @Override
    @SneakyThrows
    public Document loadDocument() {
        log.info("Loading the document");
        return connection.get();
    }
}
