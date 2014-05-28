angular.module('simple-crud')


.filter('fromNow', function(momentSrv){
    return function(date){
        if(date && typeof date.format !== 'function'){
            date = momentSrv.utc(date);
        }
        return date ? date.fromNow() : ' - ';
    };
});