import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatListItem, MatListItemIcon} from "@angular/material/list";
import {BoardComponent} from "../board/board.component";
import {ActivatedRoute, Router} from "@angular/router";
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

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private sseService: SseService,
              private apiService: ApiService) {
  }

  ngOnInit() {
    this.activatedRoute.queryParamMap.subscribe(paramMap => {
      this.gameId = BigInt(paramMap.get('gameId')!);
      this.namePlayer = paramMap.get('namePlayer') as string;
      this.player = paramMap.get('player') as Player;

      this.game$ = this.sseService.getEvents<Game>(
        `${environment.apiBaseUrl}/ws/game/register/sse/gameId/${this.gameId}/namePlayer/${this.namePlayer}`
      );

      // short disconnections (when pressing F5) will be suppressed
      this.connectionMessage$ = this.game$.pipe(
        map(game => game.connectionMessage),
        debounceTime(2000),
        distinctUntilChanged()
      );
    });
  }

  getInfoMessage(game: Game): string {
    if (game.winner === Player.PLAYER_1) {
      return `${game.namePlayer1} won!`;
    } else if (game.winner === Player.PLAYER_2) {
      return `${game.namePlayer2} won!`;
    }

    if (game.turn === Player.PLAYER_1) {
      return `${this.addApostropheIfNeeded(game.namePlayer1!)} turn`;
    } else if (game.turn === Player.PLAYER_2) {
      return `${this.addApostropheIfNeeded(game.namePlayer2!)} turn`;
    }
    return "";
  }

  private addApostropheIfNeeded(s: string): string {
    if (s.endsWith('s') || s.endsWith('ß') || s.endsWith('x') || s.endsWith('z')) {
      return `${s}'`;
    }
    return `${s}'s`;
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
    return game.winner === null && game.turn === this.player && game.board[row][col].player === null;
  }

  private setMoveOnBoard(game: Game, row: number, col: number, player: Player): void {
    game.board[row][col].player = player;
    for (let i = 0; i < game.board.length; i++) {
      for (let j = 0; j < game.board[0].length; j++) {
        game.board[i][j].lastMove = false;
      }
    }
    game.board[row][col].lastMove = true;
    game.turn = player == Player.PLAYER_1 ? Player.PLAYER_2 : Player.PLAYER_1;
  }

  quitClick(): void {
    sessionStorage.removeItem('gameId');
    sessionStorage.removeItem('player');
    this.router.navigate(['/']).then();
  }

}
