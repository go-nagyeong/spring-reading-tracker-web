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
            promise = axios.put(`/api/reading-books/${data.id}`, data);
            break;
        case 'D':
            promise = axios.delete(`/api/reading-books/${data.id}`);
            break;
        default:
            throw new Error('Invalid action type');
    }

    const onSuccess = (result) => {
        onUpdateReadingInfo(result.data, targetElement);
        closeUIComponents();
    };

    handleApiResponse(promise, onSuccess);
}

/**
 * [독서 목록에 추가] 버튼 UI 업데이트
 */
function updateReadingListButtonUI(status, targetElement) {
    const readingButtonGroups = targetElement.querySelectorAll('.reading-btn-group'); // 반응형 엘리먼트 2개

    for (const buttonGroup of readingButtonGroups) {
        const addButton = buttonGroup.querySelector('button.reading-list-btn');
        const statusButtonList = buttonGroup.querySelector('ul.dropdown-menu');

        // 이전 독서 상태 비활성화
        const activeStatusItem = statusButtonList.querySelector('.dropdown-item.active');
        if (activeStatusItem) {
            activeStatusItem.classList.remove('active');
        }

        // 독서 상태에 따른 버튼 UI 변경
        if (status) {
            const currentStatusItem = statusButtonList.querySelector(`[data-value="${status}"]`);
            currentStatusItem.classList.add('active');
            addButton.classList.replace('btn-primary', 'btn-secondary');

            let buttonContent = '<i class="bx bx-check text-success"></i>';
            if (!addButton.classList.contains('btn-icon')) {
                buttonContent += `<span class="ms-1">${currentStatusItem.textContent}</span>`;
            }
            addButton.innerHTML = buttonContent;
        } else {
            addButton.classList.replace('btn-secondary', 'btn-primary');

            if (addButton.classList.contains('btn-icon')) {
                addButton.innerHTML = '<i class="bx bxs-bookmark-plus"></i>';
            } else {
                addButton.innerHTML = '독서 목록에 추가';
            }
        }

        // 독서 상태 선택 목록 변경
        const visibleItemsOnlyAdded = statusButtonList.querySelectorAll('li.add-visible');
        visibleItemsOnlyAdded.forEach(el => {
            el.style.display = status ? 'block' : 'none';
        })
    }
}

/**
 * [독서 목록에 추가] 버튼 이벤트 세팅
 */
function setReadingListButtonEvent() {
    const readingStatusButtonList = document.querySelectorAll('.reading-btn-group .dropdown-item');
    readingStatusButtonList.forEach(el => {
        el.addEventListener('click', function (e) {
            e.stopImmediatePropagation();
            const targetElement = el.closest('.dataset-el');
            const originData = JSON.parse(JSON.stringify(targetElement.dataset));

            if (el.classList.contains('delete-btn')) {
                handleReadingBook('D', originData, targetElement);

            } else {
                originData['readingStatus'] = el.dataset.value;
                handleReadingBook(originData.id ? 'U' : 'C', originData, targetElement);
            }
        });
    })
}

/**
 * [컬렉션에 추가] 버튼에 사용자 컬렉션 목록 세팅
 */
function setCollectionButtonOptions(collectionList, targetElement = document) {
    const collectionButtonGroups = targetElement.querySelectorAll('.collection-btn-group'); // 반응형 엘리먼트 2개

    for (const buttonGroup of collectionButtonGroups) {
        const collectionButtonList = buttonGroup.querySelector('ul.dropdown-menu');

        // 컬렉션 목록 추가
        for (const collection of collectionList) {
            collectionButtonList.insertAdjacentHTML('afterbegin', `
                <li>
                    <a class="dropdown-item" href="javascript:void(0);" data-value="${collection.id}">
                        ${collection.collectionName}
                    </a>
                </li>
            `)
        }
    }
}

/**
 * [컬렉션에 추가] 버튼 UI 업데이트
 */
function updateCollectionButtonUI(collectionId, targetElement) {
    const collectionButtonGroups = targetElement.querySelectorAll('.collection-btn-group'); // 반응형 엘리먼트 2개

    for (const buttonGroup of collectionButtonGroups) {
        const addButton = buttonGroup.querySelector('button.collection-btn');
        const collectionButtonList = buttonGroup.querySelector('ul.dropdown-menu');

        // 이전 컬렉션 비활성화
        const activeCollectionItem = collectionButtonList.querySelector('.dropdown-item.active');
        if (activeCollectionItem) {
            activeCollectionItem.classList.remove('active');
        }

        // 컬렉션에 따른 버튼 UI 변경
        if (collectionId) {
            const currentCollectionItem = collectionButtonList.querySelector(`[data-value="${collectionId}"]`);
            currentCollectionItem.classList.add('active');
            addButton.classList.replace('btn-outline-primary', 'btn-primary');

            if (addButton.classList.contains('btn-icon')) {
                addButton.innerHTML = '<i class="bx bx-folder-minus"></i>';
            } else {
                addButton.innerHTML = '컬렉션에 추가됨';
            }
        } else {
            addButton.classList.replace('btn-primary', 'btn-outline-primary');

            if (addButton.classList.contains('btn-icon')) {
                addButton.innerHTML = '<i class="bx bx-folder-plus"></i>';
            } else {
                addButton.innerHTML = '컬렉션에 추가';
            }
        }

        // 컬렉션 선택 목록 변경
        const visibleItemsOnlyAdded = collectionButtonList.querySelectorAll('li.add-visible');
        visibleItemsOnlyAdded.forEach(el => {
            el.style.display = collectionId ? 'block' : 'none';
        })
    }
}

