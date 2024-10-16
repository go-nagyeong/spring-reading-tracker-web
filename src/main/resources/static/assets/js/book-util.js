/**
 * 초기 로드 시 사용자 컬렉션 목록 불러와서 캐싱
 */
let cachedUserCollectionList = null;

async function loadUserCollectionList() {
    if (!cachedUserCollectionList) {
        try {
            const response = await axios.get('/api/collections/me');
            cachedUserCollectionList = response.data.data;
        } catch (error) {
            console.error('axios 요청 오류:', error);
            showToast('알 수 없는 오류가 발생했습니다.', 'error');
        }
    }
    return cachedUserCollectionList;
}

function invalidateUserCollectionListCache() {
    cachedUserCollectionList = null;
}


/**
 * 사용자 독서 목록 / 컬렉션에 책 추가
 */
function handleReadingBook(action, data, targetElement) {
    globalLoading(true);

    let promise;
    switch (action) {
        case 'C':
            promise = axios.post('/api/reading-books', data);
            break;
        case 'U':
            promise = axios.put(`/api/reading-books/${data.rdId}`, data);
            break;
        case 'D':
            promise = axios.delete(`/api/reading-books/${data.rdId}`);
            break;
        default:
            throw new Error('Invalid action type');
    }

    const onSuccess = (result) => {
        const data = result.data;

        onUpdateReadingInfo(data?.saveResult || {}, targetElement);
        if (data.hasOwnProperty('readerCount')) {
            updateBookStatistics(data, targetElement);
        }
        if (data.hasOwnProperty('userReadCount')) {
            updateUserBookStatistics(data, targetElement);
        }
        closeUIComponents();

        if (action === 'D' && typeof commonReloadOnDelete == 'function') {
            commonReloadOnDelete();
        }
    };

    handleApiResponse(promise, onSuccess);
}

/**
 * [독서 목록에 추가] 버튼 컴포넌트 초기화 (UI 업데이트 및 이벤트 세팅)
 */
function updateReadingStatusButtonUI(status, targetElement) {
    const buttonGroup = targetElement.querySelector('.reading-btn-group');

    const toggleButton = buttonGroup.querySelector('button.reading-list-btn');
    const toggleButtonIcon = buttonGroup.querySelector('i');
    const toggleButtonText = buttonGroup.querySelector('span');
    const statusButtonList = buttonGroup.querySelector('ul.dropdown-menu');

    // 이전 독서 상태 비활성화
    const activeStatusItem = statusButtonList.querySelector('.dropdown-item.active');
    if (activeStatusItem) {
        activeStatusItem.classList.remove('active');
    }

    // 독서 상태에 따른 버튼 UI 변경
    if (toggleButton.classList.contains('filter-btn')) { // filter 버튼
        toggleButton.classList.toggle('active', status);
    } else { // action 버튼
        toggleButton.classList.toggle('btn-primary', !status);
        toggleButton.classList.toggle('btn-secondary', status);
    }

    if (status) {
        const currentStatusItem = statusButtonList.querySelector(`[data-value="${status}"]`);
        if (!currentStatusItem) return;
        currentStatusItem.classList.add('active');

        if (toggleButtonIcon) {
            toggleButtonIcon.toggle(true);
            toggleButtonIcon.classList.replace('bxs-bookmark-plus', 'bx-check');
            toggleButtonIcon.classList.add('text-success');
        }
        toggleButtonText.toggle(!toggleButton.classList.contains('btn-icon'));
        toggleButtonText.textContent = currentStatusItem.textContent;
    } else {
        if (toggleButtonIcon) {
            toggleButtonIcon.toggle(toggleButton.classList.contains('btn-icon'));
            toggleButtonIcon.classList.replace('bx-check', 'bxs-bookmark-plus');
            toggleButtonIcon.classList.remove('text-success');
        }
        toggleButtonText.toggle(!toggleButton.classList.contains('btn-icon'));
        toggleButtonText.textContent = toggleButtonText.dataset.content;
    }

    // 독서 상태 선택 목록 변경
    const visibleItemsOnlyAdded = statusButtonList.querySelectorAll('li.add-visible');
    visibleItemsOnlyAdded.forEach(el => {
        el.style.display = status ? 'block' : 'none';
    })

    // 버튼 이벤트 세팅
    setReadingStatusButtonEvent();
}

