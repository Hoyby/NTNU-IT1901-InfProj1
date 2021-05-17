package ajour.restserver;

import ajour.json.TrackerModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestServerApplication {

    //Modules for serialization/deserialization for the classes in core
    @Bean
    public TrackerModule trackerModule() {
        return new TrackerModule();
    }

    //Modules for serialization/deserialization of java.time classes
    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RestServerApplication.class);
        app.run(args);
    }

}
