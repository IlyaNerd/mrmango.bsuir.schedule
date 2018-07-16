package mrmango.bsuir.schedule.services

import spock.lang.Specification


/**
 *
 * @author Ilya Aleksandrovich
 * created on 2018-07-16
 * */
class UnarchiverSpec extends Specification {


    def "unrar file"() {
        given:
        def rar = new File(getClass().getClassLoader().getResource("test.rar")?.toURI())
        def unarchiver = new Unarchiver()

        when:
        unarchiver.unrar(rar)

        then:
        def dir = rar.getParentFile()
        dir.list().contains("first.txt")
        new File(dir, "first.txt").getText() == "first"
        dir.list().contains("second.txt")
        new File(dir, "second.txt").getText() == "second"
    }
}
