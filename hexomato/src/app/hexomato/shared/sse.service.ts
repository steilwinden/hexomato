import {Injectable, NgZone} from '@angular/core';
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SseService {

  constructor(private zone: NgZone) {
  }

  getEvents<T>(url: string): Observable<T> {
    return new Observable(observer => {
      const eventSource = new EventSource(url);

      eventSource.onmessage = event => {
        const data: T = JSON.parse(event.data);
        this.zone.run(() => {
          console.log('data:', data);
          observer.next(data);
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
