import {HexState} from "./hex-state.enum";

export interface HexNode {
  lastMove: boolean;
  startBlinking: boolean;
  state: HexState;
}
