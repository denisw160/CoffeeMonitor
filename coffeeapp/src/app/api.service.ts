import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import 'rxjs/add/operator/map';

import {SensorData} from './sensordata';
import {Alive} from './alive';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private _http: HttpClient) {
  }

  // Status Services

  /**
   * Query the service, if the sensor is alive.
   */
  getAlive(): Observable<Alive> {
    return this._http.get<Alive>('/api/alive');
  }

  // Data Services

  /**
   * Get all data directly from the database.
   */
  getData(): Observable<SensorData[]> {
    return this._http.get<SensorData[]>('/api/data');
  }

  /**
   * Get the latest data entry from the database.
   */
  getDataLatest(): Observable<SensorData> {
    return this._http.get<SensorData>('/api/data/latest');
  }

  // TODO Add service for configuration

  dailyForecast() { // TODO Remove after testing
    return this._http.get('/assets/sampleweather.json')
      .map(result => result);
  }

}
