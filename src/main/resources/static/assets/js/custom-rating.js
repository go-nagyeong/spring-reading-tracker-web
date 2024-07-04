'use strict';

document.addEventListener('DOMContentLoaded', function (e) {
    window.initializeStarRating = (el) => {
        const rating = el.dataset.value;
        let ratingRange = el.dataset.range || 5;

        for (let i = 0; i <= ratingRange; i++) {
            const option = document.createElement('option');
            if (i > 0) {
                option.value = i;
                option.innerText = i;
            }
            if (rating && i === Math.floor(rating)) {
                option.selected = true;
            }
            el.append(option);
        }

        const stars = new StarRating(el, {tooltip: ' '});
        const widgetEl = stars.widgets[0].widgetEl;

        if (widgetEl) {
            el.classList.forEach(cls => { // 별 크기 및 평점 라벨 관련 클래스
                widgetEl.classList.add(cls);
            })
            if (rating && !el.classList.contains('non-label')) {
                widgetEl.setAttribute("aria-label", rating);
            } else {
                widgetEl.removeAttribute("aria-label");
            }
        }
    }

    // 초기 요소들에 대해 star-rating 적용
    document.querySelectorAll('.star-rating').forEach(el => initializeStarRating(el));
});