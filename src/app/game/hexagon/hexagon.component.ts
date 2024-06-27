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

  constructor(public gameService: GameService) {
  }

  getContentSvgClass() {
    const classes = ['content-svg'];
    if (this.hexNode.state === HexState.PLAYER_1) {
      classes.push('player-1');
    } else if (this.hexNode.state === HexState.PLAYER_2) {
      classes.push('player-2');
    } else if (this.hexNode.state === HexState.EMPTY) {
      classes.push('empty');
    }
    if (this.hexNode.state !== HexState.EMPTY) {
      if (this.hexNode.lastMove) {
        classes.push('lastMove');
      }
      if (this.hexNode.startBlinking) {
        classes.push('blink');
      }
    }
    return classes.join(' ');
  }

  protected readonly HexState = HexState;
}
