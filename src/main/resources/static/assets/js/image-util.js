'use strict';


function setImagePreviewFeature() {
    const imagePreview = document.querySelector('.img-preview'),
        fileInput = document.querySelector('.img-input'),
        imageResetButton = document.querySelector('.img-reset');

    if (imagePreview && fileInput) {
        const formInput = fileInput.parentElement.querySelector('input[name]');

        fileInput.addEventListener('change', (e) => {
            if (fileInput.files[0]) {
                imagePreview.src = window.URL.createObjectURL(fileInput.files[0]);
            }
        })
        if (imageResetButton) {
            imageResetButton.addEventListener('click', () => {
                imagePreview.src = getProfileImageURL(null);
                fileInput.value = '';
                if (formInput) {
                    formInput.value = '';
                }
            })
        }
    }
}
window.setImagePreviewFeature = setImagePreviewFeature;

function setImageZoomControlFeature() {
    const imageZoomControl = document.querySelector('.image-zoom-control');

    if (imageZoomControl) {
        imageZoomControl.addEventListener('click', () => {
            imageZoomControl.classList.toggle('zoom-in');
            imageZoomControl.classList.toggle('zoom-out');
        })
    }
}
window.setImageZoomControlFeature = setImageZoomControlFeature;
