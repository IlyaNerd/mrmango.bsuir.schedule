import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.filter.ThresholdFilter

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }

    filter(ThresholdFilter) {
        level = INFO
    }
}
appender("FILE", FileAppender) {
    file = "app.log"
    append = false
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }

    filter(ThresholdFilter) {
        level = DEBUG
    }
}
appender("ASYNC", AsyncAppender) {
    appenderRef("FILE")
}
logger("org.springframework", INFO, ["ASYNC"])
root(DEBUG, ["CONSOLE", "ASYNC"])