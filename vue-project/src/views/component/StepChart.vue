<template>
    <div style="position: relative; width: 100%; height: 200px;">
        <div id="chart" ref="chart" style="width: 100%; height: 200px;"></div>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue';
import * as echarts from 'echarts';

// 定义父组件传递的 `steps` 作为 `props`
const props = defineProps<{
    steps: { desc: string }[]; // 接收文本数组
}>();

const chart = ref<HTMLElement | null>(null);

onMounted(() => {
    initChart();
});

// 监听 `steps` 变化，动态更新图表
watch(() => props.steps, () => {
    initChart();
}, { deep: true });

const initChart = () => {
    if (chart.value) {
        const myChart = echarts.init(chart.value);

        const radiusOffset = 15;
        const data = props.steps.map((step, index) => ({
            name: step.desc,
            value: [100 + index * 150, 150],
        }));

        const linesData = data.slice(0, -1).map((point, i) => {
            const nextPoint = data[i + 1];
            return {
                coords: [
                    [point.value[0] + radiusOffset, point.value[1]], // 线条起点，避开左侧点
                    [nextPoint.value[0] - radiusOffset, nextPoint.value[1]], // 线条终点，避开右侧点
                ],
            };
        });

        const option = {
            tooltip: {
                trigger: 'item',
                formatter: (params: any) => params.name,
            },
            xAxis: { show: false },
            yAxis: { show: false },
            series: [
                {
                    type: 'scatter',
                    coordinateSystem: 'cartesian2d',
                    symbolSize: 20,
                    data,
                    itemStyle: { color: '#0026ff' },
                    label: {
                        show: true,
                        position: 'bottom',
                        fontSize: 14,
                        fontWeight: 'bold',
                        color: '#000',
                        width: 100,
                        lineHeight: 20,
                        overflow: 'break',
                        formatter: (params: any) => params.name,
                    },
                    emphasis: { scale: 1.5 },
                },
                {
                    type: 'lines',
                    coordinateSystem: 'cartesian2d',
                    polyline: false,
                    data: linesData,
                    lineStyle: {
                        color: '#003355',
                        width: 1.5,
                    },
                    silent: true,
                },
            ],
        };

        myChart.setOption(option);
        window.addEventListener('resize', () => myChart.resize());
    }
};
</script>

<style scoped>
#chart {
    width: 100%;
    height: 200px;
}
</style>