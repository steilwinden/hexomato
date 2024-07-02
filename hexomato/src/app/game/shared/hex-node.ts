import {Player} from "./player.enum";

export interface HexNode {
  id: number;
  lastMove: boolean;
  startBlinking: boolean;
  player: Player | null;
}
