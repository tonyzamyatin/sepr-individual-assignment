import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {map, Observable, tap} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse, HorseListDto} from '../dto/horse';
import {HorseSearch} from '../dto/horse';
import {formatIsoDate} from '../util/date-helper';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) { }

  getById(id: number): Observable<Horse> {
    return this.http.get<Horse>(`${baseUri}/${id}`);
  }

  search(searchParams: HorseSearch): Observable<HorseListDto[]> {
    if (searchParams.name === '') {
      delete searchParams.name;
    }
    let params = new HttpParams();
    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }
    if (searchParams.sex) {
      params = params.append('sex', searchParams.sex);
    }
    if (searchParams.bornEarliest) {
      params = params.append('bornEarliest', formatIsoDate(searchParams.bornEarliest));
    }
    if (searchParams.bornLastest) {
      params = params.append('bornLatest', formatIsoDate(searchParams.bornLastest));
    }
    if (searchParams.breedName) {
      params = params.append('breed', searchParams.breedName);
    }
    if (searchParams.limit) {
      params = params.append('limit', searchParams.limit);
    }
    return this.http.get<HorseListDto[]>(baseUri, { params })
      .pipe(tap(horses => horses.map(h => {
        h.dateOfBirth = new Date(h.dateOfBirth); // Parse date string
      })));
  }

  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: Horse): Observable<Horse> {
    return this.http.post<Horse>(
      baseUri,
      horse
    );
  }


  /**
   * Update existing horse in the system.
   *
   * @param horse the data for the horse that should be updated (incl. id)
   * @return an Observable for the updated horse
   */
  update(horse: Horse): Observable<Horse> {
    return this.http.put<Horse>(
      `${baseUri}/${horse.id}`,
      horse
    );
  }

  /**
   * Delete the horse with the given id.
   *
   * @param horseId the id of the horse to be deleted.
   * @return an empty Observable
   */
  delete(horseId: number): Observable<void> {
    return this.http.delete<void>(`${baseUri}/${horseId}`)
  }
}
