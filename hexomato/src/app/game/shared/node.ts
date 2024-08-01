import {Player} from "./player.enum";

export interface Node {
  row: number;
  col: number;
  lastMove: boolean;
  partOfWinnerPath: boolean;
  player: Player;
}
