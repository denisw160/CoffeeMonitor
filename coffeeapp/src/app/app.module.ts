import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';

import {ApiService} from './api.service';

import {DashboardComponent} from './dashboard/dashboard.component';
import {HistoryComponent} from './history/history.component';
import {ConfigComponent} from './config/config.component';
import {ErrorComponent} from './error/error.component';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    HistoryComponent,
    ConfigComponent,
    ErrorComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [ApiService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
