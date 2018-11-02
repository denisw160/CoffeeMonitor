import {Component, OnInit} from '@angular/core';
import {ConfigModel} from '../model/config.model';
import {ApiService} from '../api.service';

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
      this.config.maxWeight = x.maxWeight;
    });
  }

  onSubmit() {
    // console.log('Submitting configuration: ' + this.config.maxWeight);

    this.showSuccess = false;
    this.showError = false;

    const request = this._api.putConfig(this.config);
    request.subscribe(data => {
        // console.log('Submit success: ' + data['statusCode']);
        this.showSuccess = true;
      },
      error => {
        // console.log('Submit failed: ' + error['statusCode']);
        this.showError = true;
      });
  }

  ngOnInit() {
  }

}
