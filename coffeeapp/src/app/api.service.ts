import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import 'rxjs/add/operator/map';

import {SensordataModel} from './model/sensordata.model';
import {AliveModel} from './model/alive.model';
import {ConfigModel} from './model/config.model';
import {ConsumptionModel} from './model/consumption.model';
import {environment} from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  private mockUpMode: boolean; // TODO Add mockUp json data

  constructor(private _http: HttpClient) {
    this.mockUpMode = environment.mockUpMode;
    // console.debug('Run services in mockUp mode: ' + this.mockUpMode);
  }

  // Status Services

  /**
   * Query the service, if the sensor is alive.
   */
  getAlive(): Observable<AliveModel> {
    return this._http.get<AliveModel>('/api/alive');
  }

  // Config Services

  /**
   * Query the config for the system.
   */
  getConfig(): Observable<ConfigModel> {
    return this._http.get<ConfigModel>('/api/config');
  }

  /**
   * Saving the configuration on the server.
   * @param config Configuration
   */
  putConfig(config: ConfigModel): Observable<any> {
    return this._http.put('/api/config', JSON.stringify(config), this.httpOptions);
  }

  // Data Services

  /**
   * Get all data directly from the database.
   */
  getData(): Observable<SensordataModel[]> {
    return this._http.get<SensordataModel[]>('/api/data/7days');
  }

  /**
   * Get the latest data entry from the database.
   */
  getDataLatest(): Observable<SensordataModel> {
    return this._http.get<SensordataModel>('/api/data/latest');
  }

  /**
   * Get the latest consumption from today.
   */
  getConsumptionLatest(): Observable<ConsumptionModel> {
    return this._http.get<ConsumptionModel>('/api/consumption/latest');
  }

  // TODO Add service for configuration

  dailyForecast() { // TODO Remove after testing
    return this._http.get('/assets/sampleweather.json')
      .map(result => result);
  }

}
