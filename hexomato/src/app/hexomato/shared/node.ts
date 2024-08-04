import {Player} from "./player.enum";

export interface Node {
  lastMove: boolean;
  partOfWinnerPath: boolean;
  player: Player;
}
