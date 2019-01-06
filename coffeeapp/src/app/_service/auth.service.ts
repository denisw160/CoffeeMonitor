import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {LoginModel, UserModel} from '../_model';
import {Observable} from 'rxjs';
import * as $ from 'jquery';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly mockUpMode: boolean;

  private user: UserModel;

  constructor(private _http: HttpClient) {
    this.mockUpMode = environment.mockUpMode;
    if (this.mockUpMode) {
      console.log('Run auth services in mockUp mode');
    }
  }

  /**
   * Is the user already login?
   */
  isLoggedIn() {
    if (this.mockUpMode) {
      return true;
    }

    if (this.user) {
      return this.user.loggedIn;
    }

    // Not working, because the data load is async and not wait
    // Check on server if login already done
    // this.getLogin().toPromise().then(l => {
    //   this.user = new UserModel();
    //   this.user.user = l.user;
    //   this.user.token = null;
    //   this.user.loggedIn = l.success;
    // }).catch();

    // Check on server if login already done
    const l = this.getLoginSync();
    this.user = new UserModel();
    this.user.user = l.user;
    this.user.token = null;
    this.user.loggedIn = l.success;

    if (this.user) {
      return this.user.loggedIn;
    }

    return false;
  }


  /**
   * Try to login the user.
   */
  doLogin(username: String, password: String): Observable<LoginModel> {
    this.user = new UserModel();
    this.user.user = username;
    this.user.loggedIn = false;
    this.user.generateToken(username, password);

    // const httpOptions = {
    //   headers: new HttpHeaders({
    //     Authorization: 'Basic ' + this.user.token
    //   })
    // };
    //
    // // Try to login to service
    // this._http.get('/api/login', httpOptions).toPromise()
    //   .then(l => {
    //     console.log('login success');
    //     this.user.loggedIn = true;
    //     return l;
    //   })
    //   .catch(() => this.user = null);

    const result = this.getLoginSync({
      Authorization: 'Basic ' + this.user.token
    });
    this.user.loggedIn = result.success;

    return this.getLogin();
  }

  /**
   * Logout the current user.
   */
  doLogout() {
    this.user = null;
  }

  /**
   * Get the login from the server.
   */
  getLogin(): Observable<LoginModel> {
    if (this.mockUpMode) {
      return this._http.get<LoginModel>('/assets/mock_api_login.json');
    } else {
      return this._http.get<LoginModel>('/api/login');
    }
  }

  getLoginSync(headers?): LoginModel {
    const model = new LoginModel();
    $.ajax({
      async: false,
      type: 'GET',
      dataType: 'json',
      url: '/api/login',
      headers: headers || {},
      success: function (data) {
        model.user = data.user;
        model.success = data.success;
      }
    });
    return model;
  }

  /**
   * Return the current logged in user.
   */
  getUser(): UserModel {
    return this.user;
  }

}
