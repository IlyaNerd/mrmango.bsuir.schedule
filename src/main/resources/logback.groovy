import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.filter.ThresholdFilter

statusListener(NopStatusListener)
appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }

    filter(ThresholdFilter) {
        level = WARN
    }
}
appender("FILE", RollingFileAppender) {
    append = false
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }

    filter(ThresholdFilter) {
        level = DEBUG
    }

    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "app.%d{yyyy-MM-dd}.log"
        maxHistory = 7
    }
}
appender("ASYNC", AsyncAppender) {
    appenderRef("FILE")
}
logger("org.springframework", INFO, ["ASYNC"])
root(DEBUG, ["CONSOLE", "ASYNC"])