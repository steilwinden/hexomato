import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {Player} from "./player.enum";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private apiBaseUrl = environment.apiBaseUrl;
  private apiUsername = environment.apiUsername;
  private apiPassword = environment.apiPassword;

  constructor(private http: HttpClient) {
  }

  createGame(player: Player, name: string): Observable<bigint> {
    const url = `${this.apiBaseUrl}/ws/setup/createGame/player/${player}/name/${name}`;
    const headers = this.createBasicAuthHeader();
    return this.http.get<bigint>(url, {headers});
  }

  joinGame(gameId: bigint, player: Player, name: string): Observable<void> {
    const url = `${this.apiBaseUrl}/ws/setup/joinGame/gameId/${gameId}/player/${player}/name/${name}`;
    const headers = this.createBasicAuthHeader();
    return this.http.get<void>(url, {headers});
  }

  startGame(gameId: bigint): Observable<void> {
    const url = `${this.apiBaseUrl}/ws/game/start/gameId/${gameId}`;
    const headers = this.createBasicAuthHeader();
    return this.http.get<void>(url, {headers});
  }

  makeMove(gameId: bigint, row: number, col: number, player: Player): Observable<void> {
    const url = `${this.apiBaseUrl}/ws/game/makeMove/gameId/${gameId}/row/${row}/col/${col}/player/${player}`;
    const headers = this.createBasicAuthHeader();
    return this.http.get<void>(url, {headers});
  }

  private createBasicAuthHeader(): HttpHeaders {
    const basicAuth = 'Basic ' + btoa(`${this.apiUsername}:${this.apiPassword}`);
    return new HttpHeaders({
      'Authorization': basicAuth
    });
  }
}
