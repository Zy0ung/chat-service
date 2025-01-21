const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/stomp/chats'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    showChatRooms(0);
    stompClient.subscribe('/sub/chats/updates',
        (chatMessage) => {
            toggleNewMessageIcon(JSON.parse(chatMessage.body).id, true)
            updateMemberCount(JSON.parse(chatMessage.body));
        })
    console.log('Connected: ' + frame);
};

function toggleNewMessageIcon(chatRoomId, toggle) {
    if (chatRoomId == $("#chatRoom-id").val()) {
        return;
    }
    if (toggle) {
        $("#new_" + chatRoomId).show();
    } else {
        $("#new_" + chatRoomId).hide();
    }
}

function updateMemberCount(chatRoom) {
    $("#memberCount_" + chatRoom.id).html(chatRoom.memberCount);
}

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

function createChatRoom() {
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/chats?title=' + $("#chatRoom-title").val(),
        success: function (data) {
            console.log('data: ', data);
            showChatRooms(0);
            enterChatRoom(data.id, true);
        },
        error: function (request, status, error) {
            console.log('request: request');
            console.log('error: ', error);
        }
    })
}

function showChatRooms(pageNumber) {
    $.ajax({
        type: 'GET',
        dataType: 'json',
        url: '/consultants/chats?sort=id,desc&page=' + pageNumber,
        success: function (data) {
            console.log('data: ', data);
            renderChatRooms(data);
        },
        error: function (request, status, error) {
            console.log('request: ', request);
            console.log('error: ', error);
        }
    })
}

let subscription;

function enterChatRoom(chatRoomId, newMember) {
    $("#chatRoom-id").val(chatRoomId);
    $("#messages").html("");
    showMessages(chatRoomId);
    $("#conversation").show();
    $("#send").prop("disabled", false);
    $("#leave").prop("disabled", false);
    toggleNewMessageIcon(chatRoomId, false);

    if (subscription != undefined) {
        subscription.unsubscribe();
    }

    subscription = stompClient.subscribe('/sub/chats/' + chatRoomId,
        (chatMessage) => {
            showMessage(JSON.parse(chatMessage.body));
        });

    if (newMember) {
        stompClient.publish({
            destination: "/pub/chats/" + chatRoomId,
            body: JSON.stringify(
                {'message': "님이 방에 들어왔습니다."})
        })
    }
}

function renderChatRooms(page) {
    let chatRooms = page.content;
    $("#chatRoom-list").html("");
    for (let i = 0; i < chatRooms.length; i++) {
        $("#chatRoom-list").append(
            "<tr onclick='joinChatRoom(" + chatRooms[i].id + ")'><td>"
            + chatRooms[i].id + "</td><td>" + chatRooms[i].title
            + "<img src='new.png' id='new_" + chatRooms[i].id + "' style='display: "
            + getDisplayValue(chatRooms[i].hasNewMessage)
            + "'/></td><td id='memberCount_" + chatRooms[i].id + "'>"
            + chatRooms[i].memberCount + "</td><td>" + chatRooms[i].createdAt
            + "</td></tr>"
        );
    }

    if (page.first) {
        $("#prev").prop("disabled", true);
    } else {
        $("#prev").prop("disabled", false).click(() => showChatRooms(page.number - 1));
    }

    if (page.last) {
        $("#next").prop("disabled", true);
    } else {
        $("#next").prop("disabled", false).click(() => showChatRooms(page.number + 1));
    }
}

function getDisplayValue(hasNewMessage) {
    if (hasNewMessage) {
        return "inline";
    }
    return "none"
}

function showMessages(chatRoomId) {
    $.ajax({
        type: 'GET',
        dataType: 'json',
        url: '/chats/' + chatRoomId + '/messages',
        success: function (data) {
            console.log('data ', data);
            for (let i = 0; i < data.length; i++) {
                showMessage(data[i])
            }
        },
        error: function (request, status, error) {
            console.log('request: request');
            console.log('error: ', error);
        }
    })
}

function joinChatRoom(chatRoomId) {
    let currentChatRoomId = $("#chatRoom-id").val();

    $.ajax({
        type: "POST",
        dataType: 'json',
        url: '/chats/' + chatRoomId + getRequestParam(currentChatRoomId),
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

function getRequestParam(currentChatroomId) {
    if (currentChatroomId == "") {
        return "";
    }

    return "?currentChatRoomId=" + currentChatroomId;
}

function leaveChatRoom() {
    let chatRoomId = $("#chatRoom-id").val();
    $.ajax({
        type: 'DELETE',
        dataType: 'json',
        url: '/chats/' + chatRoomId,
        success: function (data) {
            console.log('data: ', data);
            showChatRooms(0);
            exitChatRoom(chatRoomId);
        },
        error: function (request, status, error) {
            console.log('request: request');
            console.log('error: ', error);
        }
    })
}

function exitChatRoom(chatRoomId) {
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