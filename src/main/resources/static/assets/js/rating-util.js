function rebuildRatingEl(element) {
    const ratingWrap = element.parentElement;
    const cloneStarRatingEl = element.cloneNode();
    ratingWrap.innerHTML = '';
    ratingWrap.append(cloneStarRatingEl);

    return ratingWrap.querySelector('select.star-rating');
}

function initializeStarRating(element) {
    const rating = element.dataset.value;
    let ratingRange = element.dataset.range || 5;

    for (let i = 0; i <= ratingRange; i++) {
        const option = document.createElement('option');
        if (i > 0) {
            option.value = i;
            option.innerText = i;
        }
        if (rating && i === Math.floor(rating)) {
            option.selected = true;
        }
        element.append(option);
    }

    const starRating = new StarRating(element, {tooltip: ' '});
    const widgetEl = starRating.widgets[0].widgetEl;

    if (widgetEl) {
        element.classList.forEach(cls => { // 별 크기 및 평점 라벨 관련 클래스
            widgetEl.classList.add(cls);
        })
        if (rating && !element.classList.contains('non-label')) {
            widgetEl.setAttribute("aria-label", rating);
        } else {
            widgetEl.removeAttribute("aria-label");
        }
    }

    return starRating;
}
