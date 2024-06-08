import {Component} from '@angular/core';
import {GameService} from "../shared/game.service";

@Component({
  selector: 'app-hexagon',
  standalone: true,
  imports: [],
  templateUrl: './hexagon.component.html',
  styleUrl: './hexagon.component.scss'
})
export class HexagonComponent {

  constructor(public gameService: GameService) {}
}
