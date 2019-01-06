/**
 * This class represents the user.
 */
export class UserModel {

  user: String;
  token: String;
  loggedIn: boolean;

  constructor() {
  }

  generateToken(username: String, password: String) {
    this.token = btoa(username + ':' + password);
  }

}
