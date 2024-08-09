'use strict';

// 책 두께 1mm당 15p 기준으로 UI depth 계산
const bookElement = (book) => `
<a href="/books/detail/${book.isbn13}">
    <div class="book" data-depth="${Math.round(book.subInfo.itemPage / 15)}">
        <div class="spine">
            <span class="spine-title">${book.title}</span>
        </div>
        <div class="side top"></div>
        <div class="side cover"><img src="${book.cover}"></div>
    </div>
</a>
`;


function initializeBookshelfUI() {
    /**
     * 책 크기 스타일 세팅
     */
    document.querySelectorAll('.bookshelf .book').forEach(el => {
        if (el.getAttribute('data-depth')) {
            // const width = el.getAttribute('data-width') + 'px';
            // const height = el.getAttribute('data-height') + 'px';
            const depth = el.getAttribute('data-depth') + 'px';

            // el.style.setProperty('--book-width', width);
            // el.style.setProperty('--book-height', height);
            el.style.setProperty('--book-depth', depth);
        }
    });

    document.querySelectorAll('.bookshelf:not(.with-post-it)').forEach(el => hideOverflowingElement(el));
    document.querySelectorAll('.bookshelf.with-post-it').forEach(el => moveOverflowingElementToNextLine(el));
}

window.initializeBookshelfUI = initializeBookshelfUI;

/**
 * Flex Box(bookshelf) 넘어가지 않게 엘리먼트 조정
 */
// 엘리먼트 숨기고 '더보기' 버튼으로 처리
function hideOverflowingElement(bookshelfEl) {
    const parentSize = getWidth(bookshelfEl.querySelector('.books'));
    let childrenSize = 0;
    bookshelfEl.querySelectorAll('.book').forEach(el => {
        childrenSize += getOuterWidth(el);
        el.toggle(parentSize >= childrenSize);
    });
}

// 다음 줄로 엘리먼트 넘기기
function moveOverflowingElementToNextLine(bookshelfEl) {
    const parent = bookshelfEl.querySelector('.books');
    const children = bookshelfEl.querySelectorAll('.book');

    const parentSize = getWidth(parent);
    let childrenSize = 0;
    children.forEach(el => {
        childrenSize += getOuterWidth(el);

        if (parentSize < childrenSize) {
            const newBookshelfEl = cloneBookShelfElement(bookshelfEl);
            bookshelfEl.after(newBookshelfEl); // 새로운 줄 삽입
            bookshelfEl = newBookshelfEl;
            childrenSize = getOuterWidth(el);
        }

        bookshelfEl.querySelector('.books').append(el);
    });
}

function cloneBookShelfElement(bookshelfEl) {
    /*
        <bookshelf>
            <books>
                <book></book>
                ...
            </books>
            <shelf></shelf>
        </bookshelf>
    */
    const bookshelf = document.createElement('div');
    bookshelf.className = bookshelfEl.className;

    const books = document.createElement('div');
    books.classList.add('books');

    const shelf = document.createElement('div');
    shelf.classList.add('shelf');

    bookshelf.appendChild(books);
    bookshelf.appendChild(shelf);

    return bookshelf;
}

window.hideOverflowingElement = hideOverflowingElement;
window.moveOverflowingElementToNextLine = moveOverflowingElementToNextLine;

function getWidth(el) {
    const style = window.getComputedStyle(el),
        width = el.clientWidth,
        padding = parseFloat(style.paddingLeft) + parseFloat(style.paddingRight);

    return width - padding;
}

function getOuterWidth(el) {
    const style = window.getComputedStyle(el, null),
        width = el.offsetWidth,
        margin = parseFloat(style.marginLeft) + parseFloat(style.marginRight);

    return width + margin;
}