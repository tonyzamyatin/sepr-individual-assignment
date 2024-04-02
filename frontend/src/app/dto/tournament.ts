import {HorseSelection} from "./horse";

export interface TournamentSearchParams {
  name?: string;
  intervalStart?: Date;
  intervalEnd?: Date;
  limit?: number;
}

export interface TournamentListDto {
  id: number;
  name: string;
  startDate: Date;
  endDate: Date;
}

export interface TournamentDetailDto {
  id?: number;
  name: string;
  startDate: Date;
  endDate: Date;
  participants: TournamentDetailParticipantDto[];
}

export interface TournamentDetailParticipantDto {
  horseId: number;
  name: string;
  dateOfBirth: Date;
  entryNumber: number;
  roundReached: number;
}

export interface TournamentStandingsTreeDto {
  thisParticipant: TournamentDetailParticipantDto | null;
  branches?: TournamentStandingsTreeDto[];
}


export interface TournamentStandingsDto {
  id: number;
  name: string;
  participants: TournamentDetailParticipantDto[];
  tree: TournamentStandingsTreeDto;
}
