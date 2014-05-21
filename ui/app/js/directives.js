angular.module('simple-crud')


.directive('selectAllCheckbox', function () {
    return {
        replace: true,
        restrict: 'E',
        scope: {
            checkboxes: '=',
            allselected: '=allSelected',
            allclear: '=allClear'
        },
        template: '<input type="checkbox" ng-model="master" ng-change="masterChange()">',
        controller: function ($scope, $element) {

            $scope.masterChange = function () {
                if ($scope.master) {
                    angular.forEach($scope.checkboxes, function (cb, index) {
                        cb.isSelected = true;
                    });
                } else {
                    angular.forEach($scope.checkboxes, function (cb, index) {
                        cb.isSelected = false;
                    });
                }
            };

            $scope.$watch('checkboxes', function () {
                var allSet = true,
                    allClear = true;
                angular.forEach($scope.checkboxes, function (cb, index) {
                    if (cb.isSelected) {
                        allClear = false;
                    } else {
                        allSet = false;
                    }
                });

                if ($scope.allselected !== undefined) {
                    $scope.allselected = allSet;
                }
                if ($scope.allclear !== undefined) {
                    $scope.allclear = allClear;
                }

                $element.prop('indeterminate', false);
                if (allClear) {
                    $scope.master = false;
                } else if (allSet) {
                    $scope.master = true;
                } else {
                    $scope.master = false;
                    $element.prop('indeterminate', true);
                }

            }, true);
        }
    };
});