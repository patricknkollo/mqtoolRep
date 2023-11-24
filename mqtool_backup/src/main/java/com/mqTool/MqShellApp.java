package com.mqTool;

import com.mqTool.services.ReadProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.awt.*;
import java.net.URI;

@SpringBootApplication
public class MqShellApp {
    public static void main(String[] args) {
        SpringApplicationBuilder app = new SpringApplicationBuilder(MqShellApp.class);
        if (args.length == 0) {
            app.web(WebApplicationType.SERVLET);
            disableShell();
            launchBrowser();
        } else {
            args = new String[]{};
            app.web(WebApplicationType.NONE);
        }
        app.run(args);
    }

    private static void launchBrowser() {
        ReadProperties properties = new ReadProperties();
        Logger logger = LoggerFactory.getLogger(MqShellApp.class);
        System.setProperty("java.awt.headless", "false");
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("http://localhost" + ":" + properties.getWebPort()));
        } catch (Exception e) {
            logger.info(e.getLocalizedMessage());
        }
    }

    private static void disableShell() {
        ReadProperties properties = new ReadProperties();
        System.setProperty("spring.shell.interactive.enabled", "false");
    }
}
