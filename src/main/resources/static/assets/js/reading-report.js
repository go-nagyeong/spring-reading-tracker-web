'use strict';

(function () {
    // 독서 통계 차트
    // --------------------------------------------------------------------
    const readingStatChartEl = document.querySelector('#readingStatChart'),
        readingStatChartOptions = {
            series: [
                {
                    name: '2023',
                    type: 'column',
                    data: [2, 0, 3, 5, 1, 3, 3, 1, 5, 2, 3, 1]
                },
                {
                    name: '2024',
                    type: 'column',
                    data: [2, 0, 3, 5, 1, 3, 3, 1, 5, 2, 3, 1]
                },
            ],
            chart: {
                width: '100%',
                type: 'bar',
                toolbar: { show: false }
            },
            plotOptions: {
                bar: {
                    columnWidth: '70%',
                    dataLabels: {
                        position: 'top'

                    }
                }
            },
            colors: [config.colors.secondary, config.colors.primary],
            title: {
                text: '연간 독서',
                offsetY: 20,
                style: {
                    fontSize: '1rem',
                    fontWeight: 600
                }
            },
            dataLabels: {
                style: {
                    colors: [config.colors.bodyColor],
                    fontWeight: 500
                },
                offsetY: -20
            },
            legend: {
                show: true,
                horizontalAlign: 'left',
                position: 'top',
                markers: {
                    height: 8,
                    width: 8,
                    radius: 12,
                    offsetX: -3
                },
                itemMargin: {
                    horizontal: 10
                }
            },
            grid: {
                padding: {
                    bottom: 0
                }
            },
            xaxis: {
                categories: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
                labels: {
                    style: {
                        fontSize: '0.875rem',
                        colors: config.colors.headingColor
                    }
                },
                axisTicks: {
                    show: false
                },
                axisBorder: {
                    show: false
                }
            },
            yaxis: {
                show: false
            },
            responsive: [
                {
                    breakpoint: 768,
                    options: {
                        xaxis: {
                            categories: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
                            labels: {
                                style: {
                                    fontSize: '0.8125rem'
                                }
                            }
                        },
                        plotOptions: {
                            bar: {
                                borderRadius: 10,
                                columnWidth: '80%'
                            }
                        }
                    }
                },
            ],
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
        };
    if (typeof readingStatChartEl !== undefined && readingStatChartEl !== null) {
        const readingStatChart = new ApexCharts(readingStatChartEl, readingStatChartOptions);
        readingStatChart.render();
    }

    // 도서 분야/작가 통계 차트
    // --------------------------------------------------------------------
    const bookGenreChartEl = document.querySelector('#bookGenreChart'),
        donutChartConfig = (values, labels, title) => {
            return {
                labels: labels,
                labelValues: labels,
                series: values,
                chart: {
                    width: '100%',
                    type: 'donut',
                    events: {
                        dataPointSelection: (event, chartContext, config) => {
                            console.log(config.w.config.labels[config.dataPointIndex]);
                            console.log(config.w.config.labelValues[config.dataPointIndex]);
                        }
                    }
                },
                colors: [config.colors.primary, config.colors.secondary, config.colors.info, config.colors.success],
                stroke: {
                    width: 5,
                    colors: [config.colors.cardColor]
                },
                title: {
                    text: title,
                    offsetY: 20,
                    style: {
                        fontSize: '1rem',
                        fontWeight: 600
                    }
                },
                dataLabels: {
                    enabled: false,
                },
                legend: {
                    position: 'right',
                    width: 140
                },
                plotOptions: {
                    pie: {
                        donut: {
                            size: '75%',
                            labels: {
                                show: true,
                                value: {
                                    fontSize: '1.25rem',
                                    fontFamily: 'Public Sans',
                                    color: config.colors.headingColor,
                                    offsetY: -20,
                                    formatter: function (val) {
                                        return parseInt(val) + '%';
                                    }
                                },
                                name: {
                                    offsetY: 20,
                                    fontSize: '0.875rem',
                                    fontFamily: 'Public Sans'
                                }
                            }
                        }
                    }
                },
                responsive: [
                    {
                        breakpoint: 991,
                        options: {
                            chart: {
                                width: '80%'
                            },
                        }
                    },
                    {
                        breakpoint: 768,
                        options: {
                            chart: {
                                width: '100%'
                            },
                            stroke: {
                                width: 4,
                            },
                            legend: {
                                width: 120
                            },
                            plotOptions: {
                                pie: {
                                    donut: {
                                        size: '70%',
                                    }
                                }
                            }
                        },
                    },
                ],
                states: {
                    hover: {
                        filter: { type: 'none' }
                    },
                    active: {
                        filter: { type: 'none' }
                    }
                },
            }
        };
    if (typeof bookGenreChartEl !== undefined && bookGenreChartEl !== null) {
        const bookGenreChart = new ApexCharts(bookGenreChartEl, donutChartConfig([65, 15, 10, 10],['소설', '자기계발', '비문학', '시'], '도서 분야'));
        bookGenreChart.render();
    }
})();
