## CoffeeApp

### Description

This the web client of the CoffeeMonitor. The data from the CoffeeService will shown on this site.
The dashboard gives a quick overview over the coffee status. With the config site the parameter for 
the calculation can be adjusted. In the history view you find the consummation of coffee over the time.   

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 7.0.4.
UI is using Bootstrap.

### Development

#### Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload 
if you change any of the source files.

The backend service runs `http://localhost:80808/`. ng serve will redirect all requests .../api to this
service.

#### Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use
`ng generate directive|pipe|service|class|guard|interface|enum|module`.

#### Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. 
Use the `--prod` flag for a production build.

#### Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

#### Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

#### Further help

To get more help on the Angular CLI use `ng help` or go check out the 
[Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

### Scripts
To install the required components, simply run the setup script. The installation requires SUDO rights.

    bash>setup.sh

To install the service, simply run the install script. The installation requires SUDO rights.

    bash>install.sh

To uninstall the service, simply run the uninstall script. The installation requires SUDO rights.

    bash>uninstall.sh

The run script can be used to build and start the server on the local console (for development).

    bash>run.sh

For clean and building the service the following scripts can be used.

    bash>clean.sh
    bash>build.sh
