import {Component, EventEmitter, HostBinding, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-confirm-delete-dialog',
  templateUrl: './confirm-delete-dialog.component.html',
  styleUrls: ['./confirm-delete-dialog.component.scss'],
})
export class ConfirmDeleteDialogComponent implements OnInit {
  @Input() entityType: string = 'Entity';
  @Input() entityName: string = 'Entity Name';
  @Input() confirmButtonText: string = 'Yes';
  @Input() cancelButtonText: string = 'No';
  @Output() confirm = new EventEmitter<void>();
  @HostBinding('class') cssClass = 'modal fade';

  constructor(
  ) {}

  ngOnInit(): void {
  }
}
