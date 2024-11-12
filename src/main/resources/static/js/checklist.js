const DateUtils = {
    formatDate(date) {
        return date.toISOString().split('T')[0];
    },

    isPastDate(type, targetDate) {
        const now = new Date();
        const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        const target = new Date(targetDate);

        switch(type) {
            case 'DAILY':
                return target < today;
            case 'WEEKLY': {
                // 현재 날짜의 월요일 찾기
                const currentMonday = new Date(now);
                currentMonday.setDate(now.getDate() - now.getDay() + (now.getDay() === 0 ? -6 : 1));
                currentMonday.setHours(0, 0, 0, 0);

                // 선택한 날짜의 월요일 찾기
                const targetMonday = new Date(target);
                while (targetMonday.getDay() !== 1) {
                    targetMonday.setDate(targetMonday.getDate() - 1);
                }
                targetMonday.setHours(0, 0, 0, 0);

                // 월요일 기준으로 비교
                return targetMonday < currentMonday;
            }
            case 'MONTHLY': {
                const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
                return new Date(target.getFullYear(), target.getMonth(), 1) < startOfMonth;
            }
            default:
                return false;
        }
    },

    formatDisplayDate(date, type) {
        const days = ['일', '월', '화', '수', '목', '금', '토'];

        switch (type) {
            case 'DAILY': {
                const month = date.getMonth() + 1;
                const day = date.getDate();
                const dayOfWeek = days[date.getDay()];
                return `${month}월 ${day}일 (${dayOfWeek})`;
            }
            case 'WEEKLY': {
                const month = date.getMonth() + 1;
                const weekNumber = this.getWeekOfMonth(date);
                return `${month}월 ${weekNumber}주차`;
            }
            case 'MONTHLY': {
                const month = date.getMonth() + 1;
                return `${month}월`;
            }
            default:
                return '';
        }
    },

    getWeekOfMonth(date) {
        const firstDayOfMonth = new Date(date.getFullYear(), date.getMonth(), 1);
        const firstMonday = new Date(firstDayOfMonth);
        while (firstMonday.getDay() !== 1) {
            firstMonday.setDate(firstMonday.getDate() + 1);
        }
        return Math.ceil((date.getDate() - firstMonday.getDate() + 1) / 7);
    },

    // DatePickerUtils의 기능을 통합
    createYearOptions(select, currentYear) {
        select.innerHTML = '';
        for (let year = currentYear - 5; year <= currentYear + 5; year++) {
            select.add(new Option(year + '년', year));
        }
        select.value = currentYear;
    },

    createMonthOptions(select, currentMonth) {
        select.innerHTML = '';
        for (let month = 1; month <= 12; month++) {
            select.add(new Option(month + '월', month));
        }
        select.value = currentMonth;
    },

    getWeeksInMonth(year, month) {
        const firstDay = new Date(year, month - 1, 1);
        const lastDay = new Date(year, month, 0);
        const firstWeekday = firstDay.getDay();
        return Math.ceil((lastDay.getDate() + firstWeekday) / 7);
    }
};

/**
 * 모달 관련 유틸리티
 */
