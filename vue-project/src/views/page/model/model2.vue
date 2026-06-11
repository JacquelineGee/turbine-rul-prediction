<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import NavMenu from '../../component/NavMenu.vue';
import BottomLine from '@/views/component/BottomLine.vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { PieChart } from 'echarts/charts';
import { LineChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components';
import VChart from 'vue-echarts';
import { ElTable, ElTableColumn, ElMessage, ElSelect, ElOption, ElForm, ElFormItem, ElButton } from 'element-plus';
import { StarFilled, Operation } from '@element-plus/icons-vue';
import GetPDF from '@/views/component/GetPDF.vue';

const exportSection = ref<HTMLElement | null>(null);

// 注册 ECharts 组件
use([CanvasRenderer, LineChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent]);

// 定义 chartOptions 绑定图表数据
const rulchartOptions = ref({
  title: {
    text: '寿命预测趋势',
    left: 'center',
  },
  tooltip: {
    trigger: 'axis',
  },
  legend: {
    data: ['实际寿命', '预测寿命'],
    bottom: 10,
  },
  xAxis: {
    type: 'category',
    data: [], // 初始为空
    name: '引擎ID',
  },
  yAxis: {
    type: 'value',
    name: '寿命（年）',
  },
  series: [
    {
      name: '实际寿命',
      type: 'line',
      data: [],
      smooth: true,
      lineStyle: { color: '#5470C6' },
    },
    {
      name: '预测寿命',
      type: 'line',
      data: [],
      smooth: true,
      lineStyle: { color: '#EE6666' },
    },
  ],
});

// 定义训练损失图表
const lossChartOptions = ref({
  title: {
    text: '训练损失趋势',
    left: 'center',
  },
  tooltip: {
    trigger: 'axis',
  },
  xAxis: {
    type: 'category',
    data: [], // 初始为空
    name: 'Epoch',
  },
  yAxis: {
    type: 'value',
    name: 'Loss',
  },
  series: [
    {
      name: 'Loss',
      type: 'line',
      data: [],
      smooth: true,
      lineStyle: { color: '#91CC75' },
    },
  ],
});

const options = ref({
  title: {
    text: '预测误差',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: 'RUL proportion',
      type: 'pie',
      radius: '45%',
      data: [
        {
          value: null, name: '0%-20%',
        },
        {
          value: null, name: '20%-40%',
        },
        {
          value: null, name: '大于40%',
        }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
});

const remainRUL = ref({
  title: {
    text: '剩余寿命',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: 'RUL',
      type: 'pie',
      radius: '45%',
      data: [
        {
          value: null, name: '大于0.8年',
        },
        {
          value: null, name: '0.4年-0.8年',
        },
        {
          value: null, name: '小于0.4年',
        }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
});

interface dataSet {
  time: string
  actual: number
  predicted: number
}

// 表格数据
const tableData = ref<dataSet[]>([]);

const cellStyle = ({ row, column, rowIndex }: { row: dataSet; column: any; rowIndex: number }) => {
  if (rowIndex === 2) {
    return { background: '#fffaeb', color: '#b90000' };
  } else
    if (rowIndex === 3) {
      return {
        fontWeight: 'bold'
      };
    }
    else {
      return {};
    }
};

// 加载数据的状态
const predicted = ref(false);
const show = ref(false);
const ifloading = ref(false);
const datasetLoaded = ref(false); // 数据集是否加载成功
const loadingDataset = ref(false); // 是否正在加载数据集

// 数据集相关
const datasets = ref<{ datasetId: number; datasetName: string; datasetUrl: string }[]>([]); // 存储数据集列表
const selectedDataset = ref<{ datasetId: number; datasetName: string; datasetUrl: string } | null>(null); // 存储用户选择的数据集

// 从后端获取数据集列表
const fetchDatasets = async () => {
  try {
    const response = await axios.get('http://127.0.0.1:8080/dataSet');
    datasets.value = response.data.data.map((item: any) => ({
      datasetId: item.datasetId,
      datasetName: item.datasetName,
      datasetUrl: item.datasetUrl,
    }));
  } catch (error) {
    console.error('获取数据集失败:', error);
    ElMessage.error('获取数据集失败，请检查服务器！');
  }
};

// 加载数据集
const loadDataset = async () => {
  if (!selectedDataset.value) {
    ElMessage.warning('请先选择一个数据集！');
    return;
  }

  try {
    loadingDataset.value = true;
    const response = await axios.get('http://127.0.0.1:5000/load-dataset', {
      params: {
        datasetName: selectedDataset.value.datasetName,
        datasetUrl: selectedDataset.value.datasetUrl,
      },
    });

    if (response.status === 200) {
      datasetLoaded.value = true;
      ElMessage.success('数据集加载成功！');
    }
  } catch (error) {
    console.error('加载数据集失败:', error);
    ElMessage.error('加载数据集失败，请检查服务器！');
  } finally {
    loadingDataset.value = false;
  }
};

// 从后端获取预测数据
const predictStart = async () => {
  if (!selectedDataset.value) {
    ElMessage.warning('请先选择一个数据集！');
    return;
  }

  try {
    data_show[0].index = [];
    data_show[1].index = [];
    data_show[2].index = [];

    data_RUL[0].index = [];
    data_RUL[1].index = [];
    data_RUL[2].index = [];
    predicted.value = true;
    show.value = false;
    ifloading.value = true;

    const response = await axios.get('http://127.0.0.1:5000/get-rul-data', {
      params: {
        datasetName: selectedDataset.value.datasetName,
        modelId: 2
      },
    });
    const { labels, predictions, losses } = response.data;

    // 处理数据并更新 UI
    rulchartOptions.value.xAxis.data = labels.map((_, index) => `引擎 ${index + 1}`);
    rulchartOptions.value.series[0].data = labels;
    rulchartOptions.value.series[1].data = predictions;

    let numberRul0 = 0, numberRul1 = 0, numberRul2 = 0, remain8 = 0, remain4 = 0, remain0 = 0;
    for (let i = 0; i < labels.length; i++) {
      if (Math.abs(labels[i] - predictions[i]) / labels[i] <= 0.2) {
        numberRul0++;
        data_show[0].index.push(i + 1);
      } else if (Math.abs(labels[i] - predictions[i]) / labels[i] <= 0.4) {
        numberRul1++;
        data_show[1].index.push(i + 1);
      } else {
        numberRul2++;
        data_show[2].index.push(i + 1);
      }
      if (predictions[i] >= 0.8) {
        remain8++;
        data_RUL[0].index.push(i + 1);
      } else if (predictions[i] >= 0.4) {
        remain4++;
        data_RUL[1].index.push(i + 1);
      } else {
        remain0++;
        data_RUL[2].index.push(i + 1);
      }
    }
    options.value.series[0].data[0].value = numberRul0;
    options.value.series[0].data[1].value = numberRul1;
    options.value.series[0].data[2].value = numberRul2;

    remainRUL.value.series[0].data[0].value = remain8;
    remainRUL.value.series[0].data[1].value = remain4;
    remainRUL.value.series[0].data[2].value = remain0;

    lossChartOptions.value.xAxis.data = losses.map((_, index) => `Epoch ${index + 1}`);
    lossChartOptions.value.series[0].data = losses;

    // 更新表格数据
    tableData.value = labels.map((value: number, index: number) => ({
      time: `引擎 ${index + 1}`,
      actual: Math.round((value + Number.EPSILON) * 1000000) / 1000000,
      predicted: Math.round((predictions[index] ?? 0 + Number.EPSILON) * 1000000) / 1000000, // 避免 undefined 值
    }));
  } catch (error) {
    console.error('获取数据失败:', error);
    ElMessage.error('数据加载失败，请检查服务器！');
  } finally {
    predicted.value = false;
    show.value = true;
    ifloading.value = false;
  }
};

// 保存预测记录
const savePrediction = async () => {
  try {
    const response = await axios.post('http://127.0.0.1:8080/save-prediction', {
      labels: rulchartOptions.value.series[0].data,
      predictions: rulchartOptions.value.series[1].data,
      losses: lossChartOptions.value.series[0].data,
      modelId: '2',
      userId: user_info.userId,
      datasetId: selectedDataset.value.datasetId
    });
    console.log('保存成功:', response.data);
    ElMessage.success('预测记录保存成功！');
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error('保存失败，请重试！');
  }
};

// 处理菜单跳转
const handleSelect = (index: string) => {
  console.log('跳转到:', index);
};

// 组件挂载时获取数据集列表
onMounted(() => {
  fetchDatasets();
});
const data_show = [
  { error: '0%-20%', index: [] },
  { error: '20%-40%', index: [] },
  { error: '>40%', index: [] }
]

const data_RUL = [
  { Rul: '>0.8年', index: [] },
  { Rul: '0.4年-0.8年', index: [] },
  { Rul: '<0.4年', index: [] },
  { Rul: '预测建议', index: '对于剩余寿命小于0.4年的进行检修' }
]

const stored_user = localStorage.getItem("user_information");
const user_info = JSON.parse(stored_user);
</script>

<template>
  <NavMenu />
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <el-scrollbar>
          <el-menu :default-openeds="['1']" router @select="handleSelect">
            <el-sub-menu index="1">
              <template #title>
                <el-icon>
                  <Operation />
                </el-icon>模型选择
              </template>
              <el-menu-item index="/model1">
                <el-icon>
                  <StarFilled />
                </el-icon>LSTM模型
              </el-menu-item>
              <el-menu-item index="/model2">
                <el-icon>
                  <StarFilled />
                </el-icon>GRU模型
              </el-menu-item>
              <el-menu-item index="/model3">
                <el-icon>
                  <StarFilled />
                </el-icon>Wiener+LSTM模型
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-scrollbar>
      </el-aside>
      <el-main>
        <div class="LSTM-title">
          <span>基于GRU模型的寿命预测模型</span>
        </div>
        <el-form :model="selectedDataset" label-width="120px" style="margin-top: 20px;">
          <el-form-item label="选择数据集">
            <el-select v-model="selectedDataset" placeholder="请选择数据集" style="width: 100%;" value-key="datasetId">
              <el-option v-for="dataset in datasets" :key="dataset.datasetId" :label="dataset.datasetName"
                :value="dataset" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <div style="text-align: center;">
              <el-button type="primary" @click="loadDataset" :loading="loadingDataset" :disabled="!selectedDataset">
                {{ loadingDataset ? '加载中...' : '加载数据集' }}
              </el-button>
              <el-button type="success" @click="predictStart" :disabled="!datasetLoaded || predicted"
                style="margin-left: 10px;">
                开始预测
              </el-button>
            </div>
          </el-form-item>
        </el-form>
        <!-- ECharts 图表 -->
        <!-- <div ref="exportSection"> -->
        <div v-if="ifloading === true" style="margin-top: 20px">
          <el-card v-loading="ifloading" style="height: 100vh"></el-card>
        </div>
        <div v-if="show === true">
          <GetPDF buttonText="导出报告" :filename="user_info.userName+'-GRU-report.pdf'">
            <div>
              <el-card style="margin-top: 20px;">
                <div style="width: 800px; height: 400px; margin: 0 auto;">
                  <v-chart :option="rulchartOptions" />
                </div>
              </el-card>
              <!-- 训练损失图表 -->
              <el-card style="margin-top: 20px;page-break-before: always;">
                <div style="width: 800px; height: 400px; margin: 0 auto;">
                  <v-chart :option="lossChartOptions" />
                </div>
              </el-card>
              <el-card style="margin-top: 20px;page-break-before: always;">
                <div
                  style="display: flex; justify-content: space-between; width: 100%; height: 380px; margin: 0 auto; margin-top: 30px;">
                  <div style="width: 48%; height: 100%;">
                    <v-chart :option="options" />
                  </div>
                  <div style="width: 48%; height: 100%;">
                    <v-chart :option="remainRUL" />
                  </div>
                </div>
              </el-card>
              <!-- 预测数据表格 -->
              <el-card style="margin-top: 20px;page-break-before: always;">
                <div class="report-title">
                  <span style="text-align: center;">预测分析报告</span>
                </div>
                <!-- <el-table :data="tableData" border style="width: 100%; margin-top: 10px;" :cell-style="cellStyle">
                <el-table-column prop="time" label="引擎ID" width="120" />
                <el-table-column prop="actual" label="实际寿命" />
                <el-table-column prop="predicted" label="预测寿命" />
              </el-table> -->
                <el-table :data="data_show" border style="width: 100%; margin-top: 10px;" :cell-style="cellStyle">
                  <el-table-column prop="error" label="预测误差" width="120" />
                  <el-table-column prop="index" label="引擎编号" />
                </el-table>

                <el-table :data="data_RUL" border style="width: 100%; margin-top: 10px;" :cell-style="cellStyle">
                  <el-table-column prop="Rul" label="预测寿命" width="120" />
                  <el-table-column prop="index" label="引擎编号" />
                </el-table>
              </el-card>
              <!-- 保存按钮 -->
            </div>
          </GetPDF>
          <div style="text-align: center; margin-top: 20px;">
            <el-button type="primary" @click="savePrediction">保存预测记录</el-button>
            <!-- <GetPDF :targetRef="exportSection" /> -->
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
  <!-- <BottomLine /> -->
</template>

<style scoped>
/* 统一对齐表格和图表 */
.el-card {
  width: 80%;
  margin: 0 auto;
}

.LSTM-title {
  text-align: center;
  font-size: 2.2em;
  font-weight: bold;
}

.report-title {
  text-align: center;
  font-size: 1.7em;
  font-weight: bold;
}
</style>