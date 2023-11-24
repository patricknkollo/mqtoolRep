package com.mqTool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestMqShellApp {

	public static void main(String[] args) {
		SpringApplication.from(MqShellApp::main).with(TestMqShellApp.class).run(args);
	}

}
