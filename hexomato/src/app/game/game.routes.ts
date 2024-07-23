import {Routes} from "@angular/router";
import {BoardComponent} from "./board/board.component";
import {SettingsComponent} from "./settings/settings.component";

const gameRoutes: Routes = [
  {path: '', component: SettingsComponent},
  {path: 'board', component: BoardComponent}
];

export default gameRoutes;
