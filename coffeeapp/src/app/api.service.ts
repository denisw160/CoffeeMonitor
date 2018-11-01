import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) {
  }

  getData(): Observable<any> {
    return this.http.get('/api/data');
  }

  getDataLatest(): Observable<any> {
    return this.http.get('/api/data/latest');
  }

  // TODO Add service for configuration

}
