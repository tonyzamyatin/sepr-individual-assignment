import {Component, OnInit} from '@angular/core';
import {TournamentStandingsDto, TournamentStandingsTreeDto} from "../../../dto/tournament";
import {TournamentService} from "../../../service/tournament.service";
import {ActivatedRoute, Router} from "@angular/router";
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
    private router: Router,
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
    if (this.standings !== undefined) {
      this.service.updateStandings(this.standings)
        .subscribe({
          next: data => {
            this.standings = data;
            this.notification.success( `Tournament standings of ${this.standings.name} successfully updated`);
            this.router.navigate(['/tournaments']);
          },
          error: error => {
            console.error('Error updating tournament standings', error);
            this.notification.error(error.message.message, 'Could Not Update Tournament Standings');
          }
        })
    }
  }

  public generateFirstRoundMatches() {
    const routeParams = this.route.snapshot.paramMap;
    const id = Number(routeParams.get('id'));
    this.service.generateFirstRound(id)
      .subscribe({
        next: data => {
          this.standings = data;
        },
        error: error => {
          console.error('Error generating first round', error);
          this.notification.error(error.message.message, 'Could Not Generate First Round');
        }
      })
  }
}
