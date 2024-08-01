import {Player} from "./player.enum";

export interface Game {
  id: bigint;
  namePlayer1: string | null;
  namePlayer2: string | null;
  turn: Player;
  winner: Player;
  board: Node[][];
}
