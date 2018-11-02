import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private _http: HttpClient) {
  }

  getData(): Observable<any> {
    return this._http.get('/api/data');
  }

  getDataLatest(): Observable<any> {
    return this._http.get('/api/data/latest');
  }

  // TODO Add service for configuration

  dailyForecast() { // TODO Remove after testing
    return this._http.get('/assets/sampleweather.json')
      .map(result => result);
  }

}
