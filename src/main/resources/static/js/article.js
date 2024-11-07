const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;
        fetch(`/api/article/${id}`, {
            method: 'DELETE'
        }).then(() => {
            alert('삭제가 완료되었습니다');
            location.replace('/board');
        });
    });
}

const modifyButton = document.getElementById('modify-btn');

if(modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        fetch(`api/article/${id}`,{
            method:'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        }).then(()=>{
            alert('수정 완료');
            location.replace(`/article/${id}`);
        });
    });
}

const createButton = document.getElementById('create-btn');

createButton.addEventListener('click', event => {
    fetch(`api/article`, {
        method:'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            header: document.getElementById('header').value,
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
        })
    }).then(() => {
        alert('게시물 생성 완료');
        location.replace(`/board`);
    });
});