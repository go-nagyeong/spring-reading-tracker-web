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
