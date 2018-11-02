import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {timer} from 'rxjs';
import * as moment from 'moment';

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
    const config = this._api.getConfig();
    config.subscribe(c => {
      // console.log('Max Weight: ' + c.maxWeight);

      const data = this._api.getDataLatest();
      data.subscribe(d => {
        // console.log('ID: ' + d.id);
        // console.log('TS: ' + d.timestamp);
        // console.log('Weight: ' + d.weight);
        // console.log('Allocated: ' + d.allocated);

        // Update image for fill level
        // TODO Check if an offset is required
        const div = c.maxWeight / 6.0;
        const step = Math.min(Math.floor(d.weight / div), 5);
        document.getElementById('coffee-level').setAttribute('src', '../../assets/level-' + step + '.png');

        // Update progressbar
        const percentage = Math.round((d.weight * 100) / c.maxWeight);
        const progressBar = document.getElementById('coffee-level-progress');
        progressBar.setAttribute('aria-valuenow', String(percentage));
        progressBar.style.width = percentage + '%';
        progressBar.innerText = percentage + '%';

        // Update allocation
        if (d.allocated) {
          document.getElementById('coffee-allocated').setAttribute('src', '../../assets/coffee-allocated.png');
        } else {
          document.getElementById('coffee-allocated').setAttribute('src', '../../assets/coffee-not-allocated.png');
        }

        // Set last updated
        const timestamp = moment(d.timestamp);
        // console.log(timestamp.locale());
        // console.log(timestamp.format());
        // console.log(timestamp.fromNow());
        document.getElementById('coffee-level-updated').innerText = timestamp.fromNow();
        document.getElementById('coffee-allocated-updated').innerText = timestamp.fromNow();
      });
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
