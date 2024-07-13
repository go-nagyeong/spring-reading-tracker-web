/**
 * 초 > 시+분+초 형태로 계산
 */
const calculateTime = (time) => {
    let sec = time % 60;
    let min = Math.floor((time % 3600) / 60);
    let hour = Math.floor(time / 3600);

    return {h: hour, m: min, s: sec};
}

/**
 * 동적으로 로드된 HTML 내의 스크립트를 실행하기 위한 함수
 */
function loadScript(src, type, callback = null) {
    const script = document.createElement('script');
    script.src = src;
    script.type = type;
    if (callback) script.onload = callback;
    document.body.appendChild(script);
}
function executeInlineScript(scriptContent) {
    const script = document.createElement('script');
    script.type = 'text/javascript';
    script.text = scriptContent;
    document.body.appendChild(script);
    document.body.removeChild(script); // clean up the DOM
}
function handleScripts(scripts) {
    scripts.forEach(script => {
        if (script.src) {
            loadScript(script.src, script.type);
        } else {
            executeInlineScript(script.textContent);
        }
    });
    // Remove inline scripts after execution
    scripts.forEach(script => script.remove());
}

/**
 * formData JSON 형태로 변환
 */
function formToJson(data) {
    let obj = {};
    for (let [key, value] of data) {
        obj[key] = value;
    }
    return JSON.stringify(obj);
}

/**
 * HTML 코드 동적 로드 함수
 */
function loadHTML(url, targetElement, callback = null) {
    axios.get(url)
        .then(response => {
            targetElement.innerHTML = response.data;

            // 동적으로 로드된 HTML 내 스크립트 실행
            const scripts = targetElement.querySelectorAll('script');
            handleScripts(scripts);

            if (callback) callback();
        })
        .catch(error => {
            console.error('axios 요청 오류:', error);
        });
}

/**
 * Toast 함수
 */
function showToast(content, state = 'info') {
    const toastEl = document.querySelector('#toastCont');
    const toastBody = toastEl.querySelector('.toast-body');
    toastEl.classList.add(state);
    toastBody.innerHTML = content;

    const toastBootstrap = new bootstrap.Toast(toastEl)
    toastBootstrap.show();
}

/**
 * 두 값 사이의 정수인 난수 생성하는 함수
 */
function getRandomInt(min, max) {
    const minCeiled = Math.ceil(min);
    const maxFloored = Math.floor(max);
    // return Math.floor(Math.random() * (maxFloored - minCeiled) + minCeiled); // 최댓값은 제외, 최솟값은 포함
    return Math.floor(Math.random() * (maxFloored - minCeiled + 1) + minCeiled); // 최댓값도 포함, 최솟값도 포함
}

/**
 * 독서 타이머 모달
 */
function showReadingTimerModal() {
    const modalEl = document.querySelector('#smallModal');
    const target = document.querySelector('#smallModal .modal-body');
    const callback = () => {
        document.querySelector('#smallModal .modal-title').innerText = '독서 타이머';
    }
    loadHtml('/common/reading-timer', target, callback);

    const modalBootstrap = new bootstrap.Modal(modalEl)
    modalBootstrap.show();
}

/**
 * 로딩 스피너 함수
 */
function localLoading(bool) {
    const loadingSpinner = document.getElementById('localLoading');
    if (loadingSpinner) {
        loadingSpinner.classList.toggle('show', bool);
    }
}
function globalLoading(bool) {
    const loadingSpinner = document.getElementById('globalLoading');
    if (loadingSpinner) {
        loadingSpinner.classList.toggle('show', bool);
    }
}

/**
 * 검색 결과 안내 메세지 함수
 */
function showNoResultMessage() {
    const resultMsg = document.getElementById('resultMsg');
    if (resultMsg) {
        resultMsg.style.display = 'block';
    }
}
function hideNoResultsMessage() {
    const resultMsg = document.getElementById('resultMsg');
    if (resultMsg) {
        resultMsg.style.display = 'none';
    }
}

/**
 * 페이지네이션 함수
 */
function setPagination(curPage, lastPage) {
    const paginationWrap = document.getElementById('paginationWrap');
    if (paginationWrap) {
        const pageButtonWrap = paginationWrap.querySelector('.pagination');
        const searchParams = new URLSearchParams(window.location.search);

        const buttonIcon = (direction) => `<i class="tf-icon bx bx-chevron${direction}"></i>`;
        const buttonLink = (page) => {
            searchParams.set('page', page);
            return curPage === page ? '' : '?'+searchParams;
        };

        // 페이지 버튼 표시 범위
        let startPage = Math.max(curPage - 2, 1); // 현재 페이지를 중심으로 앞으로 2페이지 이내로 시작
        let endPage = Math.min(startPage + 4, lastPage); // 시작 페이지부터 4페이지까지 (최대 5개 버튼)

        // '첫 페이지로 가기', '이전' 버튼
        appendPageButton(pageButtonWrap, buttonLink(1), buttonIcon('s-left'), 'prev');
        appendPageButton(pageButtonWrap, buttonLink(Math.max(startPage-1, 1)), buttonIcon('-left'), 'prev');

        // 페이지 버튼
        for (let page = startPage; page <= endPage; page++) {
            appendPageButton(pageButtonWrap, buttonLink(page), page, (page === curPage ? 'active' : null));
        }

        // '다음', '마지막 페이지로 가기' 버튼
        appendPageButton(pageButtonWrap, buttonLink(Math.min(endPage+1, lastPage)), buttonIcon('-right'), 'prev');
        appendPageButton(pageButtonWrap, buttonLink(lastPage), buttonIcon('s-right'), 'next');
    }
}
function appendPageButton(pageButtonWrap, link, content, additionalClass = null) {
    const templateTag = pageButtonWrap.querySelector('.template-tag');

    const newButton = templateTag.cloneNode(true);
    // 버튼 클래스
    newButton.classList.remove('template-tag');
    if (additionalClass) {
        newButton.classList.add(additionalClass);
    }
    // 버튼 링크 경로
    newButton.querySelector('a').href = link;
    // 버튼 내용
    newButton.querySelector('a').innerHTML = content;

    pageButtonWrap.appendChild(newButton);
}

/**
 * 페이지네이션이 있는 목록의 행 인덱스 계산 함수
 */
function calculateRowIndex(curPage, rowsPerPage, currentRow) {
    return (curPage - 1) * rowsPerPage + currentRow;
}

/**
 * [독서 목록에 추가] 버튼 UI 업데이트
 */
function updateReadingListButtonUI(status, element) {
    const readingButtonGroups = element.querySelectorAll('.reading-btn-group'); // 반응형 엘리먼트 2개

    console.log('status:', status);
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
            console.log('삭제');
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
 * 사용자 컬렉션 목록 세팅
 */
function setCollectionList(collectionList, element) {
    const collectionButtonGroups = element.querySelectorAll('.collection-btn-group'); // 반응형 엘리먼트 2개

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
function updateCollectionButtonUI(collectionId, element) {
    const collectionButtonGroups = element.querySelectorAll('.collection-btn-group'); // 반응형 엘리먼트 2개

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