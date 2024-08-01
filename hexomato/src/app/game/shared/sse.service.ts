import {Injectable, NgZone} from '@angular/core';
import {Node} from "./node";
import {Observable} from "rxjs";
import {Game} from "./game";

@Injectable({
  providedIn: 'root'
})
export class SseService {

  constructor(private zone: NgZone) {
  }

  getSetupEvents(url: string): Observable<Game[]> {
    return new Observable(observer => {
      const eventSource = new EventSource(url);

      eventSource.onmessage = event => {
        this.zone.run(() => {
          const gameList: Game[] = JSON.parse(event.data);
          observer.next(gameList);
        });
      };

      eventSource.onerror = error => {
        observer.error(error);
        eventSource.close();
      };

      return () => {
        eventSource.close();
      };
    });
  }

  getGameEvents(url: string): Observable<Node> {
    return new Observable(observer => {
      const eventSource = new EventSource(url);

      eventSource.onmessage = event => {
        this.zone.run(() => {
          const hexNode: Node = JSON.parse(event.data);
          observer.next(hexNode);
        });
      };

      eventSource.onerror = error => {
        observer.error(error);
        eventSource.close();
      };

      return () => {
        eventSource.close();
      };
    });
  }

}
