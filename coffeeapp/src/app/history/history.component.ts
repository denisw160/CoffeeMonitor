import {Component, OnInit} from '@angular/core';
import * as Chart from 'chart.js';
import * as moment from 'moment';
import {ApiService} from '../api.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {

  levelChart: Chart;
  consumptionCanvas: Chart;

  constructor(private _api: ApiService) {
  }

  /**
   * Building the chart for the level data.
   */
  private buildLevelChart() {
    // Chart for level
    this._api.getData()
      .subscribe(data => {
        // Query for data
        // console.log('Receiving data for SensorData');
        const ts = data.map(d => {
          const timestamp = moment(d.timestamp);
          return timestamp.format('LT');
        });
        const allocated = data.map(d => {
          if (d.allocated) {
            return 1;
          }
          return 0;
        });
        const weights = data.map(d => d.weight);

        // Building chart
        this.levelChart = new Chart('levelCanvas', {
          type: 'line',
          data: {
            labels: ts,
            datasets: [
              {
                data: weights,
                label: 'Weight',
                backgroundColor: '#E74C3C',
                borderColor: '#E74C3C',
                fill: false,
                yAxisID: 'A'
              },
              {
                data: allocated,
                label: 'Allocated',
                backgroundColor: '#f0f0f0',
                borderColor: '#cdcdcd',
                fill: true,
                yAxisID: 'B'
              }
            ]
          },
          options: {
            legend: {
              display: true,
              position: 'bottom'
            },
            scales: {
              xAxes: [{
                display: true
              }],
              yAxes: [{
                id: 'A',
                type: 'linear',
                position: 'left',
              }, {
                id: 'B',
                type: 'linear',
                position: 'right',
                ticks: {
                  max: 1,
                  min: 0,
                  stepSize: 1,
                }
              }]
            }
          }
        });
      });
  }

  /**
   * Building the chart for the consumption data.
   */
  private buildConsumptionChart() {
    // Chart for consumptions
    this._api.getConsumption()
      .subscribe(data => {
        // Query for data
        // console.log('Receiving data for consumptions');
        const days = data.map(d => {
          const day = moment(d.day);
          return day.format('ll');
        });
        const consumptions = data.map(d => d.consumption);

        // Building chart
        this.consumptionCanvas = new Chart('consumptionCanvas', {
          type: 'bar',
          data: {
            labels: days,
            datasets: [{
              data: consumptions,
              label: 'Consumption',
              backgroundColor: '#E74C3C'
            }]
          },
          options: {
            legend: {
              display: false
            },
            scales: {
              xAxes: [{
                display: true
              }],
              yAxes: [{
                display: true
              }],
            }
          }
        });
      });
  }


  ngOnInit() {
    // Building charts on init
    this.buildLevelChart();
    this.buildConsumptionChart();
  }

// TODO Add AutoUpdate

}
