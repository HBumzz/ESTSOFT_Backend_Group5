// 수정 버튼 클릭 이벤트
const updateUserButton = document.getElementById('update-user-btn');

if (updateUserButton) {
    updateUserButton.addEventListener('click', event => {
        const userId = document.getElementById('user-id').value;  // 유저 ID 가져오기

        fetch(`/api/admin/users/update`, {  // 수정할 유저의 API 엔드포인트
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                userId: userId,  // 유저 ID
                point: document.getElementById('points').value,  // 포인트 입력 값
                activated: document.getElementById('active').checked  // 활성화 상태 체크박스
            })
        }).then(response => {
            if (response.ok) {
                alert('유저 정보 수정이 완료되었습니다');
                location.reload();  // 페이지 새로고침으로 반영
            } else {
                alert('유저 정보 수정에 실패했습니다');
            }
        }).catch(error => {
            console.error('Error:', error);
            alert('오류가 발생했습니다');
        });
    });
}
