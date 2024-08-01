import {Component, DestroyRef, inject} from '@angular/core';
import {MatButtonModule} from "@angular/material/button";
import {ApiService} from "../shared/api.service";
import {BehaviorSubject, combineLatest, map, Observable} from "rxjs";
import {CommonModule} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {MatListItem, MatListItemIcon} from "@angular/material/list";
import {Player} from "../shared/player.enum";
import {Game} from "../shared/game";
import {MatTableModule} from "@angular/material/table";
import {MatTooltip} from "@angular/material/tooltip";
import {environment} from "../../../environments/environment";
import {SseService} from "../shared/sse.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, ReactiveFormsModule, MatInputModule, MatListItemIcon,
    MatListItem, MatTableModule, MatTooltip, CommonModule],
  templateUrl: './setup.component.html',
  styleUrl: './setup.component.scss'
})
export class SetupComponent {

  form: FormGroup = this.fb.group({playerName: ['']});
  playerName$: BehaviorSubject<string> = new BehaviorSubject<string>('');
  anyButtonClicked$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  buttonsDisabled$: Observable<boolean>;
  games$: Observable<Game[]>;
  displayedColumns: string[] = ['namePlayer1', 'namePlayer2'];
  destroyRef: DestroyRef = inject(DestroyRef);


  constructor(private fb: FormBuilder, private apiService: ApiService, private sseService: SseService) {
    this.form.get('playerName')!.valueChanges.pipe()
      .subscribe(value => this.playerName$.next(value));

    this.buttonsDisabled$ = combineLatest([
      this.playerName$,
      this.anyButtonClicked$
    ]).pipe(
      map(([playerName, anyButtonClicked]) => playerName.length === 0 || anyButtonClicked)
    );

    this.games$ = this.sseService.getSetupEvents(`${environment.apiBaseUrl}/ws/setup/register/sse`);
  }

  createGameClick(player: Player): void {
    this.apiService.createGame(player, this.playerName$.getValue()).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe();
    this.anyButtonClicked$.next(true);
  }

  joinGameClick(gameId: bigint): void {
    this.apiService.joinGame(gameId, this.playerName$.getValue()).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe();
    this.anyButtonClicked$.next(true);
  }

  protected readonly Player = Player;
}
