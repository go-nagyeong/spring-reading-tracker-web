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
    return obj;
}

/**
 * 공통 API 응답 처리 함수
 */
function handleApiResponse(promise, onSuccess, onError = () => {}, useLoading = true) {
    promise
        .then(response => {
            const result = response.data;

            onSuccess(result);

            if (result.message) {
                showToast(result.message, 'success');
            }
            if (useLoading) {
                globalLoading(false);
                localLoading(false);
            }
        })
        .catch(error => {
            console.error('axios 요청 오류:', error);
            const result = error.response.data;

            onError(result)

            if (result.message) {
                showToast(result.message, 'error');
            }
            if (useLoading) {
                globalLoading(false);
                localLoading(false);
            }
        })
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
    toastEl.dataset.status = state;
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
    loadHTML('/common/reading-timer', target, callback);

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
function clearPageButtons() {
    const pageButtonWrap = document.querySelector('.pagination');
    while (pageButtonWrap.children[1]) { // 첫번째 엘리먼트는 템플릿 태그이므로 제외
        pageButtonWrap.removeChild(pageButtonWrap.children[1]);
    }
}

/**
 * 페이지네이션이 있는 목록의 행 인덱스 계산 함수
 */
function calculateRowIndex(curPage, rowsPerPage, currentRow) {
    return (curPage - 1) * rowsPerPage + currentRow;
}

/**
 * 사용자 프로필 이미지 세팅 함수
 */
function setProfileImage(profileImage, element) {
    element = element || document.getElementById('profileImage');
    const defaultAvatar = '/assets/img/default-profile.jpg';
    element.src = profileImage || defaultAvatar;
}

/**
 * 모달, 드롭다운, 오프캔버스 close 함수 (서버 작업 success 후 일괄적으로 닫기 위해)
 */
function closeUIComponents() {
    const modalEl = document.querySelector('.modal.show');
    if (modalEl) {
        const modalInstance = bootstrap.Modal.getInstance(modalEl);
        modalInstance.hide();
    }
    const dropdownEl = document.querySelector('.dropdown-menu.show');
    if (dropdownEl) {
        const dropdownInstance = bootstrap.Dropdown.getInstance(dropdownEl.previousElementSibling);
        dropdownInstance.hide();
    }
    const offcanvasEl = document.querySelector('.offcanvas.show');
    if (offcanvasEl) {
        const offcanvasInstance = bootstrap.Offcanvas.getInstance(offcanvasEl);
        offcanvasInstance.hide();
    }
}

/**
 * overflow hidden 영역에서 콘텐츠가 영역을 넘어갔는지 체크하는 함수
 */
function checkOverflow(element) {
    return element.scrollHeight > element.clientHeight
}

/**
 * 생년월일 선택 옵션 목록 세팅
 */
function setBirthdateSelectOptions(targetElement) {
    const yearSelect = document.getElementById('birthYear');
    const monthSelect = document.getElementById('birthMonth');
    const daySelect = document.getElementById('birthDay');
    const newOption = (value) => `<option value=${value}>${value}</option>`;

    const today = new Date();
    const currentYear = today.getFullYear();
    const currentMonth = today.getMonth();
    const currentDay = today.getDate();

    // 연
    for (let year = currentYear; year >= currentYear-100; year--) {
        yearSelect.insertAdjacentHTML('beforeend', newOption(year));
    }
    // 월
    yearSelect.addEventListener('change', (event) => {
        updateMonths();
        updateDays();
    })
    monthSelect.addEventListener('change', (event) => {
        updateDays();
    })
    const updateMonths = () => {
        monthSelect.innerHTML = '<option value="">월</option>';
        const maxMonth = yearSelect.value == currentYear ? currentMonth : 12;
        for (let month = 1; month <= maxMonth; month++) {
            monthSelect.insertAdjacentHTML('beforeend', newOption(month));
        }
    }
    const updateDays = () => {
        daySelect.innerHTML = '<option value="">일</option>';
        const daysInMonth = new Date(yearSelect.value, monthSelect.value, 0).getDate();
        const maxDay = (yearSelect.value == currentYear && monthSelect.value == currentMonth) ? currentDay : daysInMonth;
        for (let day = 1; day <= maxDay; day++) {
            daySelect.insertAdjacentHTML('beforeend', newOption(day));
        }
    }
}

/**
 * 토글 Hide/Show 함수
 */
Element.prototype.toggle = function(isShow) {
    this.style.display = isShow ? 'revert' : 'none';
};

/**
 * 삭제 확인 모달
 */
function confirmDelete(content = null) {
    return new Promise(resolve => {
        const modalEl = document.querySelector('#smallModal');
        const target = document.querySelector('#smallModal .modal-content');
        const callback = () => {
            if (content) {
                target.querySelector('#confirmContent').textContent = content;
            }
            target.querySelector('#confirmBtn').addEventListener('click', resolve);
        }
        loadHTML('/common/modal-delete-confirm', target, callback);

        const modalBootstrap = new bootstrap.Modal(modalEl)
        modalBootstrap.show();
    })
}