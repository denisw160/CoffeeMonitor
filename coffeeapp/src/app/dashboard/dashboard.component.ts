import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {timer} from 'rxjs';
import {ApiService} from '../api.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {

  @ViewChild('coffee-status') imgCoffeeStatus: ElementRef;

  updateTimer = timer(100, 5000);
  updateSubscription;

  constructor(private _api: ApiService) {
  }

  /**
   * Update the icon for the coffee machine. Shows of the sensors.
   */
  updateAliveStatus() {
    const alive = this._api.getAlive();
    alive.subscribe(a => {
      if (a.alive) {
        document.getElementById('coffee-status').setAttribute('src', '../../assets/status-info.png');
      } else {
        document.getElementById('coffee-status').setAttribute('src', '../../assets/status-warning.png');
      }
    });
  }

  /**
   * Updates the status for the fill level, allocation from the sensor data.
   */
  updateFillAllocation() {
    const currentData = this._api.getDataLatest();
    currentData.subscribe(r => {
      console.log('ID: ' + r.id);
      console.log('TS: ' + r.timestamp);
      console.log('Weight: ' + r.weight);
      console.log('Allocated: ' + r.allocated);
    });

  }

  updateDashboard(t) {
    console.log('Update data: ' + t + ' - ' + new Date()); // TODO REMOVE

    this.updateAliveStatus();
    this.updateFillAllocation();
  }

  ngOnInit() {
    this.updateSubscription = this.updateTimer.subscribe(t => this.updateDashboard(t));
  }

  ngOnDestroy() {
    this.updateSubscription.unsubscribe();
  }

}
