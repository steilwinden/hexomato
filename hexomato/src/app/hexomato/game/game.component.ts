import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatListItem, MatListItemIcon} from "@angular/material/list";
import {BoardComponent} from "../board/board.component";
import {Router} from "@angular/router";
import {environment} from "../../../environments/environment";
import {Observable} from "rxjs";
import {SseService} from "../shared/sse.service";
import {Game} from "../shared/game";
import {CommonModule} from "@angular/common";
import {ApiService} from "../shared/api.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {Player} from "../shared/player.enum";
import {HexagonClickEvent} from "../shared/hexagon-click-event";

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [
    MatButton,
    MatIcon,
    MatListItem,
    MatListItemIcon,
    BoardComponent,
    CommonModule
  ],
  templateUrl: './game.component.html',
  styleUrl: './game.component.scss'
})
export class GameComponent implements OnInit {

  game$?: Observable<Game>;
  gameId!: bigint;
  player!: Player;
  destroyRef: DestroyRef = inject(DestroyRef);

  constructor(private router: Router, private sseService: SseService, private apiService: ApiService) {
  }

  ngOnInit() {
    this.gameId = BigInt(sessionStorage.getItem('gameId')!);
    this.player = sessionStorage.getItem('player') as Player;

    this.game$ = this.sseService.getEvents<Game>(`${environment.apiBaseUrl}/ws/game/register/sse/gameId/${this.gameId}`);
    this.apiService.startGame(this.gameId).pipe(takeUntilDestroyed(this.destroyRef)).subscribe();
  }

  quitClick(): void {
    sessionStorage.removeItem('gameId');
    sessionStorage.removeItem('player');
    this.router.navigate(['/']).then();
  }

  hexagonClicked(game: Game, event: HexagonClickEvent) {
    this.apiService.makeMove(this.gameId, event.row, event.col, this.player).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => {
      game.board[event.row][event.col].player = this.player;
    });
  }
}
