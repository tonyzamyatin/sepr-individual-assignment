import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {map, Observable, throwError} from 'rxjs';
import {formatIsoDate} from '../util/date-helper';
import {
  TournamentDetailDto, TournamentCreateDto, TournamentDetailParticipantDto,
  TournamentListDto,
  TournamentSearchParams,
  TournamentStandingsDto, TournamentStandingsTreeDto
} from "../dto/tournament";

const baseUri = environment.backendUrl + '/tournaments';

class ErrorDto {
  constructor(public message: String) {
  }
}

@Injectable({
  providedIn: 'root'
})
export class TournamentService {
  constructor(
    private http: HttpClient
  ) {
  }


  public create(tournament: TournamentCreateDto): Observable<TournamentDetailDto> {
    return this.http.post<TournamentDetailDto>(
      `${baseUri}/create`,
      tournament
    );
  }


}
