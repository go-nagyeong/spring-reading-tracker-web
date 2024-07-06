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
 * 공통 fetch 함수
 */
async function fetchRequest(url, options = {}) {
    try {
        const response = await fetch(url, options);
        const json = await response.json();
        console.log(json)
        return json;
    } catch (error) {
        console.error("Error fetching data:", error.message);
        throw error; // 에러를 다시 던져서 호출자에게 처리할 수 있도록 함
    }
}
async function _get(url, body) {
    const options = {
        method: 'GET',
        mode: 'cors',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: body
    };
    return fetchRequest(url, options);
}
async function _post(url, body) {
    const options = {
        method: 'POST',
        mode: 'cors',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json; charset=utf-8'
        },
        body: body
    };
    return fetchRequest(url, options);
}

/**
 * HTML 코드 동적 로드 함수
 */
function fetchHTML(url, targetElement, callback = null) {
    fetch(url)
        .then(response => response.text())
        .then(data => {
            targetElement.innerHTML = data;

            // 동적으로 로드된 HTML 내 스크립트 실행
            const scripts = targetElement.querySelectorAll('script');
            handleScripts(scripts);

            if (callback) callback();
        })
        .catch(error => console.error('Error loading HTML:', error));
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