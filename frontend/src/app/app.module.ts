import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {ToastrModule} from 'ngx-toastr';

import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {AutocompleteComponent} from './component/autocomplete/autocomplete.component';
import {HeaderComponent} from './component/header/header.component';
import {HorseCreateEditComponent} from './component/horse/horse-create-edit/horse-create-edit.component';
import {HorseComponent} from './component/horse/horse.component';
import {ConfirmActionDialogComponent} from './component/confirm-action-dialog/confirm-action-dialog.component';
import {TournamentCreateComponent} from './component/tournament/tournament-create/tournament-create.component';
import {TournamentStandingsComponent} from './component/tournament/tournament-standings/tournament-standings.component';
import {TournamentStandingsBranchComponent} from './component/tournament/tournament-standings/tournament-standings-branch/tournament-standings-branch.component';
// import {NgbModule} from "@ng-bootstrap/ng-bootstrap";

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    HorseComponent,
    HorseCreateEditComponent,
    AutocompleteComponent,
    ConfirmActionDialogComponent,
    TournamentCreateComponent,
    TournamentStandingsComponent,
    TournamentStandingsBranchComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ToastrModule.forRoot(),
    BrowserAnimationsModule,
    // NgbModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
