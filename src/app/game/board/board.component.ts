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

  constructor(public gameService: GameService) {}

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
}