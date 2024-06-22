import {Component, OnInit} from '@angular/core';
import {CommonModule} from "@angular/common";
import {HexagonComponent} from "../hexagon/hexagon.component";
import {GameService} from "../shared/game.service";

@Component({
  selector: 'app-board',
  standalone: true,
  imports: [
    CommonModule,
    HexagonComponent
  ],
  templateUrl: './board.component.html',
  styleUrl: './board.component.scss'
})

export class BoardComponent implements OnInit {

  grid: any[] = [];
  SVG_WIDTH: number = 1030;
  SVG_HEIGHT: number = 640;

  constructor(public gameService: GameService) {
  }

  ngOnInit() {
    this.createGrid();
  }

  createGrid() {
    const size = 11; // Größe des Spielfelds
    for (let row = 0; row < size; row++) {
      this.grid[row] = [];
      for (let col = 0; col < size; col++) {
        this.grid[row].push({});
      }
    }
  }

  numToLetter(num: number): string {
    return String.fromCharCode(64 + num);
  }

  polygonPoints(i: number): string {
    let SVG_START: number = 0;
    let SVG_HALF_WIDTH: number = this.SVG_WIDTH / 2;
    let SVG_HALF_HEIGHT: number = this.SVG_HEIGHT / 2;
    if (i === 0) {
      return `${SVG_START},${SVG_START} ${this.SVG_WIDTH} ${SVG_START} ${SVG_HALF_WIDTH},${SVG_HALF_HEIGHT}`;
    } else if (i === 1) {
      return `${this.SVG_WIDTH},${SVG_START} ${this.SVG_WIDTH},${this.SVG_HEIGHT} ${SVG_HALF_WIDTH},${SVG_HALF_HEIGHT}`;
    } else if (i === 2) {
      return `${SVG_START},${this.SVG_HEIGHT} ${SVG_HALF_WIDTH},${SVG_HALF_HEIGHT} ${this.SVG_WIDTH},${this.SVG_HEIGHT}`;
    } else {
      return `${SVG_START},${SVG_START} ${SVG_HALF_WIDTH},${SVG_HALF_HEIGHT} ${SVG_START},${this.SVG_HEIGHT}`;
    }
  }

}
