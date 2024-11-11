let socket;
let chatRoomId;
let senderInfo = {};
let receiverInfo = {};
let userId;
console.log(userId);
// 채팅방의 메시지 로드
function loadChatRoom(roomId) {
    chatRoomId = roomId;

    // 채팅방 메시지 가져오기
    fetch(`/api/chat/rooms/${chatRoomId}/messages`)
        .then(response => response.json())
        .then(messages => {
            const chatMessages = document.getElementById("chatMessages");
            chatMessages.innerHTML = '';
            messages.forEach(message => displayMessage(message));

            // senderInfo와 receiverInfo 설정
            fetch(`/api/chat/rooms?userId=${userId}`)
                .then(response => response.json())
                .then(chatRooms => {
                    const chatRoom = chatRooms.find(room => room.id === roomId);
                    if (chatRoom) {
                        senderInfo = chatRoom.user1;
                        receiverInfo = chatRoom.user2;
                    }
                    console.log('여기요', chatRoom)
                })
                .catch(error => console.error("Error loading chat room info:", error));

            // WebSocket 연결
            connectWebSocket(chatRoomId);
        })
        .catch(error => console.error("Error loading chat room messages:", error));
}

// WebSocket 연결 설정
function connectWebSocket(chatRoomId) {
    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.close();
    }

    socket = new WebSocket(`ws://localhost:8080/ws/chat?roomId=${chatRoomId}&userId=${userId}`);
    console.log('ws주소',`ws://localhost:8080/ws/chat?roomId=${chatRoomId}&userId=${userId}`);
    // console.log("WebSocket Initial Ready State (CONNECTING):", socket.readyState);

    socket.onopen = () => {
        console.log("WebSocket 연결 성공");
        console.log("WebSocket Ready State on open (OPEN):", socket.readyState); // 연결이 성공적으로 열리면 OPEN 상태(1)가 됩니다.
    };

    socket.onmessage = (event) => {
        console.log("WebSocket 메시지 수신 중...");
        try {
            const messageData = JSON.parse(event.data);
            console.log("Received message via WebSocket:", messageData);

            // 메시지 타입이 "info"일 경우 연결 완료 메시지로 처리
            if (messageData.type === "info") {
                console.log("Info message from server:", messageData.message);
            } else {
                displayMessage(messageData); // 실제 메시지 처리
            }
        } catch (error) {
            console.error("Error parsing WebSocket message:", error);
            console.log("Received raw message:", event.data);
        }
    };
    socket.addEventListener("message", (event) => {
        console.log("Message from server:", event.data);
    });
    socket.onclose = (event) => {
        console.log("Disconnected from WebSocket", event);
        if (!event.wasClean) {
            setTimeout(() => {
                connectWebSocket(chatRoomId);
            }, 3000); // 3초 후 재연결 시도
        }
    };

    socket.onerror = (error) => {
        console.error("WebSocket error:", error);
    };
}

// 메시지 전송 함수
async function sendMessage() {
    const messageInput = document.getElementById("messageInput");
    const message = messageInput.value.trim();

    if (message) {
        const chatMessageDto = {
            chatRoomId: chatRoomId,
            sender: senderInfo,
            receiver: receiverInfo,
            message: message,
            messageType: "TALK"
        };
        console.log(chatMessageDto);
        socket.send(JSON.stringify(chatMessageDto));
        try {
            const response = await fetch('/api/chat/message', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(chatMessageDto)
            });

            if (response.ok) {
                const savedMessage = await response.json();
                console.log("Message sent successfully:", savedMessage);
            } else {
                console.error("Failed to send message:", response.statusText);
            }
        } catch (error) {
            console.error("Error sending message:", error);
        }

        messageInput.value = "";
    }
}

// 메시지를 화면에 표시하는 함수
function displayMessage(messageData) {
    console.log("Displaying message:", messageData);
    const chatMessages = document.getElementById("chatMessages");
    const messageElement = document.createElement("div");

    // 'message-outgoing' 또는 'message-incoming' 결정
    messageElement.className = messageData.sender.userId === userId ? 'message-outgoing' : 'message-incoming';
    const nickname = messageData.sender.nickname;

    const senderNickname = document.createElement("strong");
    senderNickname.innerText = nickname;
    messageElement.appendChild(senderNickname);

    const messageContent = document.createElement("p");
    messageContent.innerText = messageData.message;
    messageElement.appendChild(messageContent);

    const date = messageData.createdAt ? new Date(messageData.createdAt) : new Date();
    const formattedDate = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
    const formattedTime = `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    const formattedDateTime = `${formattedDate} ${formattedTime}`;

    const messageTimestamp = document.createElement("span");
    messageTimestamp.innerText = formattedDateTime;
    messageElement.appendChild(messageTimestamp);

    chatMessages.appendChild(messageElement);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}
