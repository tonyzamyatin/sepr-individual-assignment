package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.mapper.ParticipantMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentParticipantDao;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentParticipantService;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
public class TournamentParticipantServiceImpl implements TournamentParticipantService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentService tournamentService;
  private final HorseService horseService;
  private final TournamentParticipantDao participantDao;
  private ParticipantMapper mapper;

  public TournamentParticipantServiceImpl(@Lazy TournamentService tournamentService, @Lazy HorseService horseService, TournamentParticipantDao participantDao,
                                          ParticipantMapper mapper) {
    this.tournamentService = tournamentService;
    this.horseService = horseService;
    this.participantDao = participantDao;
    this.mapper = mapper;
  }

  @Override
  public boolean isHorseParticipantInAnyTournament(long horseId) {
    LOG.trace("isHorseParticipantInAnyTournament({})", horseId);
    return participantDao.isHorseParticipantInAnyTournament(horseId);
  }

  @Override
  public List<ParticipantDetailDto> findParticipantsByTournamentId(long tournamentId) {
    LOG.trace("findParticipantsByTournamentId({})", tournamentId);
    return participantDao.findParticipantsByTournamentId(tournamentId).stream().map(mapper::entityToDetailDto).toList();
  }

  @Override
  public List<ParticipantDetailDto> searchParticipants(ParticipantSearchDto searchParams) {
    LOG.trace("searchParticipants({})", searchParams);
    return participantDao.search(searchParams).stream().map(mapper::entityToDetailDto).toList();
  }

  @Override
  public ParticipantDetailDto getParticipant(long tournamentId, long horseId) throws NotFoundException {
    LOG.trace("getParticipant({}, {})", tournamentId, horseId);
    Participant participant = participantDao.getParticipant(tournamentId, horseId);
    return mapper.entityToDetailDto(participant);
  }

  @Override
  public ParticipantDetailDto create(long tournamentId, ParticipantDetailDto participant) throws ConflictException {
    LOG.trace("create({}, {})", tournamentId, participant);
    List<String> conflictErrors = new ArrayList<>();

    // Check for consistency of provided data with system data
    try {
      HorseDetailDto horse = horseService.getById(participant.horseId());
      if (!horse.dateOfBirth().equals(participant.dateOfBirth())) {
        conflictErrors.add("Participant's date of birth with horse id " + participant.horseId() + " does not match the date of birth of horse; actual: "
            + participant.dateOfBirth() + " , expected: " + horse.dateOfBirth());
      }
      if (!horse.name().equals(participant.name())) {
        conflictErrors.add(
            "Participant's name with horse id " + participant.horseId() + " does not match the name of horse; actual: " + participant.name()
                + " , expected: " + horse.name());
      }
    } catch (NotFoundException e) {
      conflictErrors.add("Horse with id " + participant.horseId() + " does not exist");
    }

    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Participant data is in conflict with system data", conflictErrors);
    }

    Participant createdEntity = participantDao.create(tournamentId, participant);
    return mapper.entityToDetailDto(createdEntity);
  }

  @Override
  public ParticipantDetailDto update(long tournamentId, ParticipantDetailDto participant) throws NotFoundException {
    LOG.trace("update({}, {})", tournamentId, participant);
    Participant updatedEntity = participantDao.update(tournamentId, participant);
    return mapper.entityToDetailDto(updatedEntity);
  }
}