function setReadingStatusButtonEvent() {
    const readingStatusButtonList = document.querySelectorAll('.reading-btn-group.action .dropdown-item');

    readingStatusButtonList.forEach(el => {
        el.addEventListener('click', function (e) {
            e.stopImmediatePropagation();
            const targetElement = el.closest('.dataset-el');
            const originData = JSON.parse(JSON.stringify(targetElement.dataset));

            if (el.classList.contains('delete-btn')) {
                const content = '독서 목록에서 제거 시 관련 데이터가 모두 삭제됩니다.<br/>' +
                    '삭제된 데이터는 복구가 불가능합니다.<br/>' +
                    '그래도 제거하시겠습니까?<br/><br/>' +
                    '※ 삭제 데이터: 독서 노트, 완독 이력, 타이머 기록<br/>' +
                    '리뷰와 책 구매/대여 이력은 삭제되지 않습니다.';
                confirmDeleteModal(content)
                    .then(() => {
                        handleReadingBook('D', originData, targetElement);
                    });

            } else {
                originData['rdSttus'] = el.dataset.value;
                handleReadingBook(originData['rdId'] ? 'U' : 'C', originData, targetElement);
            }
        });
    })
}

/**
 * [컬렉션에 추가] 버튼에 사용자 컬렉션 목록 세팅
 */
function setCollectionButtonOptions(collectionList, targetElement = document) {
    const collectionButtonGroups = targetElement.querySelectorAll('.collection-btn-group');

    for (const buttonGroup of collectionButtonGroups) {
        const collectionButtonList = buttonGroup.querySelector('ul.dropdown-menu');
        // 컬렉션 목록 추가
        for (const collection of collectionList) {
            collectionButtonList.insertAdjacentHTML('afterbegin', `
                <li>
                    <a class="dropdown-item" role="button" data-value="${collection.id}">
                        ${collection.collectionName}
                    </a>
                </li>
            `)
        }
    }
}

/**
 * [컬렉션에 추가] 버튼 컴포넌트 초기화 (UI 업데이트 및 이벤트 세팅)
 */
async function updateCollectionButtonUI(collectionId, targetElement) {
    const buttonGroup = targetElement.querySelector('.collection-btn-group');

    const toggleButton = buttonGroup.querySelector('button.collection-btn');
    const toggleButtonIcon = buttonGroup.querySelector('i');
    const toggleButtonText = buttonGroup.querySelector('span');
    const collectionButtonList = buttonGroup.querySelector('ul.dropdown-menu');

    // 이전 컬렉션 비활성화
    const activeCollectionItem = collectionButtonList.querySelector('.dropdown-item.active');
    if (activeCollectionItem) {
        activeCollectionItem.classList.remove('active');
    }

    // 컬렉션에 따른 버튼 UI 변경
    if (toggleButton.classList.contains('filter-btn')) { // filter 버튼
        toggleButton.classList.toggle('active', collectionId);
    } else { // action 버튼
        toggleButton.classList.toggle('btn-outline-primary', !collectionId);
        toggleButton.classList.toggle('btn-secondary', collectionId);
    }

    if (collectionId) {
        const currentCollectionItem = collectionButtonList.querySelector(`[data-value="${collectionId}"]`);
        if (!currentCollectionItem) return;
        currentCollectionItem.classList.add('active');

        if (toggleButtonIcon) {
            toggleButtonIcon.toggle(true);
            toggleButtonIcon.className = toggleButtonIcon.className.replace('plus', 'minus');
            toggleButtonIcon.classList.add('text-success');
        }
        toggleButtonText.toggle(!toggleButton.classList.contains('btn-icon'));
        toggleButtonText.textContent = currentCollectionItem.textContent;
    } else {
        if (toggleButtonIcon) {
            toggleButtonIcon.toggle(toggleButton.classList.contains('btn-icon'));
            toggleButtonIcon.className = toggleButtonIcon.className.replace('minus', 'plus');
            toggleButtonIcon.classList.remove('text-success');
        }
        toggleButtonText.toggle(!toggleButton.classList.contains('btn-icon'));
        toggleButtonText.textContent = toggleButtonText.dataset.content;
    }

    // 컬렉션 선택 목록 변경
    const visibleItemsOnlyAdded = collectionButtonList.querySelectorAll('li.add-visible');
    visibleItemsOnlyAdded.forEach(el => {
        el.style.display = collectionId ? 'block' : 'none';
    })

    // 버튼 이벤트 세팅
    setCollectionButtonEvent();
}

