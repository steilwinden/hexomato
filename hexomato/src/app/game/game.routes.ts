import {Routes} from "@angular/router";
import {BoardComponent} from "./board/board.component";
import {SetupComponent} from "./settings/setup.component";

const gameRoutes: Routes = [
  {path: '', component: SetupComponent},
  {path: 'board', component: BoardComponent}
];

export default gameRoutes;
