package at.ac.tuwien.sepr.assignment.individual.rest;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TournamentStandingsDtoDeserializer extends JsonDeserializer<TournamentStandingsDto> {

  @Override
  public TournamentStandingsDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    // Use the ObjectMapper to deserialize the JSON into a Map
    ObjectMapper mapper = (ObjectMapper) p.getCodec();
    Map<String, Object> map = mapper.readValue(p, new TypeReference<Map<String, Object>>() {});

    // Extract and convert the fields from the Map
    Long id = (Long) map.get("id");
    String name = (String) map.get("name");

    // Convert the participants list
    List<Map<String, Object>> participantsList = mapper.convertValue(map.get("participants"), new TypeReference<List<Map<String, Object>>>() {});
    List<TournamentParticipantDetailDto> participants = new ArrayList<>();
    for (Map<String, Object> participantMap : participantsList) {
      Long horseId = (Long) participantMap.get("horseId");
      String participantName = (String) participantMap.get("name");
      LocalDate dateOfBirth = LocalDate.parse((String) participantMap.get("dateOfBirth"));
      Integer entryNumber = (Integer) participantMap.get("entryNumber");
      Integer roundReached = (Integer) participantMap.get("roundReached");

      participants.add(new TournamentParticipantDetailDto(horseId, participantName, dateOfBirth, entryNumber, roundReached));
    }

    TournamentStandingsTreeDto tree = (TournamentStandingsTreeDto) map.get("tree");

    // Create and return the TournamentStandingsDto
    return new TournamentStandingsDto(id, name, participants, tree);
  }
}