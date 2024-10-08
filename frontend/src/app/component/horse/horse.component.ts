import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {HorseService} from 'src/app/service/horse.service';
import {Horse, HorseListDto} from '../../dto/horse';
import {HorseSearch} from '../../dto/horse';
import {debounceTime, map, Observable, of, Subject} from 'rxjs';
import {BreedService} from "../../service/breed.service";
import {NgbActiveModal, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {ConfirmActionDialogComponent} from "../confirm-action-dialog/confirm-action-dialog.component";


@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {

  horses: HorseListDto[] = [];
  bannerError: string | null = null;
  searchParams: HorseSearch = {};
  searchBornEarliest: string | null = null;
  searchBornLatest: string | null = null;
  horseForDeletion: HorseListDto | undefined;
  searchChangedObservable = new Subject<void>();

  constructor(
    private service: HorseService,
    private breedService: BreedService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadHorses();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadHorses()});
  }

  reloadHorses() {
    if (this.searchBornEarliest == null || this.searchBornEarliest === "") {
      delete this.searchParams.bornEarliest;
    } else {
      this.searchParams.bornEarliest = new Date(this.searchBornEarliest);
    }
    if (this.searchBornLatest == null || this.searchBornLatest === "") {
      delete this.searchParams.bornLastest;
    } else {
      this.searchParams.bornLastest = new Date(this.searchBornLatest);
    }
    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }
  searchChanged(): void {
    this.searchChangedObservable.next();
  }

  breedSuggestions = (input: string): Observable<string[]> =>
    this.breedService.breedsByName(input, 5)
      .pipe(map(bs =>
        bs.map(b => b.name)));

  formatBreedName = (name: string) => name; // It is already the breed name, we just have to give a function to the component

  public onDeleteButtonClick(horse: HorseListDto): void {
    this.horseForDeletion = horse;
  }

  onDeleteConfirmed(): void {
    if (this.horseForDeletion && this.horseForDeletion.id !== undefined) {
      // Call the service to delete the horse, then navigate or show a message
      this.service.delete(this.horseForDeletion.id).subscribe({
        next: () => {
          this.reloadHorses();
          this.notification.success('Horse successfully deleted.');
          // TODO: Navigate away or update the view as necessary
        },
        error: error => {
          console.error('Error deleting horse', error);
          this.notification.error('Error occurred while deleting the horse.');
          // Handle the display of the error to the user
        }
      });
    } else {
      this.notification.error('Error: No horse selected for deletion.');
    }
    this.horseForDeletion = undefined;  // reset variable
  }
}
