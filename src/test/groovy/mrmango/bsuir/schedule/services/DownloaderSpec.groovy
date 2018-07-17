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
    File dir = new File('/test_downloader/')

    void setup() {
        dir.deleteDir()
    }

    void cleanup() {
        dir.deleteDir()
    }

    def "download file"() {
        when:
        dir.mkdirs()
        def download = new Downloader(dir.getPath()).download(
                "https://www.google.by/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png")

        then:
        assert download.exists()
        assert download.getName() == 'schedule.rar'
        assert download.getParentFile().getName() == dir.getName()
        assert download.size() == 13504
    }
}
