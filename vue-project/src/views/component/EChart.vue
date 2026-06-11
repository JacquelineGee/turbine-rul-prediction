<template>
    <div ref="chartRef" class="chart"></div>
</template>

<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue';
import * as echarts from 'echarts';

const chartRef = ref<HTMLElement | null>(null);
const chartInstance = ref<echarts.ECharts | null>(null);

const options = {
    title: {
        text: 'ECharts 示例',
    },
    tooltip: {},
    legend: {
        data: ['销量'],
    },
    xAxis: {
        data: ['衬衫', '羊毛衫', '雪纺衫', '裤子', '高跟鞋', '袜子'],
    },
    yAxis: {},
    series: [
        {
            name: '销量',
            type: 'bar',
            data: [5, 20, 36, 10, 10, 20],
        },
    ],
};

onMounted(() => {
    if (chartRef.value) {
        chartInstance.value = echarts.init(chartRef.value);
        chartInstance.value.setOption(options);
    }
});

watch(options, (newOptions) => {
    if (chartInstance.value) {
        chartInstance.value.setOption(newOptions);
    }
});
</script>

<style>
.chart {
    display: flex;
    justify-content: center;
    align-items: center;
    width:100%;
    height:400px;
    /* height: 100vh; */
}
</style>