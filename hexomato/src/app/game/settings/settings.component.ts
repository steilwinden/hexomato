import {Component} from '@angular/core';
import {MatButtonModule} from "@angular/material/button";
import {ApiService} from "../shared/api.service";
import {BehaviorSubject, combineLatest, EMPTY, map, Observable, of, startWith} from "rxjs";
import {CommonModule} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {MatListItem, MatListItemIcon} from "@angular/material/list";
import {Player} from "../shared/player.enum";
import {Game} from "../shared/game";
import {MatTableModule} from "@angular/material/table";
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, ReactiveFormsModule, MatInputModule, MatListItemIcon,
    MatListItem, MatTableModule, MatTooltip, CommonModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent {

  form: FormGroup = this.fb.group({playerName: ['']});
  playerName$: Observable<string>;
  anyButtonClicked$: BehaviorSubject<boolean>;
  buttonsDisabled$: Observable<boolean>;
  createGame$: Observable<bigint> = EMPTY;
  games$: Observable<Game[]>;
  displayedColumns: string[] = ['namePlayer1', 'namePlayer2'];


  constructor(private fb: FormBuilder, private apiService: ApiService) {
    this.playerName$ = this.form.get('playerName')!.valueChanges.pipe(
      startWith('')
    );
    this.anyButtonClicked$ = new BehaviorSubject<boolean>(false);
    this.buttonsDisabled$ = combineLatest([
      this.playerName$,
      this.anyButtonClicked$
    ]).pipe(
      map(([playerName, anyButtonClicked]) => playerName.length === 0 || anyButtonClicked)
    );
    this.games$ = of();
  }

  onButtonClick(event: MouseEvent): void {
    this.anyButtonClicked$.next(true);
  }

  initGame() {
    this.createGame$ = this.apiService.createGame(Player.PLAYER_1, 'Name');
  }
}
