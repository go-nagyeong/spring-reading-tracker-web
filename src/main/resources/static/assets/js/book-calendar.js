'use strict';

(function () {
    // --------------------------------------------------------------------
    const bookCalendarEl = document.querySelector('#bookCalendar'),
        bookCalendarOptions = {
            // initialView: 'dayGridMonth',
            initialView: 'customMonthView',
            views: {
                customMonthView: {
                    type: 'multiMonth',
                    duration: { months: 1 }
                }
            },
            locale: 'ko',
            aspectRatio: 0.57,
            // editable: true,
            selectable: true,
            showNonCurrentDates: false,
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'customMonthView,multiMonthYear'
            },
            buttonText: {
                today: '오늘',
                customMonthView: '월별',
                multiMonthYear: '연도별',
            },
            events: [
                {
                    title: '배움의 발견 (나의 특별한 가족, 교육, 그리고 자유의 이야기)',
                    start: '2024-06-03',
                    extendedProps: {
                        coverImage: '/assets/img/book01.jpg'
                    }
                },
                {
                    title: '종의 기원',
                    start: '2024-06-03',
                    extendedProps: {
                        coverImage: '/assets/img/book02.jpg'
                    }
                },
                {
                    title: '일류의 조건',
                    start: '2024-06-03',
                    extendedProps: {
                        coverImage: '/assets/img/book03.jpg'
                    }
                },
                {
                    title: '무엇이 나를 행복하게 만드는가',
                    start: '2024-06-04',
                    extendedProps: {
                        coverImage: '/assets/img/book04.jpg'
                    }
                },
                {
                    title: '나를 소모하지 않는 현명한 태도에 관하여',
                    start: '2024-06-05',
                    extendedProps: {
                        coverImage: '/assets/img/book05.jpg'
                    }
                },
                {
                    title: '듄 1',
                    start: '2024-06-07',
                    extendedProps: {
                        coverImage: '/assets/img/book06.jpg'
                    }
                }
            ],
            eventOrder: '',
            dayCellContent: function (arg) {
                return arg.dayNumberText.replace('월 ', '/').replace('일', '');
            },
            eventContent: function (arg) {
                let eventEl = document.createElement('div');

                // 북커버 UI
                if (arg.event.extendedProps.coverImage) {
                    let imgEl = document.createElement('img');
                    imgEl.src = arg.event.extendedProps.coverImage;
                    imgEl.alt = arg.event.title;
                    imgEl.classList.add('book-cover');
                    eventEl.appendChild(imgEl);
                // 책 타이틀 UI
                } else {
                    eventEl.innerText = arg.event.title;
                }

                return {domNodes: [eventEl]};
            },
            eventDidMount: function (info) {
                // 클릭시 상세 페이지 이동
                info.el.href = '/book/book-detail';

                if (info.event.extendedProps.coverImage) {
                    // 북커버 UI 설정 구분 클래스 추가
                    info.el.classList.add('ui-book-cover');

                    let eventDate = info.event.startStr;
                    let eventWrapElement = document.querySelector(`[data-date="${eventDate}"] .fc-daygrid-day-events`);
                    let eventElements = eventWrapElement.querySelectorAll('.fc-daygrid-event');
                    let bottomElement = eventWrapElement.querySelector('.fc-daygrid-day-bottom');

                    // 이벤트가 2개 이상일 경우 북커버 UI를 위한 클래스 추가 (absolute display)
                    if (eventElements.length > 1) {
                        eventWrapElement.classList.add('multiple-event-overlay');
                    }

                    // 이벤트가 4개 이상일 경우 추가 개수 badge 추가
                    if (eventElements.length > 3 && !bottomElement.hasChildNodes()) {
                        let badgeEl = document.createElement('span');
                        badgeEl.innerText = '+' + (eventElements.length - 3);
                        badgeEl.className = 'badge badge-center bg-label-primary'
                        bottomElement.append(badgeEl);

                        for (let i=3; i<eventElements.length; i++) {
                            eventElements[i].classList.add('d-none');
                        }
                    }
                }
            },
            windowResize: function(arg) {
                setTimeout(() => {
                    setContentHeight(arg.view.calendar);
                }, 100) // windowResizeDelay: 100(default)
            }
        };
    if (typeof bookCalendarEl !== undefined && bookCalendarEl !== null) {
        const bookCalendar = new FullCalendar.Calendar(bookCalendarEl, bookCalendarOptions);
        bookCalendar.render();
        setContentHeight(bookCalendar);
    }
})();

// 스크롤 안 생기게 높이 조정
function setContentHeight(calendar) {
    setTimeout(() => {
        let header = document.querySelector('.fc-multimonth-header');
        let body = document.querySelector('.fc-multimonth-daygrid');
        calendar.setOption('contentHeight', header.offsetHeight + body.offsetHeight + 2);
    }, 100)
}
