let socket;
let chatRoomId;
function getUserIdFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('userId');
}
const userId = getUserIdFromURL();
// 채팅방의 메시지
function loadChatRoom(roomId) {
    chatRoomId = roomId;

    fetch(`/api/chat/rooms/${chatRoomId}/messages`)
        .then(response => response.json())
        .then(messages => {
            const chatMessages = document.getElementById("chatMessages");
            chatMessages.innerHTML = '';
            console.log(messages[0].sender.nickname);
            messages.forEach(message => {
                displayMessage(message);
            });

            // WebSocket 연결 갱신
            connectWebSocket(chatRoomId);
            console.log("여기",messages)
            document.getElementById("chatRoomTitle").innerText = `${messages[0].chatRoom.user2.nickname}`;
        })
        .catch(error => console.error(error));
}

// WebSocket
function connectWebSocket(chatRoomId) {
    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.close();
    }

    socket = new WebSocket(`ws://localhost:8080/ws/chat?roomId=${chatRoomId}&userId=${userId}`);

    socket.onopen = () => {
        console.log("웹소켓 연결");
    };

    socket.onmessage = (event) => {
        const messageData = JSON.parse(event.data);
        console.log(messageData);
        displayMessage(messageData);
    };

    socket.onclose = (event) => {
        console.log("Disconnected from WebSocket", event);
        if (!event.wasClean) {
            setTimeout(() => {
                console.log("Attempting to reconnect to WebSocket...");
                connectWebSocket(chatRoomId);
            }, 1000);
        }
    };

    socket.onerror = (error) => {
        console.error("WebSocket error:", error);
    };
}


async function sendMessage() {
    const messageInput = document.getElementById("messageInput");
    const message = messageInput.value.trim();

    if (message) {
        const messageDto = {
            chatRoomId: chatRoomId,
            senderId: userId,
            message: message,
            messageType: "TALK"
        };

        try {
            const response = await fetch('/api/chat/message', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(messageDto)
            });

            if (response.ok) {
                const chatMessage = await response.json();
                console.log("메세지전송완료")
                displayMessage(chatMessage);
            } else {
                console.error("Failed to send message:", response.statusText);
            }
        } catch (error) {
            console.error("Error sending message:", error);
        }

        messageInput.value = "";
    }
}


function displayMessage(messageData) {
    const chatMessages = document.getElementById("chatMessages");
    console.log(messageData.sender);
    console.log("내아이디",messageData.chatRoom.id, "보낸사람 아이디",messageData.sender.id)
    const messageElement = document.createElement("div");
    messageElement.className = messageData.sender && messageData.sender.id === messageData.chatRoom.id ? 'message-outgoing' : 'message-incoming';

    const nickname = messageData.sender.nickname;

    const senderNickname = document.createElement("strong");
    senderNickname.innerText = nickname;
    messageElement.appendChild(senderNickname);

    const messageContent = document.createElement("p");
    messageContent.innerText = messageData.message;
    messageElement.appendChild(messageContent);

    const date = new Date(messageData.createdAt);
    const formattedDate = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
    const formattedTime = `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    const formattedDateTime = `${formattedDate} ${formattedTime}`;

    const messageTimestamp = document.createElement("span");
    messageTimestamp.innerText = formattedDateTime;
    messageElement.appendChild(messageTimestamp);

    chatMessages.appendChild(messageElement);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}
