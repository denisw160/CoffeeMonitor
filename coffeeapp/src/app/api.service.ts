import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import 'rxjs/add/operator/map';

import {SensorData} from './sensordata';
import {Alive} from './alive';
import {Config} from './config';
import {Consumption} from './consumption';
import {environment} from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private mockUpMode: boolean; // TODO Add mockUp json data

  constructor(private _http: HttpClient) {
    this.mockUpMode = environment.mockUpMode;
    // console.debug('Run services in mockUp mode: ' + this.mockUpMode);
  }

  // Status Services

  /**
   * Query the service, if the sensor is alive.
   */
  getAlive(): Observable<Alive> {
    return this._http.get<Alive>('/api/alive');
  }

  // Config Services

  /**
   * Query the config for the system.
   */
  getConfig(): Observable<Config> {
    return this._http.get<Config>('/api/config');
  }

  // Data Services

  /**
   * Get all data directly from the database.
   */
  getData(): Observable<SensorData[]> {
    return this._http.get<SensorData[]>('/api/data/7days');
  }

  /**
   * Get the latest data entry from the database.
   */
  getDataLatest(): Observable<SensorData> {
    return this._http.get<SensorData>('/api/data/latest');
  }

  /**
   * Get the latest consumption from today.
   */
  getConsumptionLatest(): Observable<Consumption> {
    return this._http.get<Consumption>('/api/consumption/latest');
  }

  // TODO Add service for configuration

  dailyForecast() { // TODO Remove after testing
    return this._http.get('/assets/sampleweather.json')
      .map(result => result);
  }

}
