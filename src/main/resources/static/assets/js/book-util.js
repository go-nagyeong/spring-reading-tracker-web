/**
 * 사용자 독서 목록 / 컬렉션에 책 추가
 */
function saveReadingBook(data, targetElement) {
    globalLoading(true);

    axios.post('/api/user/reading-list', data)
        .then(response => {
            const result = response.data;
            if (result.success) {
                onUpdateReadingInfo(result.data.saveResult , targetElement);
                closeUIComponents();
                showToast(result.message, 'success');
            } else {
                showToast(result.message, 'error');
            }
            globalLoading(false);
        })
        .catch(error => {
            console.error('axios 요청 오류:', error);
            globalLoading(false);
        });
}

/**
 * 사용자 독서 목록에서 책 삭제
 */
function deleteReadingBook(id, targetElement) {
    globalLoading(true);

    axios.delete('/api/user/reading-list/' + id)
        .then(response => {
            const result = response.data;
            if (result.success) {
                onUpdateReadingInfo(null, targetElement)
                closeUIComponents();
                showToast(result.message, 'success');
            } else {
                showToast(result.message, 'error');
            }
            globalLoading(false);
        })
        .catch(error => {
            console.error('axios 요청 오류:', error);
            globalLoading(false);
        });
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
            const originData = targetElement.dataset;

            if (el.classList.contains('delete-btn')) {
                deleteReadingBook(originData.id, targetElement);

            } else {
                saveReadingBook({
                    id: originData.id || null,
                    bookIsbn: targetElement.dataset.isbn,
                    readingStatus: el.dataset.value,
                    collectionId: originData.collectionId || null
                }, targetElement);
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
            const originData = targetElement.dataset;

            if (el.classList.contains('delete-btn')) {
                saveReadingBook({
                    id: originData.id,
                    readingStatus: originData.readingStatus,
                    collectionId: null
                }, targetElement);

            } else if (el.classList.contains('new-collection-modal-btn')) {
                // 새 컬렉션 생성 모달 버튼
                const target = document.querySelector(el.dataset.bsTarget + ' .modal-content');
                loadHTML('/books/partials/modal-new-collection', target);

            } else {
                saveReadingBook({
                    id: originData.id || null,
                    bookIsbn: targetElement.dataset.isbn,
                    readingStatus: originData.readingStatus || null,
                    collectionId: el.dataset.value
                }, targetElement);
            }
        });
    })
}

/**
 * 책 통계 데이터 업데이트 (독자 수, 리뷰 수, 평균 평점)
 */
function updateBookStatistics(data, targetElement) {
    const subInfoWraps = targetElement.querySelectorAll('.info-rating-wrap'); // 반응형 엘리먼트 2개

    for (const subInfoWrap of subInfoWraps) {
        subInfoWrap.querySelector('em.sale-num, .sale-num em').textContent = data.readerCount;
        subInfoWrap.querySelector('em.rating-rv-count, .rating-rv-count em').textContent = data.reviewCount;

        const ratingWrap = targetElement.querySelector('.rating-grade');
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

    // 독서 상태에 따른 버튼 UI 변경 (책 상세 페이지 > 내 독서 관리 > 하단 버튼 / 책 목록 > 우측 버튼)
    updateReadingListButtonUI(readingInfo?.readingStatus, targetElement);
    updateCollectionButtonUI(readingInfo?.collectionId, targetElement);
}