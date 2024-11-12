// 삭제 버튼 클릭 이벤트
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('challenge-id').value;  // 챌린지 ID 가져오기
        fetch(`/api/chboard/${id}`, {
            method: 'DELETE'
        }).then(() => {
            alert('삭제가 완료되었습니다');
            location.replace('/chboard');
        });
    });
}

// 생성 기능
const createButton = document.getElementById('create-btn');

if (createButton) {
    createButton.addEventListener('click', event => {
        fetch(`/api/chboard/write`, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body : JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value,
                startDate: document.getElementById('start-date').value,
                endDate: document.getElementById('end-date').value,
                status: document.getElementById('status').value,
                type: document.getElementById('type').value
            }),
        }).then(() => {
            alert('등록 완료되었습니다');
            location.replace("/chboard");
        }).catch(error => {
            console.error('Error:', error);
            alert('챌린지 등록에 실패했습니다');
        });
    })
}

// 수정 버튼 클릭 이벤트
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let id = document.getElementById('challenge-id').value;  // 챌린지 ID 가져오기

        fetch(`/api/chboard/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value,
                startDate: document.getElementById('start-date').value,
                endDate: document.getElementById('end-date').value,
                status: document.getElementById('status').value,
                type: document.getElementById('type').value
            })
        }).then(() => {
            alert('수정이 완료되었습니다');
            location.replace(`/chboard/${id}`);
        });
    });
}

// 댓글 등록 기능
const commentForm = document.getElementById("comment-form");

if (commentForm) {
    commentForm.addEventListener('submit', event => {
        event.preventDefault();  // 폼 제출 기본 동작 방지

        let content = document.getElementById('comment-content').value.trim();
        if (content === "") {
            alert("댓글 내용을 입력해 주세요.");
            return;
        }

        let challengeId = document.getElementById('challenge-id').value;

        fetch(`/api/chboard/${challengeId}/comments`, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                body: content  // 필드명 변경: content -> body
            }),
        }).then(response => response.json()).then(data => {
            alert('댓글이 등록되었습니다');
            addCommentToList(data);  // 새 댓글 목록에 추가
            document.getElementById('comment-content').value = '';  // 댓글 입력창 초기화
        }).catch(error => {
            console.error('Error:', error);
            alert('댓글 등록에 실패했습니다');
        });
    });
}

// 댓글 목록에 새 댓글 추가
function addCommentToList(comment) {
    const commentSection = document.querySelector(".comment-section");

    const commentCard = document.createElement("div");
    commentCard.classList.add("card", "mb-3");

    const cardBody = document.createElement("div");
    cardBody.classList.add("card-body");

    const cardText = document.createElement("p");
    cardText.classList.add("card-text");

    // 댓글 내용이 제대로 전달되었는지 확인
    if (comment && comment.content) {
        cardText.textContent = comment.content;
    } else {
        cardText.textContent = "댓글 내용이 없습니다."; // 디폴트 텍스트
    }

    const timestamp = document.createElement("div");
    timestamp.classList.add("text-muted", "fst-italic");

    // 댓글 작성일자 처리
    if (comment && comment.createdAt) {
        timestamp.textContent = `Posted on ${new Date(comment.createdAt).toLocaleString()}`;
    } else {
        timestamp.textContent = "작성일자를 알 수 없습니다.";
    }

    const deleteButton = document.createElement("button");
    deleteButton.classList.add("btn", "btn-danger", "btn-sm", "ml-2");
    deleteButton.textContent = "삭제";
    deleteButton.addEventListener("click", function () {
        deleteComment(comment.id, commentCard);
    });

    cardBody.appendChild(cardText);
    cardBody.appendChild(timestamp);
    cardBody.appendChild(deleteButton);
    commentCard.appendChild(cardBody);

    commentSection.appendChild(commentCard);
}


// 댓글 삭제 기능
function deleteComment(commentId, commentElement) {
    if (!confirm("댓글을 삭제하시겠습니까?")) return;

    let challengeId = document.getElementById('challenge-id').value;

    fetch(`/api/chboard/${challengeId}/comments/${commentId}`, {
        method: 'DELETE',
    }).then(() => {
        commentElement.remove();
        alert('댓글이 삭제되었습니다');
    }).catch(error => {
        console.error('Error:', error);
        alert('댓글 삭제에 실패했습니다');
    });
}
