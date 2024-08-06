import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatListItem, MatListItemIcon} from "@angular/material/list";
import {BoardComponent} from "../board/board.component";
import {Router} from "@angular/router";
import {environment} from "../../../environments/environment";
import {debounceTime, distinctUntilChanged, map, Observable} from "rxjs";
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
  connectionMessage$?: Observable<string>;
  gameId!: bigint;
  player!: Player;
  namePlayer!: string;
  destroyRef: DestroyRef = inject(DestroyRef);

  constructor(private router: Router, private sseService: SseService, private apiService: ApiService) {
  }

  ngOnInit() {
    this.gameId = BigInt(sessionStorage.getItem('gameId')!);
    this.player = sessionStorage.getItem('player') as Player;
    this.namePlayer = sessionStorage.getItem('namePlayer') as string;

    this.game$ = this.sseService.getEvents<Game>(
      `${environment.apiBaseUrl}/ws/game/register/sse/gameId/${this.gameId}/namePlayer/${this.namePlayer}`
    );

    this.connectionMessage$ = this.game$.pipe(
      map(game => game.connectionMessage),
      distinctUntilChanged(),
      debounceTime(3000)
    );
  }

  hexagonClicked(game: Game, event: HexagonClickEvent): void {
    if (!this.isValidMove(game, event.row, event.col)) {
      return;
    }
    this.setMoveOnBoard(game, event.row, event.col, this.player);

    this.apiService.makeMove(this.gameId, event.row, event.col, this.player).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe();
  }

  private isValidMove(game: Game, row: number, col: number): boolean {
    return game.turn === this.player && game.board[row][col].player === null;
  }

  private setMoveOnBoard(game: Game, row: number, col: number, player: Player): void {
    game.board[row][col].player = player;
    for (let i = 0; i < game.board.length; i++) {
      for (let j = 0; j < game.board[0].length; j++) {
        game.board[i][j].lastMove = false;
      }
    }
    game.board[row][col].lastMove = true;
  }

  quitClick(): void {
    sessionStorage.removeItem('gameId');
    sessionStorage.removeItem('player');
    this.router.navigate(['/']).then();
  }

}
