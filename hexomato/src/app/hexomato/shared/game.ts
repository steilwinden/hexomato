import {Player} from "./player.enum";
import {Node as NodeAlias} from './node';

type Node = NodeAlias;

export interface Game {
  id: bigint;
  namePlayer1: string | null;
  namePlayer2: string | null;
  turn: Player;
  winner: Player | null;
  board: Node[][];
  connectionMessage: string;
}
