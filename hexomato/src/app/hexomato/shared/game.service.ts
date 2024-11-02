import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class GameService {
  readonly hexDiameter: number = 60;
}
