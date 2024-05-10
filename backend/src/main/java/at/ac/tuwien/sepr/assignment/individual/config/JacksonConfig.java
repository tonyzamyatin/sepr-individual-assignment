package at.ac.tuwien.sepr.assignment.individual.config;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.rest.TournamentStandingsDtoDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer customJackson() {
    return new Jackson2ObjectMapperBuilderCustomizer() {
      @Override
      public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(TournamentStandingsDto.class, new TournamentStandingsDtoDeserializer());
        jacksonObjectMapperBuilder.modules(module);
      }
    };
  }
}
