package com.randywallace.shinywrench.logback;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;

import java.nio.charset.Charset;

public class ConfigureLogback {

    /*
     * Do not run this until JavaFX is starting (i.e. not in main)
     */
    public static void addJavaFxAppender(LoggerContext loggerContext) {
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setCharset(Charset.forName("UTF-8"));
        encoder.setContext(loggerContext);
        PatternLayout layout = new PatternLayout();
        layout.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n%rEx{5}");
        layout.setContext(loggerContext);
        layout.start();
        encoder.setLayout(layout);
        JavaFXTextAreaAppender<ILoggingEvent> ta;
        ta = new JavaFXTextAreaAppender<>();
        ta.setContext(loggerContext);
        ta.setName("JavaFXTextArea");
        ta.setEncoder(encoder);
        ta.start();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(ta);
    }

    public static void configure(LoggerContext loggerContext) {
        System.out.println("Setting up Logback...");
        StatusManager statusManager = loggerContext.getStatusManager();
        if (statusManager != null) {
            statusManager.add(new InfoStatus("Configuring logger for ShinyWrench", loggerContext));
        }
        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
        ca.setContext(loggerContext);
        ca.setName("STDOUT");
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setCharset(Charset.forName("UTF-8"));
        encoder.setContext(loggerContext);
        PatternLayout layout = new PatternLayout();
        layout.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n%rEx{5}");
        layout.setContext(loggerContext);
        layout.start();
        encoder.setLayout(layout);
        ca.setEncoder(encoder);
        ca.start();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAppender("console");
        rootLogger.addAppender(ca);
        rootLogger.setLevel(Level.INFO);
    }
}
