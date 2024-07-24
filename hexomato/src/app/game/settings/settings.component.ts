import {Component} from '@angular/core';
import {MatButtonModule} from "@angular/material/button";
import {ApiService} from "../shared/api.service";
import {EMPTY, Observable} from "rxjs";
import {AsyncPipe} from "@angular/common";

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [MatButtonModule, AsyncPipe],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent {

  createGame$: Observable<bigint> = EMPTY;

  constructor(public apiService: ApiService) {
  }

  initGame() {
    this.createGame$ = this.apiService.createGame('PLAYER_1', 'Hugo');
  }
}
