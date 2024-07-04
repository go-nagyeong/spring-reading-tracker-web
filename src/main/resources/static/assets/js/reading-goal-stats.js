/**
 * Dashboard Analytics
 */

'use strict';

(function () {
    const today = new Date();
    const todayWeek = today.getDay(); // TODO: 임시

    // 일일 목표 달성 (주간)
    // --------------------------------------------------------------------
    const weeks = ['일', '월', '화', '수', '목', '금', '토', '일'];
    const weeklyStatElements = document.querySelectorAll('.week-stats-inline > *'),
        weeklyStatConfig = (value, week) => {
            const labelColor = weeks[todayWeek] == week ? config.colors.primary : config.colors.textMuted;
            let fillColor;
            if (value > 0) {
                if (value >= 80) {
                    fillColor = config.colors.success;
                } else if (value >= 60) {
                    fillColor = config.colors.warning;
                } else {
                    fillColor = config.colors.danger;
                }
            } else {
                fillColor = config.colors.textMuted;
            }

            return {
                series: [value],
                chart: {
                    width: 50,
                    height: 50,
                    type: 'radialBar'
                },
                plotOptions: {
                    radialBar: {
                        // strokeWidth: '8',
                        hollow: {
                            margin: 0,
                            size: '35%'
                        },
                        dataLabels: {
                            show: true,
                            name: {
                                formatter: function (val) {
                                    return week;
                                },
                                offsetY: 5,
                                color: labelColor,
                                fontSize: '14px',
                                show: true
                            },
                            value: {
                                show: false
                            }
                        }
                    }
                },
                fill: {
                    type: 'solid',
                    colors: fillColor
                },
                stroke: {
                    lineCap: 'round'
                },
                grid: {
                    padding: {
                        top: -10,
                        bottom: -15,
                        left: -10,
                        right: -10
                    }
                },
                states: {
                    hover: {
                        filter: {
                            type: 'none'
                        }
                    },
                    active: {
                        filter: {
                            type: 'none'
                        }
                    }
                }
            }
        };
    if (typeof weeklyStatElements !== undefined && weeklyStatElements !== null) {
        weeklyStatElements.forEach((el, idx) => {
            const stats = [10, 90, 60, 20, -1, -1, -1];
            const weeklyStat = new ApexCharts(el, weeklyStatConfig(stats[idx], weeks[idx + 1]));
            weeklyStat.render();
        })
    }
})();
