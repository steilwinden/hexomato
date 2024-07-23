import {Component} from '@angular/core';
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [MatButtonModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent {

  initGame() {
  }
}
