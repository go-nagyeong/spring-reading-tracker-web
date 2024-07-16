function rebuildRatingEl(element) {
    const cloneStarRatingEl = element.querySelector('select.star-rating').cloneNode();
    element.innerHTML = '';
    element.append(cloneStarRatingEl);

    return element.querySelector('select.star-rating');
}

function initializeStarRating(el) {
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

    const starRating = new StarRating(el, {tooltip: ' '});
    const widgetEl = starRating.widgets[0].widgetEl;

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

    return starRating;
}