function setCollectionButtonEvent() {
    const collectionButtonList = document.querySelectorAll('.collection-btn-group.action .dropdown-item');
    collectionButtonList.forEach(el => {
        el.addEventListener('click', function (e) {
            e.stopImmediatePropagation();
            const targetElement = el.closest('.dataset-el');
            const originData = JSON.parse(JSON.stringify(targetElement.dataset));

            if (el.classList.contains('delete-btn')) {
                originData['colId'] = null;
                handleReadingBook('U', originData, targetElement);

            } else if (el.classList.contains('new-collection-modal-btn')) {
                // 새 컬렉션 생성 모달 버튼
                const target = document.querySelector(el.dataset.bsTarget + ' .modal-content');
                loadHTML('/my/partials/modal-collection-form', target);

            } else {
                originData['colId'] = el.dataset.value;
                originData['rdSttus'] ||= "TO_READ"; // 기본값: 읽을 예정
                handleReadingBook(originData['rdId'] ? 'U' : 'C', originData, targetElement);
            }
        });
    })
}

/**
 * 독서 상태에 따라 [책 상세 > 내 독서 관리 > 상단 Carousel 카드] UI 업데이트
 */
function updateReadingCarouselCardUI(readingStatus) {
    const carousel = document.getElementById('readingStatusCarouselCard');
    if (carousel) {
        const carouselInner = carousel.querySelector('.carousel-inner');
        carousel.style.display = 'block';

        if (readingStatus === 'READING' || readingStatus === 'READ') {
            let url, callback = null;
            if (readingStatus === 'READING') {
                callback = () => setReadingRecordCardData();
                url = '/books/partials/reading-status-card';
            } else {
                callback = () => setReadRecordCardData();
                url = '/books/partials/read-status-card';
            }
            loadHTML(url, carouselInner, callback);
        } else {
            carousel.style.display = 'none';
        }
    }
}
// (위 Carousel 카드 콘텐츠) 최근 독서 이력
function setReadingRecordCardData() {
    const targetElement = document.querySelector('.dataset-el')
    const readingId = targetElement.dataset.rdId;

    const promise = axios.get(`/api/reading-records/books/${readingId}/me/latest`);

    const onSuccess = (result) => {
        const latestReadingRecord = result.data;
        const cardEl = document.getElementById('readingDDayCard');

        cardEl.querySelector('.start-date').textContent = latestReadingRecord.startDate;
        cardEl.querySelector('.d-day').textContent = latestReadingRecord.dday;
    }

    handleApiResponse(promise, onSuccess);
}
// (위 Carousel 카드 콘텐츠) 완독 독서 이력
function setReadRecordCardData() {
    const targetElement = document.querySelector('.dataset-el')
    const readingId = targetElement.dataset.rdId;

    const promise = axios.get(`/api/reading-records/books/${readingId}/me/latest/completed`);

    const onSuccess = (result) => {
        const { latestReadRecord, readCount } = result.data;
        const cardEl = document.getElementById('readCountCard');

        cardEl.querySelector('.start-date').textContent = latestReadRecord.startDate;
        cardEl.querySelector('.end-date').textContent = latestReadRecord.endDate;
        cardEl.querySelector('.record-cnt').textContent = readCount;
    }

    handleApiResponse(promise, onSuccess);
}


