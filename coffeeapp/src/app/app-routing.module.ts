import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ConfigComponent} from './config/config.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {ErrorComponent} from './error/error.component';
import {HistoryComponent} from './history/history.component';

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
    {enableTracing: false} // <-- debugging purposes only
  )],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
