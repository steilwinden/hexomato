import {Component, Input} from '@angular/core';
import {GameService} from "../shared/game.service";
import {HexNode} from "../shared/hex-node";
import {HexState} from "../shared/hex-state.enum";

@Component({
  selector: 'app-hexagon',
  standalone: true,
  imports: [],
  templateUrl: './hexagon.component.html',
  styleUrl: './hexagon.component.scss'
})
export class HexagonComponent {

  @Input() hexNode!: HexNode;

  constructor(public gameService: GameService) {}

  getContentSvgClass() {
    const classes = ['content-svg'];
    if (this.hexNode.state === HexState.PLAYER_1) {
      classes.push('player-1');
    } else if (this.hexNode.state === HexState.PLAYER_2) {
      classes.push('player-2');
    }
    if (this.hexNode.active) {
      classes.push('active');
    }
    return classes.join(' ');
  }

    protected readonly HexState = HexState;
}