/**
 * 책 통계 데이터 업데이트 (독자 수, 리뷰 수, 평균 평점)
 */
function updateBookStatistics(data, targetElement) {
    const statisticsWraps = targetElement.querySelectorAll('.info-rating'); // 반응형 엘리먼트 2개

    for (const statisticsWrap of statisticsWraps) {
        const readerCntEl = statisticsWrap.querySelector('em.sale-num, .sale-num em');
        const reviewCntEl = statisticsWrap.querySelector('em.rating-rv-count, .rating-rv-count em');
        const ratingEl = statisticsWrap.querySelector('select.star-rating');

        if (data.hasOwnProperty('readerCount') && readerCntEl) {
            readerCntEl.textContent = data.readerCount;
        }
        if (data.hasOwnProperty('reviewCount') && reviewCntEl) {
            reviewCntEl.textContent = data.reviewCount;
        }

        if (data.hasOwnProperty('averageRating') && ratingEl) {
            if (data.averageRating < 1) {
                ratingEl.parentElement.innerHTML = '';
            } else {
                ratingEl.dataset.value = data.averageRating.toFixed(2);
                initializeStarRating(ratingEl);
            }
        }

    }
}

/**
 * 책 통계 데이터 업데이트 (작성 평점, 노트 수, 완독 수)
 */
function updateUserBookStatistics(data, targetElement) {
    const statisticsWrap = targetElement.querySelector('.info-rating');
    const readCntEl = statisticsWrap.querySelector('em.read-cnt');
    const noteCntEl = statisticsWrap.querySelector('em.note-cnt');
    const ratingElements = statisticsWrap.querySelectorAll('select.star-rating'); // 반응형 엘리먼트 2개

    if (data.hasOwnProperty('userReadCount') && readCntEl) {
        readCntEl.textContent = data.userReadCount;
    }
    if (data.hasOwnProperty('userNoteCount') && noteCntEl) {
        noteCntEl.textContent = data.userNoteCount;
    }

    if (data.hasOwnProperty('userRating')) {
        for (const ratingEl of ratingElements) {
            if (data.userRating < 1) {
                ratingEl.parentElement.innerHTML = '';
            } else {
                ratingEl.dataset.value = data.userRating;
                initializeStarRating(ratingEl)
            }
        }
    }
}

/**
 * 로그인 유저 컬렉션 목록 가져오기
 */
async function getUserCollectionList() {
    const promise = axios.get('/api/collections/me');

    let collectionList = null;

    await new Promise((resolve, reject) => {
        const onSuccess = (result) => {
            collectionList = result.data;
            resolve();
        };

        handleApiResponse(promise, onSuccess);
    })

    return collectionList;
}

/**
 * 독서 상태 변경시 공통 업데이트 사항
 */
function onUpdateReadingInfo(readingInfo, targetElement) {
    // MEMO: ?. => readingInfo 파라미터는 null 일 수도 있음
    // 독서 상태 데이터 저장
    targetElement.dataset.rdId = readingInfo?.id || "";
    targetElement.dataset.rdSttus = readingInfo?.readingStatus || "";
    targetElement.dataset.colId = readingInfo?.collectionId || "";

    // 독서 상태에 따른 UI 변경
    updateReadingStatusButtonUI(readingInfo?.readingStatus, targetElement);
    updateCollectionButtonUI(readingInfo?.collectionId, targetElement);
    updateReadingCarouselCardUI(readingInfo?.readingStatus);
}

/**
 * 컬렉션 생성 시 공통 업데이트 사항
 */
function onCreateCollection(newCollection) {
    setCollectionButtonOptions([newCollection]); // 컬렉션 목록 다시 세팅
    setCollectionButtonEvent(); // 버튼 이벤트 세팅
    if (typeof setFilterButtonEvent == 'function') { // 컬렉션 필터 목록도 다시 세팅
        setFilterButtonEvent();
    }
}
