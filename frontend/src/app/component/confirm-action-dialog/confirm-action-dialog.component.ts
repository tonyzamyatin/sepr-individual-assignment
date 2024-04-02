import {Component, EventEmitter, HostBinding, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-confirm-action-dialog',
  templateUrl: './confirm-action-dialog.component.html',
  styleUrls: ['./confirm-action-dialog.component.scss'],
})
export class ConfirmActionDialogComponent implements OnInit {
  @Input() action: string = 'do it, Annikin! Do it!';
  @Input() modalTitle: string = 'Confirm Action';
  @Input() modalMessage: string = 'Do you really want to do this?';
  @Output() confirm = new EventEmitter<void>();
  @HostBinding('class') cssClass = 'modal fade';

  constructor(
  ) {}

  ngOnInit(): void {
  }
}
