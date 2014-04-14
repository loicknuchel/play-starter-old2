angular.module('simple-crud')

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
    var actions = {
        showAll: 'all',
        show: 'show',
        create: 'create',
        update: 'update',
        delete: 'delete'
    };
    var status = {
        unknown: 'Unknown',
        working: 'Working',
        warning: 'Warning',
        error: 'ERROR'
    };
    var style = {
        unknown: 'label-default',
        working: 'label-success',
        warning: 'label-warning',
        error: 'label-danger'
    };
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

    $scope.elts = [];
    $scope.apiPath = apiPath;
    /*$scope.endpoints = [
        {name: actions.showAll, status: status.unknown, style: style.unknown},
        {name: actions.show,   status: status.unknown, style: style.unknown},
        {name: actions.create, status: status.unknown, style: style.unknown},
        {name: actions.update, status: status.unknown, style: style.unknown},
        {name: actions.delete, status: status.unknown, style: style.unknown}
    ];*/

    console.log('GET '+apiPath);
    $http({method: 'GET', url: apiPath}).then(function(result){
        if($scope.endpoints){testEndpoints($scope.endpoints);}
        onSuccess(result, function(result){
            $scope.elts = result.data;
        });
    }, onError);

    $scope.create = function(){
        createModal(angular.copy(emptyElt), actions.create);
    };
    $scope.show = function(elt){
        createModal(elt, actions.show);
    };
    $scope.edit = function(elt){
        createModal(elt, actions.update);
    };
    $scope.delete = function(elt){
        createModal(elt, actions.delete);
    };

    function createModal(elt, action){
        var modalInstance = $modal.open({
            templateUrl: modalUrl,
            controller: function($scope, $modalInstance){
                $scope.initElt = elt;
                $scope.elt = angular.copy(elt);
                $scope.action = action;
                $scope.edit = action === actions.create || action === actions.update;

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
                    console.log('PUT '+apiPath+'/'+elt.id, elt);
                    $http({method:'PUT', url: apiPath+'/'+elt.id, data: elt}).then(function(result){
                        onSuccess(result, function(result){
                            angular.copy($scope.elt, $scope.initElt);
                            $modalInstance.close(elt);
                        });
                    }, onError);
                };
                $scope.delete = function () {
                    var elt = $scope.initElt;
                    console.log('DELETE '+apiPath+'/'+elt.id);
                    $http({method:'DELETE', url: apiPath+'/'+elt.id}).then(function(result){
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
                if(action === actions.create){
                    $scope.elts.push(result);
                } else if(action === actions.update){
                    // nothing
                } else if(action === actions.delete){
                    var index = $scope.elts.indexOf(result);
                    if (index > -1) {$scope.elts.splice(index, 1);}
                }
            }
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    }

    function statusStyle(s){
        if(s === status.unknown){return style.unknown;}
        else if(s === status.working){return style.working;}
        else if(s === status.warning){return style.warning;}
        else if(s === status.error){return style.error;}
    }
    function getEndpoint(endpoints, name){
        for(var i in endpoints){
            if(endpoints[i].name === name){return endpoints[i];}
        }
    }
    function updateEndpointStatus(endpoints, name, status){
        var endpoint = getEndpoint(endpoints, name);
        if(endpoint){
            endpoint.status = status;
            endpoint.style = statusStyle(status);
        }
    }
    function testEndpoints(endpoints){
        // check read-only endpoints
        $http({method: 'GET', url: apiPath}).then(function(result){
            if(result.status === 200){
                updateEndpointStatus(endpoints, actions.showAll, status.working);
                if(result.data.length > 0){
                    $http({method:'GET', url: apiPath+'/'+result.data[0].id}).then(function(result){
                        if(result.status === 200){ updateEndpointStatus(endpoints, actions.show, status.working); }
                        else { updateEndpointStatus(endpoints, actions.show, status.warning); }
                    }, function(error){
                        updateEndpointStatus(endpoints, actions.show, status.error);
                    });
                }
            } else {
                updateEndpointStatus(endpoints, actions.showAll, status.warning);
            }
        }, function(error){
            updateEndpointStatus(endpoints, actions.showAll, status.error);
        });

        // check write endpoints
        var elt = angular.copy(emptyElt);
        elt.name = 'TEST:todelete!';
        $http({method: 'POST', url: apiPath, data: elt}).then(function(result){
            if(result.status === 200 || result.status === 201){
                updateEndpointStatus(endpoints, actions.create, status.working);
                elt.id = result.data.id;
                if(elt.id){
                    elt.bio = 'TESTabc';
                    $http({method:'PUT', url: apiPath+'/'+elt.id, data: elt}).then(function(result){
                        if(result.status === 200){ updateEndpointStatus(endpoints, actions.update, status.working); }
                        else { updateEndpointStatus(endpoints, actions.update, status.warning); }
                        $http({method:'DELETE', url: apiPath+'/'+elt.id}).then(function(result){
                            if(result.status === 200){ updateEndpointStatus(endpoints, actions.delete, status.working); }
                            else { updateEndpointStatus(endpoints, actions.delete, status.warning); }
                        }, function(error){
                            updateEndpointStatus(endpoints, actions.delete, status.error);
                        });
                    }, function(error){
                        updateEndpointStatus(endpoints, actions.update, status.error);
                        $http({method:'DELETE', url: apiPath+'/'+elt.id}).then(function(result){
                            if(result.status === 200){ updateEndpointStatus(endpoints, actions.delete, status.working); }
                            else { updateEndpointStatus(endpoints, actions.delete, status.warning); }
                        }, function(error){
                            updateEndpointStatus(endpoints, actions.delete, status.error);
                        });
                    });
                }
            } else {
                updateEndpointStatus(endpoints, actions.create, status.warning);
            }
        }, function(error){
            updateEndpointStatus(endpoints, actions.create, status.error);
        });
    }
});