angular.module('myApp')

.controller('crudCtrl', function($scope, Restangular){
    $scope.baseUrl = 'api/users1';
})

.controller('crudAllCtrl', function($scope, $state, Restangular){
    $scope.allUsers = [];

    Restangular.all($scope.baseUrl).getList().then(function(users) {
        $scope.allUsers = users;
    });
})

.controller('crudCreateCtrl', function($scope, $state, Restangular){
    $scope.user = {
        name: '',
        bio: ''
    };

    $scope.createUser = function(){
        Restangular.all($scope.baseUrl).post($scope.user).then(function(data){
            $state.go('root.crud.all');
        });
    };
})

.controller('crudDetailsCtrl', function($scope, $state, $stateParams, Restangular){
    var id = $stateParams.id;
    var syncUser = null;
    $scope.user = null;
    $scope.edit = false;
    $scope.notFound = false;

    Restangular.one($scope.baseUrl, id).get().then(function(user){
        syncUser = user;
        $scope.user = Restangular.copy(syncUser);
    }, function(err){
        $scope.notFound = id;
    });

    $scope.startEdit = function(){
        $scope.edit = true;
    };
    $scope.cancelEdit = function(){
        $scope.edit = false;
        $scope.user = Restangular.copy(syncUser);
    };
    $scope.saveEdit = function(){
        $scope.user.save().then(function(result){
            syncUser = Restangular.copy($scope.user);
            $scope.edit = false;
        });
    };
    
    $scope.delete = function(){
        if(confirm('Delete '+$scope.user.name+' ?')){
            $scope.user.remove().then(function(result){
                $state.go('root.crud.all');
            });
        }
    };
});
