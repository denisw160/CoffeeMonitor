import {Component, OnInit} from '@angular/core';
import {ApiService} from '../_service';
import {ConfigModel} from '../_model';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.css']
})
export class ConfigComponent implements OnInit {

  config: ConfigModel;

  showSuccess = false;
  showError = false;

  constructor(private _api: ApiService) {
    document.getElementById('alertSuccess');

    this.config = new ConfigModel();

    const c = this._api.getConfig();
    c.subscribe(x => {
      this.config = x;
    });
  }

  ngOnInit() {
  }

  onSubmit() {
    // console.log('Submitting configuration: ' + this.config.maxWeight);

    this.showSuccess = false;
    this.showError = false;

    const request = this._api.postConfig(this.config);
    request.subscribe(() => {
        this.showSuccess = true;
      },
      () => {
        this.showError = true;
      });
  }

}
