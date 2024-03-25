import {Component, OnInit} from '@angular/core';
import {TournamentStandingsDto} from "../../../dto/tournament";
import {TournamentService} from "../../../service/tournament.service";
import {ActivatedRoute} from "@angular/router";
import {NgForm} from "@angular/forms";
import {Location} from "@angular/common";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../service/error-formatter.service";

@Component({
  selector: 'app-tournament-standings',
  templateUrl: './tournament-standings.component.html',
  styleUrls: ['./tournament-standings.component.scss']
})
export class TournamentStandingsComponent implements OnInit {
  standings: TournamentStandingsDto | undefined;

  public constructor(
    private service: TournamentService,
    private errorFormatter: ErrorFormatterService,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private location: Location,
  ) {
  }

  public ngOnInit() {
    const routeParams = this.route.snapshot.paramMap;
    const id = Number(routeParams.get('id'));
    this.service.getStandings(id)
      .subscribe({
        next: data => {
          this.standings = data;
        },
        error: error => {
          console.error('Error fetching tournament standings', error);
          this.notification.error(error.message.message, 'Could Not Fetch Tournament Standings');
        }
      })
  }

  public submit(form: NgForm) {
    const routeParams = this.route.snapshot.paramMap;
    const id = Number(routeParams.get('id'));
    if (this.standings !== undefined) {
      this.service.updateStandings(id, this.standings)
        .subscribe({
          next: data => {
            this.standings = data;
          },
          error: error => {
            console.error('Error updating tournament standings', error);
            this.notification.error(error.message.message, 'Could Not Update Tournament Standings');
          }
        })
    }
  }

  public generateFirstRound() {
    if (!this.standings)
      return;
    // TODO implement
  }
}
