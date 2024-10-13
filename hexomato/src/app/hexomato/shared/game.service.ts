import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class GameService {
  readonly hexDiameter: number = 60;
  readonly boardWidth: number = 1100;
  readonly boardHeight: number = 660;
}
