package com.POS.SpotifyApp;

import com.POS.SpotifyApp.DataAccess.Models.Song;
import com.POS.SpotifyApp.DataAccess.Repositories.ISongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class SpotifyAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotifyAppApplication.class, args);
	}

}
