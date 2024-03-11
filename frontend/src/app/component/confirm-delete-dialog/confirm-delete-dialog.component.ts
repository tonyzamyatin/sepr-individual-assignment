import {Component, EventEmitter, HostBinding, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-confirm-delete-dialog',
  templateUrl: './confirm-delete-dialog.component.html',
  styleUrls: ['./confirm-delete-dialog.component.scss'],
})
export class ConfirmDeleteDialogComponent implements OnInit {

  @Input() title: string = 'Confirm Action';
  @Input() message: string = 'Do you want to proceed?';
  @Input() confirmButtonText: string = 'Yes';
  @Input() cancelButtonText: string = 'No';
  @Output() confirm = new EventEmitter<boolean>();

  @HostBinding('class') cssClass = 'modal fade';

  constructor() {}

  ngOnInit(): void {
    console.log("Delete dialog rendered")
  }

  onConfirm(): void {
    this.confirm.emit(true);
  }

  onCancel(): void {
    this.confirm.emit(false);
  }

}
