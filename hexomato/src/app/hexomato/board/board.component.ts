import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CommonModule} from "@angular/common";
import {HexagonComponent} from "../hexagon/hexagon.component";
import {GameService} from "../shared/game.service";
import {Node} from "../shared/node";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatListItem, MatListItemIcon} from "@angular/material/list";
import {HexagonClickEvent} from "../shared/hexagon-click-event";

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

export class BoardComponent {

  @Input() board!: Node[][];
  @Output() clicked = new EventEmitter<HexagonClickEvent>();

  constructor(public gameService: GameService) {
  }

  numToLetter(num: number): string {
    return String.fromCharCode(64 + num);
  }

  polygonPoints(i: number): string {
    const X1 = 20;
    const Y1 = 30;
    const X2 = 40;
    const Y2 = 0;
    const X3 = 725;
    const Y3 = 0;
    const X4 = 535;
    const Y4 = 320;

    const X5 = 1070;
    const Y5 = 580;
    const X6 = 1050;
    const Y6 = 610;

    const X7 = 340;
    const Y7 = 640;
    const X8 = 1030;
    const Y8 = 640;

    const X9 = 0;
    const Y9 = 60;

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

  hexagonClicked(row: number, col: number) {
    this.clicked.emit({row, col});
  }

}
