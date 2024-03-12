import {Component, EventEmitter, HostBinding, Input, OnInit, Output} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

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
  @HostBinding('class') cssClass = 'modal fade';

  constructor(
    public activeModal: NgbActiveModal
  ) {}

  ngOnInit(): void {
    console.log("Delete dialog rendered")
  }


}
