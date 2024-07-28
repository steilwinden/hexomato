import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {Player} from "./player.enum";

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
    const url = `${this.apiBaseUrl}/ws/createGame/player/${player}/name/${name}`;
    const basicAuth = 'Basic ' + btoa(`${this.apiUsername}:${this.apiPassword}`);

    const headers = new HttpHeaders({
      'Authorization': basicAuth
    });

    return this.http.get<bigint>(url, {headers});
  }
}
