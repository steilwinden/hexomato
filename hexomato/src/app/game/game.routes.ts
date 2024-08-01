import {Routes} from "@angular/router";
import {BoardComponent} from "./board/board.component";
import {SetupComponent} from "./setup/setup.component";

const gameRoutes: Routes = [
  {path: '', component: SetupComponent},
  {path: 'board', component: BoardComponent}
];

export default gameRoutes;
