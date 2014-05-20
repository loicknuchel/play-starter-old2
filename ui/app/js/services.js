angular.module('simple-crud.services', [])


.service('momentSrv', function(){
    // wrap momentjs lib
    return moment;
})

.service('chatModel', function(){
    function getRooms(){
        return [
            {name: 'Room 1', value: 'room1'},
            {name: 'Room 2', value: 'room2'},
            {name: 'Room 3', value: 'room3'},
            {name: 'Room 4', value: 'room4'},
            {name: 'Room 5', value: 'room5'}
        ];
    }

    return {
        getRooms: getRooms
    };
});
