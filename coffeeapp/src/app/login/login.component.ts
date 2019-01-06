import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../_service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;

  loading = false;
  submitted = false;
  error = false;
  notLoggedIn = false;

  returnUrl: string;

  constructor(private _formBuilder: FormBuilder,
              private _route: ActivatedRoute,
              private _router: Router,
              private _authService: AuthService) {
    // redirect to home if already logged in
    if (this._authService.isLoggedIn()) {
      this._router.navigate(['/']);
    }
  }

  ngOnInit() {
    this.loginForm = this._formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    // get return url from route parameters or default to '/'
    this.returnUrl = this._route.snapshot.queryParams['returnUrl'] || '/';
  }

  // convenience getter for easy access to form fields
  get f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    this.notLoggedIn = false;
    this.error = false;

    // stop here if form is invalid
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this._authService.doLogin(this.f.username.value, this.f.password.value)
      .subscribe(
        l => {
          if (l.success) {
            this._router.navigate([this.returnUrl]);
          }
          this.notLoggedIn = true;
          this.loading = false;
        },
        () => {
          this.error = true;
          this.loading = false;
        });
  }

}
