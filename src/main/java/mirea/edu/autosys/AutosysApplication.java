package mirea.edu.autosys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import mirea.edu.autosys.utils.EnvLoader;

@SpringBootApplication
public class AutosysApplication {

	public static void main(String[] args) {
		EnvLoader.load();
		SpringApplication.run(AutosysApplication.class, args);
	}
}
