const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/stomp/chats'
});

stompClient.onConnect = (frame) => {
  setConnected(true);
  showChatRooms();
  console.log('Connected: ' + frame);
};

stompClient.onWebSocketError = (error) => {
  console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']);
  console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  $("#create").prop("disabled", !connected);
}

function connect() {
  stompClient.activate();
}

function disconnect() {
  stompClient.deactivate();
  setConnected(false);
  console.log("Disconnected");
}

function sendMessage() {
  let chatRoomId = $("#chatRoom-id").val();
  stompClient.publish({
    destination: "/pub/chats/" + chatRoomId,
    body: JSON.stringify(
        {'message': $("#message").val()})
  });
  $("#message").val("")
}

function showMessage(chatMessage) {
  $("#messages").append(
      "<tr><td>" + chatMessage.sender + " : " + chatMessage.message
      + "</td></tr>");
}

function createChatRoom(){
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: '/chats?title=' + $("#chatRoom-title").val(),
    success:function (data) {
      console.log('data: ', data);
      showChatRooms();
      enterChatRoom(data.id, true);
    },
    error: function (request, status, error){
      console.log('request: request');
      console.log('error: ', error);
    }
  })
}

function showChatRooms() {
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: '/chats',
    success: function (data) {
      console.log('data: data');
      renderChatRooms(data);
    },
    error: function (request, status, error) {
      console.log('request: request');
      console.log('error: ', error);
    }
  })
}

let subscription;

function enterChatRoom(chatRoomId, newMember){
  $("#chatRoom-id").val(chatRoomId);
  $("#messages").html("");
  showMessages(chatRoomId);
  $("#conversation").show();
  $("#send").prop("disabled", false);
  $("#leave").prop("disabled", false);

  if(subscription != undefined){
    subscription.unsubscribe();
  }

  subscription = stompClient.subscribe('/sub/chats/' + chatRoomId,
      (chatMessage) => {
        showMessage(JSON.parse(chatMessage.body));
      });

  if (newMember){
    stompClient.publish({
      destination: "/pub/chats/" + chatRoomId,
      body: JSON.stringify(
          {'message': "님이 방에 들어왔습니다."})
    })
  }
}

function renderChatRooms(chatRooms){
  $("#chatRoom-list").html("");
  for(let i = 0; i < chatRooms.length; i++){
    $("#chatRoom-list").append(
        "<tr onclick='joinChatRoom(" + chatRooms[i].id + ")'><td>"
        + chatRooms[i].id + "</td><td>" + chatRooms[i].title + "</td><td>"
        + chatRooms[i].memberCount + "</td><td>" + chatRooms[i].createAt
        + "</td></tr>"
    )
  }
}

function showMessages(chatRoomId) {
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: '/chats/' + chatRoomId + '/messages',
    success: function (data) {
      console.log('data ', data);
      for(let i  = 0; data.length; i++) {
        showMessage(data[i])
      }
    },
    error: function (request, status, error) {
      console.log('request: request');
      console.log('error: ', error);
    }
  })
}

function joinChatRoom(chatRoomId){
  $.ajax({
    type: "POST",
    dataType: 'json',
    url: '/chats/' + chatRoomId,
    success: function (data) {
      console.log('data: ', data);
      enterChatRoom(chatRoomId, data);
    },
    error: function (request, status, error) {
      console.log('request: request');
      console.log('error: ', error);
    }
  })
}

function leaveChatRoom(){
  let chatRoomId = $("#chatRoom-id").val();
  $.ajax({
    type: 'DELETE',
    dataType: 'json',
    url: '/chats/' + chatRoomId,
    success: function (data){
      console.log('data: ', data);
      showChatRooms();
      exitChatRoom(chatRoomId);
    },
    error: function (request, status, error) {
      console.log('request: request');
      console.log('error: ', error);
    }
  })
}

function exitChatRoom(chatRoomId){
  $("#chatRoom-id").val("");
  $("#conversation").hide();
  $("#send").prop("disabled", true);
  $("#leave").prop("disabled", true);
}

$(function () {
  $("form").on('submit', (e) => e.preventDefault());
  $("#connect").click(() => connect());
  $("#disconnect").click(() => disconnect());
  $("#create").click(() => createChatRoom());
  $("#leave").click(() => leaveChatRoom());
  $("#send").click(() => sendMessage());
});