import {Component, OnInit} from '@angular/core';
import {TournamentListDto, TournamentSearchParams} from "../../dto/tournament";
import {debounce, debounceTime, Subject} from "rxjs";
import {TournamentService} from "../../service/tournament.service";
import {ToastrService} from "ngx-toastr";
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-tournament',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    RouterLink
  ],
  templateUrl: './tournament.component.html',
  styleUrl: './tournament.component.scss'
})
export class TournamentComponent implements OnInit {

  tournaments: TournamentListDto[] = [];
  bannerError: string | null = null;
  searchParams: TournamentSearchParams = {};
  searchPeriodStart: string | null = null;
  searchPeriodEnd: string | null = null;
  searchChangeObservable = new Subject<void>();

  constructor(
    private service: TournamentService,
    private notification: ToastrService,
  ) { }


  ngOnInit() {
    this.reloadTournaments();
    this.searchChangeObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadTournaments()});
  }

  reloadTournaments() {
    if (this.searchPeriodStart == null || this.searchPeriodStart === "") {
      delete this.searchParams.intervalStart;
    } else {
      this.searchParams.intervalStart = new Date(this.searchPeriodStart);
    }
    if (this.searchPeriodEnd == null || this.searchPeriodEnd === "") {
      delete this.searchParams.intervalEnd;
    } else {
      this.searchParams.intervalEnd = new Date(this.searchPeriodEnd);
    }
    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.tournaments = data;
        },
        error: error => {
          console.error('Error fetching tournaments', error);
          this.bannerError = 'Could not fetch tournaments: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Tournaments');
        }
      });
  }

  searchChanged(): void {
    this.searchChangeObservable.next();
  }
}
