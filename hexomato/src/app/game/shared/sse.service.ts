import {Injectable, NgZone} from '@angular/core';
import {HexNode} from "./hex-node";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SseService {

  constructor(private zone: NgZone) {
  }

  getServerSentEvent(url: string): Observable<HexNode> {
    return new Observable(observer => {
      const eventSource = new EventSource(url);

      eventSource.onmessage = event => {
        this.zone.run(() => {
          const hexNode: HexNode = JSON.parse(event.data);
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
