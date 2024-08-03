import {Component, DestroyRef, inject, OnInit} from '@angular/core';
import {CommonModule} from "@angular/common";
import {HexagonComponent} from "../hexagon/hexagon.component";
import {GameService} from "../shared/game.service";
import {Node} from "../shared/node";
import {SseService} from "../shared/sse.service";
import {environment} from "../../../environments/environment";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {tap} from "rxjs";
import {Player} from "../shared/player.enum";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatListItem, MatListItemIcon} from "@angular/material/list";

@Component({
  selector: 'app-board',
  standalone: true,
  imports: [
    CommonModule,
    HexagonComponent,
    MatButton,
    MatIcon,
    MatListItem,
    MatListItemIcon,
  ],
  templateUrl: './board.component.html',
  styleUrl: './board.component.scss'
})

export class BoardComponent implements OnInit {

  grid: any[] = [];
  destroyRef: DestroyRef = inject(DestroyRef);

  constructor(private sseService: SseService, public gameService: GameService) {
  }

  ngOnInit() {
    const storedGameId = sessionStorage.getItem('gameId');
    const storedPlayer = sessionStorage.getItem('player');
    console.log("storedGameId: " + storedGameId);
    console.log("storedPlayer: " + storedPlayer);

    this.createGrid();

    this.sseService.getGameEvents(`${environment.apiBaseUrl}/api/register/sse`).pipe(
      takeUntilDestroyed(this.destroyRef),
      tap(e => console.log("sse-event = ", e))
    ).subscribe();
  }

  createGrid() {
    const size = 11; // Größe des Spielfelds
    for (let row = 0; row < size; row++) {
      this.grid[row] = [];
      for (let col = 0; col < size; col++) {
        this.grid[row].push({
          row: 0,
          col: 0,
          lastMove: false,
          partOfWinnerPath: false,
          player: Player.PLAYER_1
        } as Node);
      }
    }
  }

  numToLetter(num: number): string {
    return String.fromCharCode(64 + num);
  }

  polygonPoints(i: number): string {
    let X1 = 20;
    let Y1 = 30;
    let X2 = 40;
    let Y2 = 0;
    let X3 = 725;
    let Y3 = 0;
    let X4 = 535;
    let Y4 = 320;

    let X5 = 1070;
    let Y5 = 580;
    let X6 = 1050;
    let Y6 = 610;

    let X7 = 340;
    let Y7 = 640;
    let X8 = 1030;
    let Y8 = 640;

    let X9 = 0;
    let Y9 = 60;

    if (i === 0) {
      return `${X1},${Y1} ${X2} ${Y2} ${X3},${Y3} ${X4},${Y4}`;
    } else if (i === 1) {
      return `${X4},${Y4} ${X3} ${Y3} ${X5},${Y5} ${X6},${Y6}`;
    } else if (i === 2) {
      return `${X7},${Y7} ${X4},${Y4} ${X6},${Y6} ${X8},${Y8}`;
    } else {
      return `${X7},${Y7} ${X9},${Y9} ${X1},${Y1} ${X4},${Y4}`;
    }
  }

}