/**
 * [컬렉션에 추가] 버튼 이벤트 세팅
 */
function setCollectionButtonEvent() {
    const collectionButtonList = document.querySelectorAll('.collection-btn-group .dropdown-item');
    collectionButtonList.forEach(el => {
        el.addEventListener('click', function (e) {
            e.stopImmediatePropagation();
            const targetElement = el.closest('.dataset-el');
            const originData = JSON.parse(JSON.stringify(targetElement.dataset));

            if (el.classList.contains('delete-btn')) {
                originData['collectionId'] = null;
                handleReadingBook('U', originData, targetElement);

            } else if (el.classList.contains('new-collection-modal-btn')) {
                // 새 컬렉션 생성 모달 버튼
                const target = document.querySelector(el.dataset.bsTarget + ' .modal-content');
                loadHTML('/books/partials/modal-new-collection', target);

            } else {
                originData['collectionId'] = el.dataset.value;
                originData['readingStatus'] ||= null; // Enum 객체로 받기 위해
                handleReadingBook(originData.id ? 'U' : 'C', originData, targetElement);
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
                url = '/books/partials/carousel-reading-status-card';
            } else {
                callback = () => setReadRecordCardData();
                url = '/books/partials/carousel-read-status-card';
            }
            loadHTML(url, carouselInner, callback);
        } else {
            carousel.style.display = 'none';
        }
    }
}
// (위 Carousel 카드 콘텐츠) 최근 독서 이력
function setReadingRecordCardData() {
    const promise = axios.get(`/api/reading-records/book/${bookIsbn}/me/latest`);

    const onSuccess = (result) => {
        const latestReadingRecord = result.data.latestReadingRecord;
        const cardEl = document.getElementById('readingDDayCard');
        cardEl.querySelector('.start-date').textContent = latestReadingRecord.startDate;
        cardEl.querySelector('.d-day').textContent = latestReadingRecord.dday;
    }

    handleApiResponse(promise, onSuccess);
}
// (위 Carousel 카드 콘텐츠) 완독 독서 이력
function setReadRecordCardData() {
    const promise = axios.get(`/api/reading-records/book/${bookIsbn}/me/completed`);

    const onSuccess = (result) => {
        const completedReadingRecordList = result.data.completedReadingRecordList;
        const latestRecord = completedReadingRecordList[completedReadingRecordList.length - 1]
        const cardEl = document.getElementById('readDateCard');
        cardEl.querySelector('.start-date').textContent = latestRecord.startDate;
        cardEl.querySelector('.end-date').textContent = latestRecord.endDate;
        cardEl.querySelector('.record-cnt').textContent = completedReadingRecordList.length;
    }

    handleApiResponse(promise, onSuccess);
}


/**
 * 책 통계 데이터 업데이트 (독자 수, 리뷰 수, 평균 평점)
 */
function updateBookStatistics(data, targetElement) {
    const subInfoWraps = targetElement.querySelectorAll('.info-rating'); // 반응형 엘리먼트 2개

    for (const subInfoWrap of subInfoWraps) {
        subInfoWrap.querySelector('em.sale-num, .sale-num em').textContent = data.readerCount;
        subInfoWrap.querySelector('em.rating-rv-count, .rating-rv-count em').textContent = data.reviewCount;

        const ratingWrap = subInfoWrap.querySelector('.rating-grade');
        if (data.averageRating) {
            const newRatingEl = rebuildRatingEl(ratingWrap);
            newRatingEl.dataset.value = data.averageRating;
            initializeStarRating(newRatingEl);
        } else {
            ratingWrap.innerHTML = '';
        }
    }
}

/**
 * 독서 상태 변경시 공통 업데이트 사항
 */
function onUpdateReadingInfo(readingInfo, targetElement) {
    // MEMO: ?. => readingInfo 파라미터는 null 일 수도 있음
    // 독서 상태 데이터 저장
    targetElement.dataset.id = readingInfo?.id || "";
    targetElement.dataset.readingStatus = readingInfo?.readingStatus || "";
    targetElement.dataset.collectionId = readingInfo?.collectionId || "";

    // 독서 상태에 따른 UI 변경
    updateReadingListButtonUI(readingInfo?.readingStatus, targetElement);
    updateCollectionButtonUI(readingInfo?.collectionId, targetElement);
    updateReadingCarouselCardUI(readingInfo?.readingStatus);
}