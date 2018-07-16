package mrmango.bsuir.schedule.services

import spock.lang.Shared
import spock.lang.Specification

/**
 *
 * @author Ilya Aleksandrovich
 * created on 2018-07-16
 * */
class DownloaderSpec extends Specification {
    @Shared
    File dir = new File('tmp/')

    void setup() {
        try {
            dir.eachFileRecurse { it.delete() }
            dir.delete()
        } catch (Exception ignored) {
        }
    }

    void cleanup() {
        dir.eachFileRecurse { it.delete() }
        dir.delete()
    }

    def "download file"() {
        when:
        dir.mkdirs()
        def download = new Downloader('/tmp/').download(
                "https://www.google.by/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png")

        then:
        assert download.exists()
        assert download.getName() == 'schedule.rar'
        assert download.getParentFile().getName() == dir.getName()
        assert download.size() == 13504
    }
}
