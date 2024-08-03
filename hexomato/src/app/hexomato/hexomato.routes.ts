import {Routes} from "@angular/router";
import {SetupComponent} from "./setup/setup.component";
import {GameComponent} from "./game/game.component";

const hexomatoRoutes: Routes = [
  {path: '', component: SetupComponent},
  {path: 'game', component: GameComponent}
];

export default hexomatoRoutes;
