/**
 * User Config Dictionary (Key, Value 명칭 통일)
 */
const userConfigKeyDict = {
    COLLECTION_BOOK_VIEW: 'collection_book_view_option',
    MY_PAGE_BOOK_VIEW: 'my_page_book_view_option',
    CALENDAR_BOOK_VIEW: 'calendar_book_view_option',
    CALENDAR_DISPLAY: 'calendar_display_option',
};

const bookViewOptions = {
    DEFAULT: 'spine',
    OPTIONS: {
        SPINE: 'spine',
        COVER: 'cover',
    }
};
const calendarDisplayOptions = {
    DEFAULT: 'all',
    OPTIONS: {
        ALL: 'all',
        READING_RECORD: 'reading_record',
        READING_TIME_LOG: 'reading_time_log',
        NOTE: 'note',
        PURCHASE_RENTAL: 'purchase_or_rental',
    }
};

const userConfigValueDict = {
    COLLECTION_BOOK_VIEW: bookViewOptions,
    MY_PAGE_BOOK_VIEW: bookViewOptions,
    CALENDAR_BOOK_VIEW: bookViewOptions,
    CALENDAR_DISPLAY: calendarDisplayOptions,
};

/**
 * 초기 로드 시 로그인 유저 정보 및 사용자 설정 값 불러와서 캐싱
 */
let cachedLoggedInUserInfo = null;
let cachedLoggedInUserConfig = null;

async function loadLoggedInUserInfo() {
    try {
        if (!cachedLoggedInUserInfo) {
            const response = await axios.get('/api/auth/logged-in-user');
            cachedLoggedInUserInfo = response.data.data;
        }
    } catch (error) {
        console.error('axios 요청 오류:', error);
        showToast('알 수 없는 오류가 발생했습니다.', 'error');
    }
    return cachedLoggedInUserInfo;
}
async function loadLoggedInUserConfig() {
    try {
        if (!cachedLoggedInUserConfig) {
            const response = await axios.get('/api/user-configs/me');
            cachedLoggedInUserConfig = response.data.data;
        }
    } catch (error) {
        console.error('axios 요청 오류:', error);
        showToast('알 수 없는 오류가 발생했습니다.', 'error');
    }
    return cachedLoggedInUserConfig;
}

function invalidateLoggedInUserInfoCache() {
    cachedLoggedInUserInfo = null;
}
function invalidateLoggedInUserConfigCache() {
    cachedLoggedInUserConfig = null;
}

/**
 * 로그인 유저 정보 세팅 (공통 레이아웃)
 */
function setGlobalUserData(userInfo) {
    document.querySelectorAll('.user-profile').forEach(el => {
        setProfileImage(userInfo.profileImage, el);
    })
    document.querySelectorAll('.user-name').forEach(el => {
        el.textContent = userInfo.username;
    })
    document.querySelectorAll('.user-email').forEach(el => {
        el.textContent = userInfo.email;
    })
}


/**
 * 로그인 유저 사용자 설정 값 조회
 */
async function getUserConfig(keyEnum) {
    let config = await loadLoggedInUserConfig();
    let value = config[userConfigKeyDict[keyEnum]];
    if (!value) {
        value = userConfigValueDict[keyEnum].DEFAULT;
    }
    return value;
}

/**
 * 로그인 유저 사용자 설정 값 저장
 */
function saveUserConfig(data) {
    const promise = axios.post('/api/user-configs', data); // batch create/update

    const onSuccess = (result) => {
        invalidateLoggedInUserConfigCache();
        loadLoggedInUserConfig();
    };

    handleApiResponse(promise, onSuccess);
}