angular.module('simple-crud')

.service('roomSrv', function(){
    var rooms = [
        {id: 'room1', name: 'Room 1'},
        {id: 'room2', name: 'Room 2'},
        {id: 'room3', name: 'Room 3'},
        {id: 'room4', name: 'Room 4'},
        {id: 'room5', name: 'Room 5'}
    ];

    return {
        getRooms: function(){
            return rooms;
        }
    };
})

.service('chatSrv', function($rootScope, $localStorage, $http){
    // TODO create a Chat class https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Inheritance_and_the_prototype_chain
    if(!$localStorage.chats){$localStorage.chats = [];}
    var chats = $localStorage.chats;
    for(var i in chats){
        loadChat(chats[i]);
    }

    function loadChat(chat){
        delete chat.chatFeed;
        chat.msgs = [];

        chat.isOpen = function(){
            return this.currentRoom && this.chatFeed;
        };
        chat.close = function(){
            closeChatRoom(this);
        };
        chat.changeRoom = function(room){
            var roomId = room ? room.id : null;
            var curRoomId = this.currentRoom ? this.currentRoom.id : null;
            if(roomId !== curRoomId){
                closeChatRoom(this);
                openChatRoom(this, room);
            }
        };
        chat.receiveMessage = function(msg){
            this.msgs.push(msg);
        };
        chat.sendMessage = function(){
            if(this.isOpen()){
                $http.post('/api/chat/'+this.currentRoom.id+'/post', {
                    text: this.inputText,
                    user: this.user,
                    time: new Date().getTime()
                });
                this.inputText = '';
            }
        };

        openChatRoom(chat, chat.currentRoom);
    }

    function openChatRoom(chat, room){
        if(chat && !chat.isOpen()){
            chat.currentRoom = room;
            if(room && room.id){
                chat.chatFeed = new EventSource('/api/chat/'+room.id+'/feed');
                chat.chatFeed.addEventListener('message', function(result){
                    $rootScope.$apply(function(){
                        chat.receiveMessage(JSON.parse(result.data));
                    });
                });
            }
        }
    }
    function closeChatRoom(chat){
        if(chat && chat.isOpen()){
            // TODO remove event handle to avoid memory leak
            chat.chatFeed.close();
            delete chat.chatFeed;
            chat.currentRoom = null;
            chat.msgs = [];
        }
    }

    return {
        getChats: function(){
            return chats;
        },
        createChat: function(){
            var chat = {
                user: 'Jane Doe #' + Math.floor((Math.random() * 100) + 1),
                msgs: [],
                inputText: '',
                currentRoom: null
            };
            loadChat(chat);
            chats.push(chat);
            return chat;
        },
        removeChat: function(chat){
            var index = chats.indexOf(chat);
            if(index > -1){
                if(chat.isOpen()){
                    chat.close();
                }
                chats.splice(index, 1);
            }
        }
    };
})

.controller('chatCtrl', function($scope, chatSrv, roomSrv){
    'use strict';
    $scope.rooms = roomSrv.getRooms();
    $scope.chats = chatSrv.getChats();

    $scope.createChat = function(){
        chatSrv.createChat();
    };

    $scope.removeChat = function(chat){
        chatSrv.removeChat(chat);
    };

    /** change current room, restart EventSource connection */
    $scope.changeRoom = function(chat, roomId){
        if(chat){
            var room = null;
            for(var i in $scope.rooms){
                if($scope.rooms[i].id === roomId){
                    room = $scope.rooms[i];
                }
            }
            chat.changeRoom(room);
        }
    };

    /** posting chat text to server */
    $scope.submitMsg = function(chat){
        if(chat){
            chat.sendMessage();
        }
    };
});