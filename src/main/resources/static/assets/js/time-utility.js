class Stopwatch {
    static instance = null;

    // TODO: 추후에 로컬 스토리지 > DB 저장으로 변경 (JS 스톱워치랑 DB에 저장된 시간 비교 테스트 해보기)
    constructor(selector, storageItemName = null) {
        if (Stopwatch.instance) {
            return Stopwatch.instance;
        }

        this.selector = selector;
        this.time = 0;
        this.isPlaying = 0;
        this.requestID = null;
        this.storageItemName = storageItemName;
        this.startTimeMillis = null;

        Stopwatch.instance = this;
    }

    init() {
        if (this.storageItemName && localStorage.getItem(`${this.storageItemName}_start`)) {
            this.startTimeMillis = parseFloat(localStorage.getItem(`${this.storageItemName}_start`));
            let endTimeMillis = parseFloat(localStorage.getItem(`${this.storageItemName}_stop`));
            if (!localStorage.getItem(`${this.storageItemName}_stop`)) {
                endTimeMillis = Date.now();
            }
            this.time = Math.floor((endTimeMillis - this.startTimeMillis) / 1000);
            this.isPlaying = parseInt(localStorage.getItem(`${this.storageItemName}_is_playing`))
        }

        this.setTime();
        this.setPlayStatus();

        if (this.isPlaying) {
            this.start();
        }
    }

    start() {
        this.isPlaying = 1;
        this.setPlayStatus();

        if (this.storageItemName) {
            if (!this.startTimeMillis) {
                this.startTimeMillis = Date.now();
                localStorage.setItem(`${this.storageItemName}_start`, this.startTimeMillis);
            } else {
                if (localStorage.getItem(`${this.storageItemName}_stop`)) {
                    const stoppedAt = parseFloat(localStorage.getItem(`${this.storageItemName}_stop`));
                    this.startTimeMillis += (Date.now() - stoppedAt);
                    localStorage.setItem(`${this.storageItemName}_start`, this.startTimeMillis);
                }
            }
            localStorage.removeItem(`${this.storageItemName}_stop`);

            localStorage.setItem(`${this.storageItemName}_is_playing`, this.isPlaying);
        }

        const update = () => {
            this.time = Math.floor((Date.now() - this.startTimeMillis) / 1000);
            this.setTime();

            if (this.isPlaying) {
                this.requestID = requestAnimationFrame(update);
            }
        }

        update();
    }

    stop() {
        if (this.requestID !== null) {
            cancelAnimationFrame(this.requestID);
            this.requestID = null;
        }

        this.isPlaying = 0;
        this.setPlayStatus();

        if (this.storageItemName) {
            localStorage.setItem(`${this.storageItemName}_stop`, Date.now());
            localStorage.setItem(`${this.storageItemName}_is_playing`, this.isPlaying);
        }
    }

    reset() {
        this.stop();

        this.time = 0;
        this.setTime();

        if (this.storageItemName) {
            this.startTimeMillis = null;
            localStorage.removeItem(`${this.storageItemName}_start`);
            localStorage.removeItem(`${this.storageItemName}_stop`);
            localStorage.removeItem(`${this.storageItemName}_is_playing`);
        }
    }

    formatTime(time) {
        return time < 10 ? `0${time}` : time;
    }

    setTime() {
        document.querySelectorAll(this.selector).forEach(el => {
            const stopwatchHour = el.querySelector('.sw-hour');
            const stopwatchMin = el.querySelector('.sw-min');
            const stopwatchSec = el.querySelector('.sw-sec');

            const result = calculateTime(this.time);
            if (stopwatchHour) stopwatchHour.innerText = this.formatTime(result.h);
            if (stopwatchMin) stopwatchMin.innerText = this.formatTime(result.m);
            if (stopwatchSec) stopwatchSec.innerText = this.formatTime(result.s);
        })
    }

    setPlayStatus() {
        document.querySelectorAll(this.selector).forEach(el => {
            el.classList.toggle('play', this.isPlaying);
            el.classList.toggle('pause', !this.isPlaying);
        });
    }
}


class Timer {
    constructor(element) {
        this.el = element;
        this.time = 0;
        this.interval = null;
    }

    init(time) {
        this.time = time;
        this.setTime();
    }

    start(callback = null) {
        if (this.interval) {
            clearInterval(this.interval);
        }
        this.interval = setInterval(() => {
            this.time--;
            this.setTime();

            if (this.time === 0) {
                clearInterval(this.interval);
                if (callback) {
                    callback();
                }
            }
        }, 1000);
    }

    formatTime(time) {
        return time < 10 ? `0${time}` : time;
    }

    setTime() {
        const remainingHour = this.el.querySelector('.tm-hour');
        const remainingMin = this.el.querySelector('.tm-min');
        const remainingSec = this.el.querySelector('.tm-sec');

        const result = calculateTime(this.time);
        if (remainingHour) remainingHour.innerText = result.h;
        if (remainingMin) remainingMin.innerText = result.m;
        if (remainingSec) remainingSec.innerText = this.formatTime(result.s);
    }
}


