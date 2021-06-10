package theater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.ApplicationContext;

import theater.config.kafka.KafkaProcessor;

@SpringBootApplication
@EnableBinding(KafkaProcessor.class)
@EnableFeignClients
public class MovieApplication {
    protected static ApplicationContext applicationContext;

    @Autowired
    MovieManagementRepository movieManagementRepository;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(MovieApplication.class, args);

//        MovieManagementRepository movieManagementRepository = applicationContext.getBean(MovieManagementRepository.class);
//
//        MovieManagement movie = new MovieManagement();
//        movie.setMovieId("MOVIE-0001");
//        movie.setTitle("어벤져스");
//        movie.setStatus("RUNNING");
//
//        movieManagementRepository.save(movie);
    }
}