const ModalUtils = {
    show(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            if (this.validateModalAccess(modalId)) {
                modal.style.display = 'block';
            }
        }
    },

    validateModalAccess(modalId) {
        if (modalId === 'addItemModal') {
            const currentType = document.querySelector('.type-tab.active').textContent.trim();
            const targetDate = document.querySelector('.date-display span').textContent;
            const parsedDate = this.parseDisplayDate(targetDate, currentType);

            if (DateUtils.isPastDate(currentType, parsedDate)) {
                alert('지나간 날짜는 수정할 수 없습니다.');
                return false;
            }
        }
        return true;
    },

    close(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'none';
            const form = modal.querySelector('form');
            if (form) form.reset();
        }
    },

    parseDisplayDate(displayDate, type) {
        const now = new Date();
        const match = displayDate.match(/(\d+)월/);
        const month = match ? parseInt(match[1]) - 1 : now.getMonth();
        const year = now.getFullYear();

        switch(type) {
            case 'DAILY': {
                const dayMatch = displayDate.match(/(\d+)일/);
                const day = dayMatch ? parseInt(dayMatch[1]) : now.getDate();
                return new Date(year, month, day);
            }
            case 'WEEKLY': {
                // 주차 정보 파싱
                const weekMatch = displayDate.match(/(\d+)주차/);
                if (weekMatch) {
                    const weekNumber = parseInt(weekMatch[1]);
                    // 해당 월의 첫 날
                    const firstDay = new Date(year, month, 1);
                    // 첫 번째 월요일 찾기
                    let firstMonday = new Date(firstDay);
                    while (firstMonday.getDay() !== 1) {
                        firstMonday.setDate(firstMonday.getDate() + 1);
                    }
                    // 선택한 주차의 월요일로 설정
                    const targetDate = new Date(firstMonday);
                    targetDate.setDate(firstMonday.getDate() + (weekNumber - 1) * 7);
                    return targetDate;
                }
                return now;
            }
            case 'MONTHLY':
                return new Date(year, month, 1);
            default:
                return now;
        }
    }
};

function showAddItemForm() {
    ModalUtils.show('addItemModal');
}

async function submitAddItemForm(event) {
    event.preventDefault();

    const formData = {
        checklistId: document.getElementById('currentChecklistId').value,
        itemContent: document.getElementById('itemContent').value,
        categoryType: document.getElementById('categoryType').value,
        savedAmount: Math.floor(parseFloat(document.getElementById('savedAmount').value)),
        itemMemo: document.getElementById('itemMemo').value || null,
    };

    try {
        const response = await fetch('/api/checklists/items', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || '항목 추가에 실패했습니다.');
        }

        ModalUtils.close('addItemModal');
        location.reload();
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}

function editItem(itemId) {
    fetch(`/api/checklists/items/${itemId}`)
        .then(response => response.json())
        .then(item => {
            document.getElementById('editItemId').value = itemId;
            document.getElementById('editItemContent').value = item.itemContent;
            document.getElementById('editSavedAmount').value = item.savedAmount;
            document.getElementById('editItemMemo').value = item.itemMemo || '';
            ModalUtils.show('editItemModal');
        })
        .catch(error => {
            console.error('Error loading item:', error);
            alert('아이템 정보를 불러오는데 실패했습니다.');
        });
}


