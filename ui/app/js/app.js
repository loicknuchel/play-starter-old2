angular.module('simple-crud', ['ui.router', 'ui.bootstrap', 'simple-crud.controllers', 'simple-crud.services', 'simple-crud.directives'])

.config(function($stateProvider, $urlRouterProvider) {
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
    })
    .state('root.users2', {
        url: '/users2',
        templateUrl: 'assets/views/users.html',
        controller: 'usersCtrl'
    })
    .state('root.chat', {
        url: '/chat',
        templateUrl: 'assets/views/chat.html',
        controller: 'chatCtrl'
    });
});
