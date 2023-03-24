// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html
const isCI = require('is-ci');

module.exports = function(config) {
  config.set({
    basePath: "",
    frameworks: ["jasmine", "@angular-devkit/build-angular"],
    plugins: [
      require("karma-jasmine"),
      require("karma-chrome-launcher"),
      require("karma-jasmine-html-reporter"),
      require("karma-coverage-istanbul-reporter"),
      require("@angular-devkit/build-angular/plugins/karma")
    ],
    client: {
      clearContext: false // leave Jasmine Spec Runner output visible in browser
    },
    coverageIstanbulReporter: {
      dir: require("path").join(__dirname, "../../coverage/worldpay-spartacus"),
      reports: ["html", "lcovonly", "text-summary"],
      fixWebpackSourcePaths: true
    },
    customLaunchers: {
          ChromeHeadlessCI: {
            base: 'ChromeHeadless',
            flags: [
              '--no-sandbox',
              '--disable-gpu',
              '--disable-translate',
              '--disable-extensions',
              '--disable-dev-shm-usage',
              '--window-size=1280,800'
            ]
          }
        },
    reporters: ["progress", "kjhtml"],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: isCI ? ['ChromeHeadlessCI'] : ['Chrome'],
    singleRun: false,
    restartOnFileChange: true,
    files: [

    ]
  });
};
