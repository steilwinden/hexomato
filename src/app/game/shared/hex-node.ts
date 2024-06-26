import {HexState} from "./hex-state.enum";

export interface HexNode {
  active: boolean;
  state: HexState;
}
