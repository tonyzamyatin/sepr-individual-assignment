import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {map, Observable, tap, throwError} from 'rxjs';
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

  search(searchParams: TournamentSearchParams): Observable<TournamentListDto[]> {
    if (searchParams.name === '') {
      delete searchParams.name;
    }
    let params = new HttpParams();
    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }
    if (searchParams.intervalStart) {
      params = params.append('intervalStart', formatIsoDate(searchParams.intervalStart));
    }
    if (searchParams.intervalEnd) {
      params = params.append('intervalEnd', formatIsoDate(searchParams.intervalEnd));
    }
    if (searchParams.limit) {
      params = params.append('limit', searchParams.limit);
    }
    return this.http.get<TournamentListDto[]>(baseUri, { params })
      .pipe(tap(tournaments => tournaments.map(t => {
        t.startDate = new Date(t.startDate);
        t.endDate = new Date(t.endDate);
      })));
  }

  create(tournament: TournamentCreateDto): Observable<TournamentDetailDto> {
    return this.http.post<TournamentDetailDto>(
      `${baseUri}/create`,
      tournament
    );
  }


}
