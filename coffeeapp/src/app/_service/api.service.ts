import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import 'rxjs/add/operator/map';
import {AliveModel, ConfigModel, ConsumptionModel, SensordataModel} from '../_model';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private readonly mockUpMode: boolean;

  constructor(private _http: HttpClient,
              @Inject('BASE_URL') private _baseHref: string) {
    this.mockUpMode = environment.mockUpMode;
    if (this.mockUpMode) {
      console.log('Run api services in mockUp mode');
    }
  }

  // Status Services

  /**
   * Query the service, if the sensor is alive.
   */
  getAlive(): Observable<AliveModel> {
    if (this.mockUpMode) {
      return this._http.get<AliveModel>(this._baseHref + 'assets/mock_api_alive.json');
    } else {
      return this._http.get<AliveModel>(this._baseHref + 'api/alive');
    }
  }

  // Config Services

  /**
   * Query the config for the system.
   */
  getConfig(): Observable<ConfigModel> {
    if (this.mockUpMode) {
      return this._http.get<ConfigModel>(this._baseHref + 'assets/mock_api_config.json');
    } else {
      return this._http.get<ConfigModel>(this._baseHref + 'api/config');
    }
  }

  /**
   * Saving the configuration on the server.
   * @param config Configuration
   */
  postConfig(config: ConfigModel): Observable<any> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };

    return this._http.post(this._baseHref + 'api/config', JSON.stringify(config), httpOptions);
  }

  // Data Services

  /**
   * Get all data directly from the database (7 days).
   */
  getData(): Observable<SensordataModel[]> {
    if (this.mockUpMode) {
      return this._http.get<SensordataModel[]>(this._baseHref + 'assets/mock_api_data_7days.json');
    } else {
      return this._http.get<SensordataModel[]>(this._baseHref + 'api/data/7days');
    }
  }

  /**
   * Get the latest data entry from the database.
   */
  getDataLatest(): Observable<SensordataModel> {
    if (this.mockUpMode) {
      return this._http.get<SensordataModel>(this._baseHref + 'assets/mock_api_data_latest.json');
    } else {
      return this._http.get<SensordataModel>(this._baseHref + 'api/data/latest');
    }
  }

  /**
   * Get the all consumption for the last 7 days.
   */
  getConsumption(): Observable<ConsumptionModel[]> {
    if (this.mockUpMode) {
      return this._http.get<ConsumptionModel[]>(this._baseHref + 'assets/mock_api_consumption_7days.json');
    } else {
      return this._http.get<ConsumptionModel[]>(this._baseHref + 'api/consumption/7days');
    }
  }

  /**
   * Get the latest consumption from today.
   */
  getConsumptionLatest(): Observable<ConsumptionModel> {
    if (this.mockUpMode) {
      return this._http.get<ConsumptionModel>(this._baseHref + 'assets/mock_api_consumption_latest.json');
    } else {
      return this._http.get<ConsumptionModel>(this._baseHref + 'api/consumption/latest');
    }
  }

}
