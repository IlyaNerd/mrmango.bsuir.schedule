package mrmango.bsuir.schedule.main

import mrmango.bsuir.schedule.config.AppConfig
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericGroovyApplicationContext

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
class Main {

    static void main(String[] args) {
        ApplicationContext context = new GenericGroovyApplicationContext(AppConfig.class)
    }
}
