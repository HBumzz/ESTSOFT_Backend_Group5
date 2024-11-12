// header.js
function checkPointsForChecklist(userPoint) {
    if (userPoint >= 500) {
        location.href = '/checklist';
    } else {
        alert('포인트 500점을 쌓아야합니다');
    }
}
