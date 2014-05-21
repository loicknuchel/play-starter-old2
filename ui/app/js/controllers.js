angular.module('simple-crud')

.controller('rootCtrl', function($scope, $location){
    'use strict';

    $scope.isActive = function (viewLocation) {
        var active = (viewLocation === $location.path());
        return active;
    };
})

.controller('homeCtrl', function($scope){
    'use strict';
});