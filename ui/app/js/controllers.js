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

.controller('usersCtrl', function ($scope, $http, $location, $modal) {
    "use strict";
    var apiPath = '/api'+$location.path();
    var modalUrl = 'assets/views/userModal.html';
    var emptyElt = {
        name: '',
        bio: ''
    };

    var actionCreate = 'create', actionShow = 'show', actionUpdate = 'edit', actionDelete = 'delete';
    $scope.elts = [];
    $scope.apiPath = apiPath;
    console.log('GET '+apiPath);
    $http({method: 'GET', url: apiPath}).then(function(result){
        console.log('result', result);
        $scope.elts = result.data;
    });

    $scope.create = function(){
        createModal(angular.copy(emptyElt), actionCreate);
    };
    $scope.show = function(elt){
        createModal(elt, actionShow);
    };
    $scope.edit = function(elt){
        createModal(elt, actionUpdate);
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
                $scope.edit = action === actionCreate || action === actionUpdate;
                var onError = function(error){
                    console.log('Error', error);
                    alert('error !');
                };
                var onSuccess = function(result, successCB){
                    console.log('result', result);
                    if(result.status === 200 || result.status === 201){
                        successCB(result);
                    } else {
                        // TODO add form validation
                        alert('error !');
                    }
                }

                $scope.create = function () {
                    var elt = $scope.elt;
                    console.log('POST '+apiPath, elt);
                    $http({method: 'POST', url: apiPath, data: elt}).then(function(result){
                        onSuccess(result, function(result){
                            $modalInstance.close(elt);
                        });
                    }, onError);
                };
                $scope.update = function () {
                    var elt = $scope.elt;
                    console.log('PUT '+apiPath+'/'+elt._id.$oid, elt);
                    $http({method:'PUT', url: apiPath+'/'+elt._id.$oid, data: elt}).then(function(result){
                        onSuccess(result, function(result){
                            angular.copy($scope.elt, $scope.initElt);
                            $modalInstance.close(elt);
                        });
                    }, onError);
                };
                $scope.delete = function () {
                    var elt = $scope.initElt;
                    console.log('DELETE '+apiPath+'/'+elt._id.$oid);
                    $http({method:'DELETE', url: apiPath+'/'+elt._id.$oid}).then(function(result){
                        onSuccess(result, function(result){
                            $modalInstance.close(elt);
                        });
                    }, onError);
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
                } else if(action === actionUpdate){
                    // nothing
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