/**
 * Main
 */

'use strict';

let menu, animate;

(function () {
    // Initialize menu
    //-----------------

    let layoutMenuEl = document.querySelectorAll('#layout-menu');
    layoutMenuEl.forEach(function (element) {
        menu = new Menu(element, {
            orientation: 'vertical',
            closeChildren: false
        });
        // Change parameter to true if you want scroll animation
        window.Helpers.scrollToActive((animate = false));
        window.Helpers.mainMenu = menu;
    });

    // Initialize menu togglers and bind click on each
    let menuToggler = document.querySelectorAll('.layout-menu-toggle');
    menuToggler.forEach(item => {
        item.addEventListener('click', event => {
            event.preventDefault();
            window.Helpers.toggleCollapsed();
        });
    });

    // Display menu toggle (layout-menu-toggle) on hover with delay
    let delay = function (elem, callback) {
        let timeout = null;
        elem.onmouseenter = function () {
            // Set timeout to be a timer which will invoke callback after 300ms (not for small screen)
            if (!Helpers.isSmallScreen()) {
                timeout = setTimeout(callback, 300);
            } else {
                timeout = setTimeout(callback, 0);
            }
        };

        elem.onmouseleave = function () {
            // Clear any timers set to timeout
            document.querySelector('.layout-menu-toggle').classList.remove('d-block');
            clearTimeout(timeout);
        };
    };
    if (document.getElementById('layout-menu')) {
        delay(document.getElementById('layout-menu'), function () {
            // not for small screen
            if (!Helpers.isSmallScreen()) {
                document.querySelector('.layout-menu-toggle').classList.add('d-block');
            }
        });
    }

    // Display in main menu when menu scrolls
    let menuInnerContainer = document.getElementsByClassName('menu-inner'),
        menuInnerShadow = document.getElementsByClassName('menu-inner-shadow')[0];
    if (menuInnerContainer.length > 0 && menuInnerShadow) {
        menuInnerContainer[0].addEventListener('ps-scroll-y', function () {
            if (this.querySelector('.ps__thumb-y').offsetTop) {
                menuInnerShadow.style.display = 'block';
            } else {
                menuInnerShadow.style.display = 'none';
            }
        });
    }

    // Initialize LC Select
    //-----------------
    window.Helpers.initializeLCSelect = () => {
        new lc_select('select.lc-select', {
            wrap_width: '100%',
            min_for_search: 5,
        });
    }
    window.Helpers.initializeLCSelect();

    // Init helpers & misc
    // --------------------

    // Init BS Tooltip
    window.Helpers.initializeTooltips = () => {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl, {html: true, sanitize: false, trigger: 'hover'});
        });
    }

    // Init BS Popover
    let openPopover = null;
    window.Helpers.initializePopovers = () => {
        const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
        popoverTriggerList.map(function (popoverTriggerEl) {
            const popover = new bootstrap.Popover(popoverTriggerEl, {html: true, sanitize: false, trigger: 'click'});
            popoverTriggerEl.addEventListener('click', function (event) {
                openPopover = popover;
            });
        });
    }
    // Close popover when clicking outside, but not when clicking inside
    document.addEventListener('mousedown', function (event) {
        const isClickInside = event.target.closest('.popover');
        const isClickCloseBtn = event.target.closest('[data-bs-dismiss=popover]');
        if (openPopover && (!isClickInside || isClickCloseBtn)) {
            openPopover.hide();
            openPopover = null;
        }
    });

    // Init BS Show Collapse (Not Toggle)
    window.Helpers.initializeCollapses = () => {
        const collapseTriggerList = [].slice.call(document.querySelectorAll('[data-bs-show="collapse"]'));
        collapseTriggerList.map(function (collapseTriggerEl) {
            const target = collapseTriggerEl.getAttribute('data-bs-target') || collapseTriggerEl.getAttribute('href');
            const collapseElement = document.querySelector(target);
            const collapse = new bootstrap.Collapse(collapseElement, {toggle: false});
            collapseTriggerEl.addEventListener('click', function (event) {
                collapse.show();
            });
        });
    }
    // Close collapse when clicking [data-bs-dismiss] button
    document.addEventListener('mousedown', function (event) {
        const openCollapseEl = document.querySelector('.collapse.show');
        if (openCollapseEl) {
            const collapse = bootstrap.Collapse.getInstance(openCollapseEl);
            const isClickCloseBtn = event.target.closest('[data-bs-dismiss=collapse]');
            if (collapse && isClickCloseBtn) {
                collapse.hide();
            }
        }
    });

    window.Helpers.initializeTooltips();
    window.Helpers.initializePopovers();
    window.Helpers.initializeCollapses();

    // Accordion active class
    const accordionActiveFunction = function (e) {
        if (e.type == 'show.bs.collapse' || e.type == 'show.bs.collapse') {
            e.target.closest('.accordion-item').classList.add('active');
        } else {
            e.target.closest('.accordion-item').classList.remove('active');
        }
    };

    const accordionTriggerList = [].slice.call(document.querySelectorAll('.accordion'));
    const accordionList = accordionTriggerList.map(function (accordionTriggerEl) {
        accordionTriggerEl.addEventListener('show.bs.collapse', accordionActiveFunction);
        accordionTriggerEl.addEventListener('hide.bs.collapse', accordionActiveFunction);
    });

    // Auto update layout based on screen size
    window.Helpers.setAutoUpdate(true);

    // Toggle Password Visibility
    window.Helpers.initPasswordToggle();

    // Speech To Text
    window.Helpers.initSpeechToText();

    // Manage menu expanded/collapsed with templateCustomizer & local storage
    //------------------------------------------------------------------

    // If current layout is horizontal OR current window screen is small (overlay menu) than return from here
    if (window.Helpers.isSmallScreen()) {
        return;
    }

    // If current layout is vertical and current window screen is > small

    // Auto update menu collapsed/expanded based on the themeConfig
    window.Helpers.setCollapsed(true, false);


    // axios default config
    axios.defaults.headers.post['Content-Type'] = 'application/json';
    axios.defaults.responseType = 'json';
    axios.defaults.withCredentials = true; // 쿠키를 서버로 전송 (Jwt 토큰을 헤더에 담지 않고 쿠키로 전달 <- MPA 형식의 프론트 때문)


    // Custom helpers & misc
    // --------------------

    // 로그인 유저 정보 세팅
    document.addEventListener('DOMContentLoaded', function (event) {
        getLoginUserDetail()
            .then(userDetail => {
                setGlobalLoginUserData(userDetail);
            });
    });

    // 숫자 input 값 전처리 1
    document.addEventListener('keydown', function (event) {
        const input = event.target;
        if (input.type === 'number') {
            // +,- 부호 입력 제한
            if (!input.classList.contains('whole-num')) {
                if (event.key === '+' || event.key === '-') {
                    event.preventDefault();
                }
            }
            // 소수점 입력 제한
            if (!input.classList.contains('float')) {
                if (event.key === '.') {
                    event.preventDefault();
                }
            }
        }
    });
    document.addEventListener('input', function (event) {
        const input = event.target;

        // 숫자 input 값 전처리 2
        if (input.type === 'number') {
            // 셀 수 있는 숫자만 입력 가능 (0으로 시작 X)
            if (input.value.length > 1 && input.value.startsWith('0')) {
                input.value = input.value.replace(/^0+/, '');
            }
            // 음수 입력 제한
            if (!input.classList.contains('whole-num')) {
                if (input.value < 0) {
                    input.value = 0;
                }
            }
            // min, max 제한
            if (input.value < parseInt(input.min)) {
                input.value = input.min;
            }
            if (input.value > parseInt(input.max)) {
                input.value = input.max;
            }
        }

        // 휴대폰번호 input 값에 자동 하이픈
        if (input.classList.contains('phone-num')) {
            input.value = input.value
                .replace(/[^0-9]/g, '')
                .replace(/^(\d{2,3})(\d{3,4})(\d{4})$/, `$1-$2-$3`);
        }
    });

    // 양방향 switch label 클릭 이벤트
    const labelElements = document.querySelectorAll('label');
    labelElements.forEach(el => {
        const parentEl = el.parentElement;
        const switchEl = parentEl.querySelector('input');
        const isSwitchLabel = switchEl.getAttribute('role') === 'switch';
        const hasTwoLabels = parentEl.querySelectorAll('label').length === 2;
        if (isSwitchLabel && hasTwoLabels) {
            el.addEventListener('click', function() {
                switchEl.checked = el.dataset.switch === 'on'; // data-switch = on/off
                switchEl.dispatchEvent(new Event('change')); // change trigger
            })
        }
    })

    // 버튼 클릭 후 자동 focus out(blur) 되게
    document.addEventListener('click', function (event) {
        if (event.target.closest('button')) {
            event.target.closest('button').blur();
        }
    })

    // '서비스 준비중' alert 처리
    document.addEventListener('DOMContentLoaded', function (event) {
        document.querySelectorAll('.under-construction').forEach(el => {
            el.addEventListener('click', function (event) {
                event.preventDefault();
                const content = '서비스 개발 중입니다.<br/>더 나은 경험을 위해 최선을 다하고 있습니다.';
                showToast(content);
            });
        })
    })

    // 독서 타이머 인스턴스 초기화
    document.addEventListener('DOMContentLoaded', function (event) {
        if (!window.stopwatch) {
            window.stopwatch = new Stopwatch('.reading-timer', 'reading_timer');
        }
        window.stopwatch.init();

        window.applyTimerShortcutOnline();
    });

    // 독서 타이머 단축 버튼 > '온라인' 스타일 적용 (사용 중인 경우)
    window.applyTimerShortcutOnline = () => {
        const shortcutButton = document.querySelector('.short-cut .btn');
        if (window.stopwatch && shortcutButton) {
            shortcutButton.classList.toggle('avatar-online', window.stopwatch.isPlaying);
        }
    }
})();
