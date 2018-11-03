import {Component, OnInit} from '@angular/core';
import {Chart} from 'chart.js';
import * as moment from 'moment';

import {ApiService} from '../api.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {

  chart_level = []; // This will hold our chart info
  consumptionCanvas: Chart;

  constructor(private _api: ApiService) {
  }

  ngOnInit() {
    // TODO Replace with real implementation
    this._api.dailyForecast()
      .subscribe(res => {
        console.log('Query data');

        let temp_max = res['list'].map(res => res.main.temp_max);
        let temp_min = res['list'].map(res => res.main.temp_min);
        let alldates = res['list'].map(res => res.dt);

        let weatherDates = [];
        alldates.forEach((res) => {
          let jsdate = new Date(res * 1000);
          weatherDates.push(jsdate.toLocaleTimeString('en', {year: 'numeric', month: 'short', day: 'numeric'}));
        });

        this.chart_level = new Chart('canvas_level', {
          type: 'line',
          data: {
            labels: weatherDates,
            datasets: [
              {
                data: temp_max,
                borderColor: '#3cba9f',
                fill: false
              },
              {
                data: temp_min,
                borderColor: '#ffcc00',
                fill: false
              },
            ]
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


    this._api.getConsumption()
      .subscribe(data => {
        // Query for data
        console.log('Receiving data for consumptions');
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
              backgroundColor: '#00A600'
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

}
