import {Component, OnInit} from '@angular/core';
import {NgForm, NgModel} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Observable, of, retry} from 'rxjs';
import {Horse} from 'src/app/dto/horse';
import {Sex} from 'src/app/dto/sex';
import {HorseService} from 'src/app/service/horse.service';
import {Breed} from "../../../dto/breed";
import {BreedService} from "../../../service/breed.service";
import {ErrorFormatterService} from "../../../service/error-formatter.service";


export enum HorseCreateEditMode {
  create,
  edit,
  view
}

@Component({
  selector: 'app-horse-create-edit',
  templateUrl: './horse-create-edit.component.html',
  styleUrls: ['./horse-create-edit.component.scss']
})
export class HorseCreateEditComponent implements OnInit {

  mode: HorseCreateEditMode = HorseCreateEditMode.create;
  horseId: number | undefined;
  initialHorse: Horse | undefined;
  horse: Horse = {
    name: '',
    sex: Sex.female,
    dateOfBirth: new Date(), // TODO this is bad
    height: 1.50, // TODO this is bad
    weight: 500, // TODO this is bad
    breed: undefined,
  };
  sexes = [
    { value: 'FEMALE', viewValue: 'Female' },
    { value: 'MALE', viewValue: 'Male' }
  ];

  private sexSet: boolean = false;
  private heightSet: boolean = false;
  private weightSet: boolean = false;
  private dateOfBirthSet: boolean = false;

  get sex(): string {
    return this.sexSet ? this.horse.sex.toString() : '';
  }

  set sex(value: string) {
    if (value) {
      this.sexSet = true;
      this.horse.sex = value as Sex; // Assuming `value` will be either 'Male' or 'Female'
    }
  }


  get height(): number | null {
    return this.heightSet
      ? this.horse.height
      : null;
  }

  set height(value: number) {
    this.heightSet = true;
    this.horse.height = value;
  }

  get weight(): number | null {
    return this.weightSet
      ? this.horse.weight
      : null;
  }

  set weight(value: number) {
    this.weightSet = true;
    this.horse.weight = value;
  }

  get dateOfBirth(): Date | null {
    return this.dateOfBirthSet
      ? this.horse.dateOfBirth
      : null;
  }

  set dateOfBirth(value: Date) {
    this.dateOfBirthSet = true;
    this.horse.dateOfBirth = value;
  }


  constructor(
    private service: HorseService,
    private breedService: BreedService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create New Horse';
      case HorseCreateEditMode.edit:
        return `Edit Horse Details`
      case HorseCreateEditMode.view:
        return `Horse Details`
      default:
        return '?';
    }
  }


  public get submitButtonText(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create';
      case HorseCreateEditMode.edit:
        return 'Submit'
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === HorseCreateEditMode.create;
  }

  get modeIsView(): boolean {
    return this.mode === HorseCreateEditMode.view;
  }

  get modeIsEdit(): boolean {
    return this.mode === HorseCreateEditMode.edit;
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'created';
      case HorseCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  private initializeFormFields(horse: Horse): void {
    this.initialHorse = {...horse}   // copy
    this.sex = horse.sex ?? null;
    this.height = horse.height ?? null;
    this.weight = horse.weight ?? null;
    this.dateOfBirth = horse.dateOfBirth ?? new Date();

    // Mark as set to handle logic in your getters and setters
    this.sexSet = horse.sex != null
    this.heightSet = horse.height != null;
    this.weightSet = horse.weight != null;
    this.dateOfBirthSet = horse.dateOfBirth != null;
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
      if (!this.modeIsCreate) {
        this.horseId = Number(this.route.snapshot.paramMap.get('id'));
        this.service.getById(this.horseId).subscribe({
          next: retrievedHorse => {
          this.horse = retrievedHorse;
          this.initializeFormFields(retrievedHorse)
          },
          error: error => {
            console.error(`Error getting horse with id ${this.horseId}`, error);
            // TODO show an error message to the user. Include and sensibly present the info from the backend!
            this.notification.error(this.errorFormatter.format(error), `Could Not Get Horse With Id ${this.horseId}`, {
              enableHtml: true,
              timeOut: 10000,
            });
          }
        })
      }
    })
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public formatBreedName(breed: Breed | null): string {
    return breed?.name ?? '';
  }

  breedSuggestions = (input: string) => (input === '')
    ? of([])
    :  this.breedService.breedsByName(input, 5);

  public hasFormChanges(): boolean {
    return JSON.stringify(this.initialHorse) !== JSON.stringify(this.horse);
  }

  confirmDelete(): void {
    if (this.horse && this.horse.id !== undefined) {
      // Call the service to delete the horse, then navigate or show a message
      this.service.delete(this.horse.id).subscribe({
        next: () => {
          this.router.navigate(['/horses']);
          this.notification.success('Horse successfully deleted.');
        },
        error: error => {
          console.error('Error deleting horse', error);
          this.notification.error('Error deleting horse');
          // TODO show an error message to the user. Include and sensibly present the info from the backend!
          this.notification.error(this.errorFormatter.format(error), `Could Not Delete Horse With Id ${this.horseId}`, {
            enableHtml: true,
            timeOut: 10000,
          });
        }
      });
    }
  }

  public submit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.horse);
    if (form.valid) {
      let observable: Observable<Horse>;
      switch (this.mode) {
        case HorseCreateEditMode.create:
          observable = this.service.create(this.horse);
          break;
        case HorseCreateEditMode.edit:
          if (!this.hasFormChanges()) {
            console.log('No changes detected.');
            return;
          }
          observable = this.service.update(this.horse);
        default:
          console.error('Unknown HorseCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/horses']);
        },
        error: error => {
          console.error('Error creating horse', error);
          // TODO show an error message to the user. Include and sensibly present the info from the backend!
          this.notification.error(this.errorFormatter.format(error), `Could Not Create Horse`, {
            enableHtml: true,
            timeOut: 10000,
          });
        }
      });
    }
  }

}
