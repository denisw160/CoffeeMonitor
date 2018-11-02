import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {DashboardComponent} from './dashboard/dashboard.component';
import {HistoryComponent} from './history/history.component';
import {ConfigComponent} from './config/config.component';
import {ErrorComponent} from './error/error.component';

const routes: Routes = [
  {path: 'dashboard', component: DashboardComponent},
  {path: 'history', component: HistoryComponent},
  {path: 'config', component: ConfigComponent},
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {path: '**', component: ErrorComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes,
    {enableTracing: true} // <-- debugging purposes only
  )],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
