'use strict';

document.addEventListener('DOMContentLoaded', function (e) {
    // Update/reset user image of account page
    let profileUserImage = document.getElementById('userProfile');
    const fileInput = document.querySelector('.profile-img-input'),
        fileRandomBtn = document.querySelector('.profile-img-random'),
        resetFileInput = document.querySelector('.profile-img-reset'),
        realFormInput = document.querySelector('[name=profileImage]');

    if (profileUserImage) {
        const resetImage = profileUserImage.src;
        fileInput.onchange = () => {
            if (fileInput.files[0]) {
                profileUserImage.src = window.URL.createObjectURL(fileInput.files[0]);
            }
        };
        fileRandomBtn.onclick = () => {
            fileInput.value = '';
            // TODO: max => 서버 측에서 파일 개수를 계산해서 전달
            const randomImageDir = `/assets/img/system-profile/random-profile${getRandomInt(1, 13)}.png`;
            profileUserImage.src = randomImageDir;
            realFormInput.value = randomImageDir;
        };
        resetFileInput.onclick = () => {
            fileInput.value = '';
            profileUserImage.src = resetImage;
        };
    }
});
