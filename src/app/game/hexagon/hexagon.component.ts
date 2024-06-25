import {Component, Input} from '@angular/core';
import {GameService} from "../shared/game.service";
import {HexNode} from "../shared/hex-node";

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

  getHexagonSvgContentClass() {
    const classes = ['hexagon-svg-content'];
    if (this.hexNode.player === 1) {
      classes.push('player-1');
    } else if (this.hexNode.player === 2) {
      classes.push('player-2');
    }
    if (this.hexNode.active) {
      classes.push('active');
    }
    return classes.join(' ');
  }
}
