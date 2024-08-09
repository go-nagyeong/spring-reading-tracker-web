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
 * 로그인 유저 정보 가져오기
 */
async function getLoginUserDetail() {
    const promise = axios.get(`/api/users/me`);

    let loginUserDetail = null;

    // handleApiResponse를 호출하면서 성공 콜백을 정의
    await new Promise((resolve, reject) => {
        const onSuccess = (result) => {
            loginUserDetail = result.data.user;
            resolve();
        }

        handleApiResponse(promise, onSuccess);
    });

    return loginUserDetail;
}

/**
 * 로그인 유저 정보 세팅 (공통 레이아웃)
 */
function setGlobalLoginUserData(loginUserDetail) {
    document.querySelectorAll('.user-profile').forEach(el => {
        setProfileImage(loginUserDetail.profileImage, el);
    })
    document.querySelectorAll('.user-name').forEach(el => {
        el.textContent = loginUserDetail.username;
    })
    document.querySelectorAll('.user-email').forEach(el => {
        el.textContent = loginUserDetail.email;
    })
}

/**
 * 로그인 유저 Config 데이터 로드 (DB 데이터를 로컬 스토리지에 저장)
 */
function loadLoginUserConfig() {
    const promise = axios.get(`/api/user-configs/me`);

    const onSuccess = (result) => {
        const data = result.data;
        Object.entries(data).forEach(([key, value]) => {
            localStorage.setItem(key, value);
        });
    }

    handleApiResponse(promise, onSuccess);
}

/**
 * 로그인 User Config 값 조회 (로컬 스토리지에서 조회)
 */
function getUserConfig(keyEnum) {
    let value = localStorage.getItem(userConfigKeyDict[keyEnum]);
    if (!value) {
        value = userConfigValueDict[keyEnum].DEFAULT;
    }
    return value;
}

/**
 * 로그인 유저 Config 저장
 */
function saveUserConfig(data) {
    const promise = axios.post('/api/user-configs', data); // batch create/update

    const onSuccess = (result) => {
        const data = result.data;
        Object.entries(data).forEach(([key, value]) => {
            localStorage.setItem(key, value);
        });
    };

    handleApiResponse(promise, onSuccess);
}