angular.module('retail-scan')

.controller('rootCtrl', function ($scope, $location) {
    "use strict";

    $scope.isActive = function (viewLocation) {
        var active = (viewLocation === $location.path());
        return active;
    };
})

.controller('homeCtrl', function ($scope) {
    "use strict";
})

.controller('usersCtrl', function ($scope, $http, $modal) {
    "use strict";
    var apiPath = '/api/users1';
    var modalUrl = 'assets/views/userModal.html';

    var actionCreate = 'create', actionShow = 'show', actionEdit = 'edit', actionDelete = 'delete';
    $scope.elts = [];
    $http({method: 'GET', url: apiPath}).then(function(result){
        console.log(result.data);
        $scope.elts = result.data;
    });

    $scope.create = function(){
        var emptyElt = {
            name: '',
            pw: ''
        };
        createModal(emptyElt, actionCreate);
    };
    $scope.show = function(elt){
        createModal(elt, actionShow);
    };
    $scope.edit = function(elt){
        createModal(elt, actionEdit);
    };
    $scope.delete = function(elt){
        createModal(elt, actionDelete);
    };

    function createModal(elt, action){
        var modalInstance = $modal.open({
            templateUrl: modalUrl,
            controller: function($scope, $modalInstance){
                $scope.initElt = elt;
                $scope.elt = angular.copy(elt);
                $scope.action = action;
                $scope.edit = action === actionCreate || action === actionEdit;

                $scope.create = function () {
                    var elt = $scope.elt;
                    console.log('create elt', elt);
                    $http({method: 'POST', url: apiPath, data: elt}).then(function(result){
                        if(result.status === 200 || result.status === 201){
                            $modalInstance.close(elt);
                        } else {
                            // TODO add form validation
                            console.log('result', result);
                            alert('error !');
                        }
                    }, function(error){
                        console.log('Error', error);
                        alert('error !');
                    });
                };
                $scope.update = function () {
                    var elt = $scope.elt;
                    $http({method:'PUT', url: apiPath+'/'+elt._id.$oid+'/full'}).then(function(result){
                        console.log('result', result);
                        if(result.status === 200){
                            angular.copy($scope.elt, $scope.initElt);
                            $modalInstance.close();
                        } else {
                            // TODO add form validation
                            console.log('result', result);
                            alert('error !');
                        }
                    }, function(error){
                        console.log('Error', error);
                        alert('error !');
                    });
                };
                $scope.delete = function () {
                    var elt = $scope.initElt;
                    $http({method:'DELETE', url: apiPath+'/'+elt._id.$oid}).then(function(result){
                        if(result.status === 200){
                            $modalInstance.close(elt);
                        } else {
                            // TODO add form validation
                            console.log('result', result);
                            alert('error !');
                        }
                    }, function(error){
                        console.log('Error', error);
                        alert('error !');
                    });
                };
                $scope.ok = function () {
                    $modalInstance.close();
                };
                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };
            }
        });

        modalInstance.result.then(function(result) {
            if(result){
                if(action === actionCreate){
                    $scope.elts.push(result);
                } else if(action === actionDelete){
                    var index = $scope.elts.indexOf(result);
                    if (index > -1) {$scope.elts.splice(index, 1);}
                }
            }
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    }
});