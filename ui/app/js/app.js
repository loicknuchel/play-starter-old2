angular.module('myApp', ['ui.router', 'ui.bootstrap', 'ngStorage', 'restangular'])

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
    .state('root.crud', {
        abstract: true,
        url: '/crud',
        templateUrl: 'assets/views/crud/main.html',
        controller: 'crudCtrl'
    })
    .state('root.crud.all', {
        url: '/all',
        templateUrl: 'assets/views/crud/all.html',
        controller: 'crudAllCtrl'
    })
    .state('root.crud.create', {
        url: '/create',
        templateUrl: 'assets/views/crud/create.html',
        controller: 'crudCreateCtrl'
    })
    .state('root.crud.details', {
        url: '/:id',
        templateUrl: 'assets/views/crud/details.html',
        controller: 'crudDetailsCtrl'
    })
    .state('root.chat', {
        url: '/chat',
        templateUrl: 'assets/views/chat.html',
        controller: 'chatCtrl'
    });
})

.run(function($rootScope, $location){
    $rootScope.isActive = function (viewLocation) {
        var regex = new RegExp('^'+viewLocation+'$', 'g');
        return regex.test($location.path());
    };
});