// 아이템 삭제
async function deleteItem(itemId) {
    if (confirm('정말 삭제하시겠습니까?')) {
        try {
            const response = await fetch(`/api/checklists/items/${itemId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error('삭제에 실패했습니다.');
            }

            location.reload();
        } catch (error) {
            console.error('Error:', error);
            alert(error.message);
        }
    }
}

// 완료 상태 토글
async function toggleComplete(itemId) {
    try {
        const response = await fetch(`/api/checklists/items/${itemId}/complete`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('상태 변경에 실패했습니다.');
        }

        location.reload();
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}

function submitEditItemForm(event) {
    event.preventDefault();
    console.log('Submit edit form called');

    const itemId = document.getElementById('editItemId').value;

    const formData = {
        itemContent: document.getElementById('editItemContent').value,
        savedAmount: Math.floor(parseFloat(document.getElementById('editSavedAmount').value)),
        itemMemo: document.getElementById('editItemMemo').value || null
    };

    console.log('Sending data:', formData);

    // 페이지 새로고침 전에 요청 완료를 기다림
    fetch(`/api/checklists/items/${itemId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify(formData)
    })
        .then(response => {
            console.log('Response status:', response.status);
            if (!response.ok) {
                return response.json().then(err => {
                    throw new Error(err.message || '수정에 실패했습니다.');
                });
            }
            return response.json();
        })
        .then(data => {
            console.log('Update successful:', data);
            alert('수정이 완료되었습니다.');
            location.reload();  // 성공 후 새로고침
        })
        .catch(error => {
            console.error('Error updating item:', error);
            alert('수정 중 오류가 발생했습니다: ' + error.message);
        });

    return false;
}

document.getElementById('addItemForm')?.addEventListener('reset', function() {
    setTimeout(() => {
        ModalUtils.close('addItemModal');
    }, 100);
});

document.getElementById('editItemForm')?.addEventListener('reset', function() {
    setTimeout(() => {
        ModalUtils.close('editItemModal');
    }, 100);
});


function showDatePicker() {
    ModalUtils.show('datePickerModal');
    initializeDatePicker();
}


const DatePickerUtils = {
    createYearOptions(select, currentYear) {
        select.innerHTML = '';
        for (let year = currentYear - 5; year <= currentYear + 5; year++) {
            select.add(new Option(year + '년', year));
        }
        select.value = currentYear;
    },

    createMonthOptions(select, currentMonth) {
        select.innerHTML = '';
        for (let month = 1; month <= 12; month++) {
            select.add(new Option(month + '월', month));
        }
        select.value = currentMonth;
    },

    // 주차 계산 로직 수정
    getCurrentWeek(date) {
        const firstDayOfMonth = new Date(date.getFullYear(), date.getMonth(), 1);
        const firstWeekday = firstDayOfMonth.getDay();
        const currentDate = date.getDate();

        // 1일이 속한 주의 처리
        if (currentDate <= (7 - firstWeekday)) {
            return 1;
        }

        return Math.ceil((currentDate + firstWeekday) / 7);
    },

    // 해당 월의 총 주차 수 계산
    getWeeksInMonth(year, month) {
        const lastDay = new Date(year, month, 0);
        const firstDayOfMonth = new Date(year, month - 1, 1);
        const firstWeekday = firstDayOfMonth.getDay();

        return Math.ceil((lastDay.getDate() + firstWeekday) / 7);
    }
};

function getSelectedDate(type) {
    switch(type) {
        case 'DAILY':
            return document.getElementById('dailyDate')?.value || DateUtils.formatDate(new Date());
        case 'WEEKLY':
            const weekYear = document.getElementById('weeklyYear')?.value;
            const weekMonth = document.getElementById('weeklyMonth')?.value;
            const week = document.getElementById('weeklyWeek')?.value;
            if (weekYear && weekMonth && week) {
                // 주의 시작일 계산
                const firstDayOfMonth = new Date(weekYear, weekMonth - 1, 1);
                let weekStart = new Date(firstDayOfMonth);
                while (weekStart.getDay() !== 1) { // 월요일이 될 때까지
                    weekStart.setDate(weekStart.getDate() + 1);
                }
                weekStart.setDate(weekStart.getDate() + (week - 1) * 7);
                return DateUtils.formatDate(weekStart);
            }
            return DateUtils.formatDate(new Date());
        case 'MONTHLY':
            const monthYear = document.getElementById('monthlyYear')?.value;
            const month = document.getElementById('monthlyMonth')?.value;
            if (monthYear && month) {
                return DateUtils.formatDate(new Date(monthYear, month - 1, 1));
            }
            return DateUtils.formatDate(new Date());
        default:
            return DateUtils.formatDate(new Date());
    }
}

const DateDisplayUtils = {
    parseDisplayDate() {
        const displayDate = document.querySelector('.date-display span').textContent;
        const currentDate = new Date();
        let result = {
            year: currentDate.getFullYear(),
            month: currentDate.getMonth() + 1,
            day: currentDate.getDate()
        };

        // 일간 형식 (11월 12일)
        let match = displayDate.match(/(\d+)월 (\d+)일/);
        if (match) {
            result.month = parseInt(match[1]);
            result.day = parseInt(match[2]);
            return result;
        }

        // 주간 형식 (11월 3주차)
        match = displayDate.match(/(\d+)월\s+(\d+)주차/);
        if (match) {
            result.month = parseInt(match[1]);
            result.week = parseInt(match[2]);
            return result;
        }

        // 월간 형식 (11월)
        match = displayDate.match(/(\d+)월/);
        if (match) {
            result.month = parseInt(match[1]);
            return result;
        }

        return result;
    }
};

function initializeDatePicker() {
    const selectedType = document.querySelector('.type-tab.active').textContent.trim();
    const currentDate = new Date();
    const displayDate = DateDisplayUtils.parseDisplayDate();

    // 표시된 날짜로 Date 객체 생성
    const selectedDate = new Date(
        displayDate.year,
        displayDate.month - 1,
        displayDate.day || 1
    );

    document.querySelectorAll('.date-picker-section').forEach(section => {
        section.style.display = 'none';
    });

    const pickerElement = document.getElementById(`${selectedType.toLowerCase()}-picker`);
    if (pickerElement) {
        pickerElement.style.display = 'block';

        switch(selectedType) {
            case 'DAILY':
                initializeDailyPicker(selectedDate);
                break;
            case 'WEEKLY':
                initializeWeeklyPicker(selectedDate);
                break;
            case 'MONTHLY':
                initializeMonthlyPicker(selectedDate);
                break;
        }
    }
}

function initializeDailyPicker(currentDate) {
    const dateInput = document.getElementById('dailyDate');
    if (dateInput) {
        // 현재 표시된 날짜 가져오기
        const displayDate = document.querySelector('.date-display span').textContent;
        const match = displayDate.match(/(\d+)월 (\d+)일/);

        if (match) {
            // timezone 오프셋을 고려한 날짜 설정
            const year = currentDate.getFullYear();
            const month = parseInt(match[1]) - 1;
            const day = parseInt(match[2]);

            const selectedDate = new Date(year, month, day);
            const timezoneOffset = selectedDate.getTimezoneOffset() * 60000;
            const adjustedDate = new Date(selectedDate.getTime() - timezoneOffset);

            dateInput.value = adjustedDate.toISOString().split('T')[0];
        } else {
            // 현재 날짜를 사용할 경우도 동일하게 timezone 고려
            const timezoneOffset = currentDate.getTimezoneOffset() * 60000;
            const adjustedDate = new Date(currentDate.getTime() - timezoneOffset);
            dateInput.value = adjustedDate.toISOString().split('T')[0];
        }
    }
}

function initializeWeeklyPicker(currentDate) {
    const yearSelect = document.getElementById('weeklyYear');
    const monthSelect = document.getElementById('weeklyMonth');

    // 현재 표시된 날짜 가져오기
    const displayDate = document.querySelector('.date-display span').textContent;
    const match = displayDate.match(/(\d+)월\s+(\d+)주차/);
    let selectedWeek = 1;

    if (match) {
        selectedWeek = parseInt(match[2]);
    }

    // 년도, 월 초기화
    DatePickerUtils.createYearOptions(yearSelect, currentDate.getFullYear());
    DatePickerUtils.createMonthOptions(monthSelect, currentDate.getMonth() + 1);

    // 주차 옵션 초기화
    const weekSelect = document.getElementById('weeklyWeek');
    weekSelect.innerHTML = '';

    const weeksInMonth = DatePickerUtils.getWeeksInMonth(currentDate.getFullYear(), currentDate.getMonth() + 1);

    for (let week = 1; week <= weeksInMonth; week++) {
        weekSelect.add(new Option(week + '주차', week));
    }

    // 현재 표시된 주차로 설정
    weekSelect.value = selectedWeek;

    // 이벤트 리스너
    yearSelect.addEventListener('change', updateWeeksForYearMonth);
    monthSelect.addEventListener('change', updateWeeksForYearMonth);
}

function initializeMonthlyPicker(currentDate) {
    const yearSelect = document.getElementById('monthlyYear');
    const monthSelect = document.getElementById('monthlyMonth');

    // 현재 표시된 날짜 가져오기
    const displayDate = document.querySelector('.date-display span').textContent;
    const match = displayDate.match(/(\d+)월/);
    let selectedMonth = currentDate.getMonth() + 1;

    if (match) {
        selectedMonth = parseInt(match[1]);
    }

    // 년도 초기화
    DatePickerUtils.createYearOptions(yearSelect, currentDate.getFullYear());

    // 월 초기화
    DatePickerUtils.createMonthOptions(monthSelect, currentDate.getMonth() + 1);

    // 표시된 월로 설정
    monthSelect.value = selectedMonth;
}


function updateWeeksForYearMonth() {
    const yearSelect = document.getElementById('weeklyYear');
    const monthSelect = document.getElementById('weeklyMonth');
    const weekSelect = document.getElementById('weeklyWeek');

    // 현재 선택된 주차 저장
    const currentWeek = weekSelect.value;

    const selectedDate = new Date(yearSelect.value, monthSelect.value - 1, 1);
    const weeksInMonth = DatePickerUtils.getWeeksInMonth(selectedDate.getFullYear(), selectedDate.getMonth() + 1);

    weekSelect.innerHTML = '';
    for (let week = 1; week <= weeksInMonth; week++) {
        weekSelect.add(new Option(week + '주차', week));
    }

    // 이전에 선택된 주차가 새로운 월의 주차 범위 내에 있으면 그대로 유지
    if (currentWeek <= weeksInMonth) {
        weekSelect.value = currentWeek;
    } else {
        weekSelect.value = 1;  // 범위를 벗어나면 1주차로 설정
    }
}

function updateWeekOptions(date) {
    const weekSelect = document.getElementById('weeklyWeek');
    weekSelect.innerHTML = '';

    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const totalWeeks = DatePickerUtils.getWeeksInMonth(year, month);

    for (let week = 1; week <= totalWeeks; week++) {
        weekSelect.add(new Option(week + '주차', week));
    }

    // 현재 선택된 날짜의 주차 계산
    const currentWeek = DatePickerUtils.getCurrentWeek(date);
    weekSelect.value = currentWeek;
}

function applyDateFilter() {
    const selectedType = document.querySelector('.type-tab.active').textContent.trim();
    let url = '/checklist?type=' + selectedType;

    let selectedDate;
    switch(selectedType) {
        case 'DAILY': {
            const dailyDate = document.getElementById('dailyDate').value;
            if (dailyDate) {
                url += `&date=${dailyDate}`;
                selectedDate = new Date(dailyDate);
            }
            break;
        }
        case 'WEEKLY': {
            const year = document.getElementById('weeklyYear').value;
            const month = document.getElementById('weeklyMonth').value;
            const week = document.getElementById('weeklyWeek').value;
            url += `&year=${year}&month=${month}&week=${week}`;

            // 해당 주의 월요일 계산
            const firstDayOfMonth = new Date(year, month - 1, 1);
            let weekStart = new Date(firstDayOfMonth);
            while (weekStart.getDay() !== 1) {
                weekStart.setDate(weekStart.getDate() + 1);
            }
            weekStart.setDate(weekStart.getDate() + (week - 1) * 7);
            selectedDate = weekStart;
            break;
        }
        case 'MONTHLY': {
            const year = document.getElementById('monthlyYear').value;
            const month = document.getElementById('monthlyMonth').value;
            url += `&year=${year}&month=${month}`;
            selectedDate = new Date(year, month - 1, 1);
            break;
        }
    }

    // 날짜 표시 업데이트
    if (selectedDate) {
        const displayElement = document.querySelector('.date-display span');
        if (displayElement) {
            displayElement.textContent = DateUtils.formatDisplayDate(selectedDate, selectedType);
        }
    }

    window.location.href = url;
}

document.addEventListener('DOMContentLoaded', function() {
    ModalUtils.setupListeners();
});

