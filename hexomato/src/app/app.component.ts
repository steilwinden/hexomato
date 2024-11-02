import {Component, HostListener, OnInit} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {NgStyle} from "@angular/common";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NgStyle],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {

  readonly appWidth: number = 1100;
  scale: number = 1;

  constructor(private router: Router) {
  }

  ngOnInit() {
    this.calculateScale();
  }

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.calculateScale();
  }

  private calculateScale() {
    const minDimension = Math.min(window.innerWidth, window.innerHeight);
    this.scale = minDimension / this.appWidth;
  }

  getScaleStyle() {
    const currentRoute = this.router.url;
    const gameRoute = currentRoute.includes('/game');
    return {
      transform: `scale(${this.scale})`,
      'transform-origin': 'center ' + (gameRoute ? 'center' : 'top')
    };
  }
}
