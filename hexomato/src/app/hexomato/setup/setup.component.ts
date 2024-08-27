import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {MatButtonModule} from "@angular/material/button";
import {ApiService} from "../shared/api.service";
import {BehaviorSubject, combineLatest, map, Observable, tap} from "rxjs";
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
import {Router} from "@angular/router";

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, ReactiveFormsModule, MatInputModule, MatListItemIcon,
    MatListItem, MatTableModule, MatTooltip, CommonModule],
  templateUrl: './setup.component.html',
  styleUrl: './setup.component.scss'
})
export class SetupComponent implements OnInit {

  form: FormGroup = this.fb.group({name: ['']});
  name$: BehaviorSubject<string> = new BehaviorSubject<string>('');
  anyButtonClicked$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  buttonsDisabled$?: Observable<boolean>;
  games$?: Observable<Game[]>;
  displayedColumns: string[] = ['namePlayer1', 'namePlayer2'];
  destroyRef: DestroyRef = inject(DestroyRef);

  constructor(private router: Router, private fb: FormBuilder, private apiService: ApiService,
              private sseService: SseService) {
  }

  ngOnInit() {
    this.form.get('name')?.valueChanges.pipe().subscribe(value => this.name$.next(value));
    this.form.get('name')?.setValue(sessionStorage.getItem('namePlayer'));

    this.buttonsDisabled$ = combineLatest([
      this.name$,
      this.anyButtonClicked$
    ]).pipe(
      map(([name, anyButtonClicked]) => name.length === 0 || anyButtonClicked)
    );

    this.games$ = this.sseService.getEvents<Game[]>(`${environment.apiBaseUrl}/ws/setup/register/sse`).pipe(
      tap(games => games.find(game =>
        this.findGameReadyForPlaying(game) && this.navigateToGame()
      )),
      map(games => games.filter(game => game.namePlayer1 == null || game.namePlayer2 == null))
    );
  }

  private findGameReadyForPlaying(game: Game) {
    const storedGameId = sessionStorage.getItem('gameId');
    return game.id.toString() === storedGameId && game.namePlayer1 !== null && game.namePlayer2 !== null;
  }

  createGameClick(player: Player): void {
    this.apiService.createGame(player, this.name$.getValue()).pipe(
      takeUntilDestroyed(this.destroyRef),
    ).subscribe((gameId) => {
      sessionStorage.setItem('gameId', gameId.toString());
      sessionStorage.setItem('namePlayer', this.name$.getValue());
      sessionStorage.setItem('player', player);
    });
    this.anyButtonClicked$.next(true);
  }

  joinGameClick(gameId: bigint, player: Player): void {
    this.apiService.joinGame(gameId, player, this.name$.getValue()).pipe(
      takeUntilDestroyed(this.destroyRef),
    ).subscribe(() => {
        sessionStorage.setItem('gameId', gameId.toString());
        sessionStorage.setItem('namePlayer', this.name$.getValue());
        sessionStorage.setItem('player', player);
        this.navigateToGame();
      }
    );

    this.anyButtonClicked$.next(true);
  }

  navigateToGame(): void {
    this.router.navigate(['/game'],
      {
        queryParams: {
          gameId: sessionStorage.getItem('gameId'),
          namePlayer: sessionStorage.getItem('namePlayer'),
          player: sessionStorage.getItem('player')
        }
      }
    ).then();
  }

  protected readonly Player = Player;
}
