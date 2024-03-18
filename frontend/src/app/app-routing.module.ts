import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {
  HorseCreateEditComponent,
  HorseCreateEditMode
} from './component/horse/horse-create-edit/horse-create-edit.component';
import {HorseComponent} from './component/horse/horse.component';
import {TournamentCreateComponent} from "./component/tournament/tournament-create/tournament-create.component";
import {TournamentStandingsComponent} from "./component/tournament/tournament-standings/tournament-standings.component";
import {TournamentComponent} from "./component/tournament/tournament.component";

const ROUTES: Routes = [
  {path: '', redirectTo: 'horses', pathMatch: 'full'},
  {
    path: 'horses', children: [
      {path: '', component: HorseComponent},
      {path: 'create', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.create}},
      {path: 'edit/:id', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.edit}},
      {path: 'view/:id', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.view}}
    ]
  },
  {
    path: 'tournaments', children: [
      {path: '', component: TournamentComponent},
      {path: 'create', component: TournamentCreateComponent},
      {path: 'standings/:id', component: TournamentStandingsComponent}
    ]
  },
  {path: '**', redirectTo: 'horses'},
];

@NgModule({
  imports: [RouterModule.forRoot(ROUTES)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
