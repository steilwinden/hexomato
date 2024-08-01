import {Component, Input} from '@angular/core';
import {GameService} from "../shared/game.service";
import {Node} from "../shared/node";
import {Player} from "../shared/player.enum";

@Component({
  selector: 'app-hexagon',
  standalone: true,
  imports: [],
  templateUrl: './hexagon.component.html',
  styleUrl: './hexagon.component.scss'
})
export class HexagonComponent {

  @Input() hexNode!: Node;

  constructor(public gameService: GameService) {
  }

  getContentSvgClass() {
    const classes = ['content-svg'];
    if (this.hexNode.player === null) {
      classes.push('empty');
    } else if (this.hexNode.player === Player.PLAYER_1) {
      classes.push('player-1');
    } else if (this.hexNode.player === Player.PLAYER_2) {
      classes.push('player-2');
    }

    if (this.hexNode.lastMove) {
      classes.push('lastMove');
    }
    if (this.hexNode.partOfWinnerPath) {
      classes.push('blink');
    }
    return classes.join(' ');
  }

}
