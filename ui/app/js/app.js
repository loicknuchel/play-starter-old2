angular.module('retail-scan', [
    'ui.router',
    'ui.bootstrap']);


angular.module('retail-scan').config(function($stateProvider, $urlRouterProvider) {
    "use strict";
    $urlRouterProvider.otherwise('/home');

    $stateProvider
    .state('root', {
        abstract: true,
        url: '',
        templateUrl: 'assets/views/root.html',
        controller: 'rootCtrl'
    })
    .state('root.home', {
        url: '/home',
        templateUrl: 'assets/views/home.html',
        controller: 'homeCtrl'
    })
    .state('root.users1', {
        url: '/users1',
        templateUrl: 'assets/views/users.html',
        controller: 'usersCtrl'
    });
});
