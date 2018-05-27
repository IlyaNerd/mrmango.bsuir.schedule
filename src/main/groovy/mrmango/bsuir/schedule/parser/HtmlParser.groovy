package mrmango.bsuir.schedule.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 *
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
@Component
@Scope(value = "prototype")
class HtmlParser {
    final Document document

    HtmlParser(@Value("${page.url}") String url) {
        document = Jsoup.connect(url).get()
    }

}
