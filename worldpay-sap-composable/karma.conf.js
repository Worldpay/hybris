// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html
const isCI = require('is-ci');

module.exports = function (config) {
  config.set({
    basePath: "",
    frameworks: ["jasmine", "@angular-devkit/build-angular"],
    plugins: [
      require("karma-jasmine"),
      require("karma-chrome-launcher"),
      require("karma-jasmine-html-reporter"),
      require("karma-coverage-istanbul-reporter"),
      require("@angular-devkit/build-angular/plugins/karma"),
      require('karma-coverage'),
    ],
    client: {
      clearContext: false // leave Jasmine Spec Runner output visible in browser
    },
    coverageIstanbulReporter: {
      dir: require("path").join(__dirname, "../../coverage/worldpay-sap-composable"),
      reports: ["html", "lcovonly", "text-summary"],
      fixWebpackSourcePaths: true
    },
    coverageReporter: {
      dir: require('path').join(__dirname, './coverage/angular-unit-test'),
      subdir: '.',
      reporters: [
        {type: 'html'},
        {type: 'text-summary'}
      ],
      check: {
        emitWarning: false,
        global: {
          statements: 75,
          branches: 75,
          functions: 75,
          lines: 75,
        }
      }
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
    reporters: ['progress', 'kjhtml', 'coverage'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: isCI ? ['ChromeHeadlessCI'] : ['Chrome'],
    singleRun: false,
    restartOnFileChange: true,
    files: [],
    preprocessors: {
      'src/**/*.js': ['coverage'],
    },
  });
};
