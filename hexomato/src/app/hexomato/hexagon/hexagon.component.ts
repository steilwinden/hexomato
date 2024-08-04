import {Component, EventEmitter, Input, Output} from '@angular/core';
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

  @Input() node!: Node;
  @Output() clicked = new EventEmitter<Event>();

  constructor(public gameService: GameService) {
  }

  getContentSvgClass() {
    const classes = ['content-svg'];
    if (this.node.player === null) {
      classes.push('empty');
    } else if (this.node.player === Player.PLAYER_1) {
      classes.push('player-1');
    } else if (this.node.player === Player.PLAYER_2) {
      classes.push('player-2');
    }

    if (this.node.lastMove) {
      classes.push('lastMove');
    }
    if (this.node.partOfWinnerPath) {
      classes.push('blink');
    }
    return classes.join(' ');
  }

  hexagonClick() {
    this.clicked.emit();
  }

}
