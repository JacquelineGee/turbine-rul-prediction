<script lang="ts" setup>
import NavMenu from '../component/NavMenu.vue';
import BottomLine from '../component/BottomLine.vue';
import { ref, onMounted, onUnmounted } from 'vue';
import axios from '@/utils/axios';
import { useRoute } from 'vue-router';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { PieChart } from 'echarts/charts';
import { LineChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components';
import VChart from 'vue-echarts';
import { ElTable, ElTableColumn, ElMessage, ElDescriptions, ElDescriptionsItem } from 'element-plus';
import { Back } from '@element-plus/icons-vue';
import GetPDF from '../component/GetPDF.vue';
import DeepSeekApiComponent from '../component/DeepSeekApiComponent.vue';

const exportSection = ref<HTMLElement | null>(null);

const route = useRoute();
const prediction = ref<any>(null); // 使用 any 类型避免类型检查问题
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
          value: 0, name: '0%-20%',
        },
        {
          value: 0, name: '20%-40%',
        },
        {
          value: 0, name: '大于40%',
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
          value: 0, name: '大于0.8年',
        },
        {
          value: 0, name: '0.4年-0.8年',
        },
        {
          value: 0, name: '小于0.4年',
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

// 判断是否有损失数据
const hasLossData = ref(false);

// 从后端获取数据
const fetchPredictionDetail = async () => {
  try {
    predicted.value = true;
    show.value = false;
    const id = Number(route.params.id);
    ifloading.value = true;
    const response = await axios.post(`http://127.0.0.1:8080/prediction/${id}`);
    prediction.value = response.data.data; // 将数据赋值给 prediction 对象
    if (response)
      console.log(prediction.value.predictModel);
    // 格式化时间，去掉 T 和 +08:00
    if (prediction.value.predictTime) {
      prediction.value.predictTime = prediction.value.predictTime
        .replace('T', ' ') // 将 T 替换为空格
        .replace(/\.\d{3}\+\d{2}:\d{2}$/, ''); // 去掉毫秒和时区部分
    }

    const { labelsAsList, predictionsAsList, lossesAsList } = prediction.value;

    // 处理数据并更新 UI
    rulchartOptions.value.xAxis.data = labelsAsList.map((_, index) => `引擎 ${index + 1}`);
    rulchartOptions.value.series[0].data = labelsAsList;
    rulchartOptions.value.series[1].data = predictionsAsList;

    let numberRul0 = 0, numberRul1 = 0, numberRul2 = 0, remain8 = 0, remain4 = 0, remain0 = 0;
    for (let i = 0; i < labelsAsList.length; i++) {
      if (Math.abs(labelsAsList[i] - predictionsAsList[i]) / labelsAsList[i] <= 0.2) {
        numberRul0++;
        data_show[0].index.push(i + 1);
      } else if (Math.abs(labelsAsList[i] - predictionsAsList[i]) / labelsAsList[i] <= 0.4) {
        numberRul1++;
        data_show[1].index.push(i + 1);
      } else {
        numberRul2++;
        data_show[2].index.push(i + 1);
      }
      if (predictionsAsList[i] >= 0.8) {
        remain8++;
        data_RUL[0].index.push(i + 1);
      } else if (predictionsAsList[i] >= 0.4) {
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

    // 检查是否有损失数据
    if (lossesAsList && lossesAsList.length > 0) {
      hasLossData.value = true;
      lossChartOptions.value.xAxis.data = lossesAsList.map((_, index) => `Epoch ${index + 1}`);
      lossChartOptions.value.series[0].data = lossesAsList;
    } else {
      hasLossData.value = false;
    }

    // 更新表格数据
    tableData.value = labelsAsList.map((value: number, index: number) => ({
      time: `引擎 ${index + 1}`,
      actual: value,
      predicted: predictionsAsList[index] ?? 0, // 避免 undefined 值
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
// const chunkedTableData = computed(() => {
//   const chunkSize = 14; // 每页显示 14 行
//   const chunks = [];
//   for (let i = 0; i < tableData.value.length; i += chunkSize) {
//     chunks.push(tableData.value.slice(i, i + chunkSize));
//   }
//   return chunks;
// });
const deepseekResponse = ref('');
const channel = new BroadcastChannel('deepseekResponseChannel');
onMounted(() => {
  fetchPredictionDetail();
  console.log('deepSeekApi.value:', deepSeekApi.value); // 调试信息
  channel.onmessage = (event) => {
    if (event.data.type === 'deepseekResponse') {
      deepseekResponse.value = event.data.content;
    }
  };
});

onUnmounted(() => {
  channel.close();
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
// 获取 DeepSeekApiComponent 的引用
const deepSeekApi = ref<InstanceType<typeof DeepSeekApiComponent> | null>(null);

// 发送预测详情给 AI
const sendPredictionToAI = () => {
  if (!prediction.value) {
    console.error('prediction.value 为 null 或 undefined');
    return;
  }



  // alert("点击了ai结果评价");

  // 动态构建预测详情内容
  let predictionDetails = `
    预测模型: ${prediction.value.predictModel}
    预测编号: ${prediction.value.predictId}
    预测用户: ${prediction.value.userName}
    使用的数据集: ${prediction.value.datasetName}
    预测时间: ${prediction.value.predictTime}
    实际寿命: ${JSON.stringify(prediction.value.labelsAsList)}
    预测寿命: ${JSON.stringify(prediction.value.predictionsAsList)}
  `;

  // 如果训练损失存在，则添加到预测详情中
  if (prediction.value.lossesAsList && prediction.value.lossesAsList.length > 0) {
    predictionDetails += `
    训练损失: ${JSON.stringify(prediction.value.lossesAsList)}
    `;
  }
  // alert(deepSeekApi.value)
  // 向 DeepSeekApiComponent 发送消息
  if (deepSeekApi.value) {
    // alert("发送消息");
    deepSeekApi.value.sendMessage(`预测寿命和实际寿命均为编号从1开始的数据，请针对预测详情分组给出各个引擎的维护建议，同时列出急需维护引擎列表：\n${predictionDetails}，以这种格式输出“# 引擎维护建议分析

根据预测寿命与实际寿命的对比分析，现给出详细维护建议：

## 一、误差分组维护建议

### 1. 严重高估引擎（误差>0.3）15台
**编号**：3,12,22,25,26,31,39,52,61,70,71,72,79,5,6  
**维护建议**：
- ✅ 立即停机进行深度检测（优先处理25号引擎）
- ✅ 检查润滑系统和轴承磨损情况
- ✅ 验证温度传感器准确性
- ✅ 建议更换涡轮叶片（特别是39、70号引擎）

### 2. 中度高估引擎（0.1<误差≤0.3）18台
**编号**：1,2,4,8,9,10,15,16,19,27,30,33,38,45,50,54,57,69  
**维护建议**：
- ✅ 下周内安排预防性维护
- ✅ 重点检查燃油喷嘴状态
- ✅ 增加振动监测至每周2次
- ✅ 建议8、9号引擎提前更换过滤器

### 3. 正常范围引擎（-0.1≤误差≤0.1）19台
**编号**：7,11,13,14,17,18,20,21,23,28,29,32,34,35,36,37,40,42,43  
**维护建议**：
- ✅ 保持当前维护计划
- ✅ 每月常规检查即可
- ✅ 重点关注13号引擎（误差接近临界值）

### 4. 中度低估引擎（-0.3≤误差<-0.1）16台
**编号**：24,41,44,46,47,48,49,51,53,55,56,58,59,60,62,63  
**维护建议**：
- ✅ 可延长25%维护间隔
- ✅ 检查是否存在过度润滑
- ✅ 验证48号引擎实际磨损数据

### 5. 严重低估引擎（误差<-0.3）12台
**编号**：64-68,73-78,80  
**维护建议**：
- ✅ 检查数据采集系统
- ✅ 重新校准预测模型参数
- ✅ 75号引擎建议减少非必要检测

## 二、急需维护引擎TOP10清单

| 排名 | 引擎编号 | 预测寿命 | 实际寿命 | 误差值  | 紧急程度       |
|------|----------|----------|----------|---------|----------------|
| 1    | 25       | 0.645    | 0.967    | -0.322  |   紧急（红色）    |
| 2    | 39       | 0.801    | 0.947    | -0.146  |   紧急（红色）    |
| 3    | 70       | 0.955    | 0.913    | +0.042  |   高（橙色）      |
| 4    | 71       | 0.946    | 0.787    | +0.159  |   高（橙色）      |
| 5    | 22       | 0.884    | 0.740    | +0.144  |   高（橙色）      |
| 6    | 12       | 0.647    | 0.827    | -0.180  |   中（黄色）      |
| 7    | 3        | 0.275    | 0.460    | -0.185  |   中（黄色）      |
| 8    | 5        | 0.633    | 0.607    | +0.026  |   中（黄色）      |
| 9    | 6        | 0.671    | 0.620    | +0.051  |   中（黄色）      |
| 10   | 61       | 0.783    | 0.807    | -0.024  |   中（黄色）      |

## 三、特别注意事项
1. **25号引擎**实际磨损已达临界值（0.967），建议立即停运
2. **70-72号引擎组**存在系统性预测偏差
3. 数据集FD001在寿命末期（>0.8阶段）预测准确率下降明显
4. 员工001操作的预测结果平均偏高8.7%

## 四、后续改进建议
1. 对红色标记引擎建立48小时响应机制
2. 每周生成预测偏差分析报告
3. 对连续3次预测不准的引擎升级监测设备
4. 建议3月份加强季节性维护

> **注**：所有编号均为用户提供的原始编号，维护记录请对应原始设备ID”`, false, true);
  }
};
</script>

<template>
  <NavMenu />
  <div style="margin-left:20px;margin-top:20px;">
    <router-link to="/data-show">
      <el-button text>
        <el-icon>
          <Back />
        </el-icon>
        返回
      </el-button>
    </router-link>
    <GetPDF buttonText="导出报告" :filename="user_info.userName + '-' + prediction.modelName + '-report.pdf'" :userId="user_info.userId"
      :hideClasses="['predict-AI-button']" :showClasses="deepseekResponse ? ['AI-text-show'] : []" v-if="prediction">
      <div class="predict-title">
        <span>基于 {{ prediction.modelName }} 的寿命预测模型</span>
      </div>
      <!-- 添加预测信息 -->
      <div class="predict-info" v-if="prediction">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预测编号">{{ prediction.predictId }}</el-descriptions-item>
          <el-descriptions-item label="预测用户">{{ prediction.userName }}</el-descriptions-item>
          <el-descriptions-item label="使用的数据集">{{ prediction.datasetName }}</el-descriptions-item>
          <el-descriptions-item label="预测时间">{{ prediction.predictTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div v-if="show === true"
        style="margin-top:20px;display: flex; justify-content: center;flex-direction: column;align-items: center;">
        <el-card style="margin-top: 20px;width:1100px;">
          <div
            style="display: flex; justify-content: space-between; width: 100%; height: 400px; margin: 0 auto;margin-top: 30px;">
            <div style="width: 520px; height: 400px; margin: 0 auto;">
              <v-chart :option="rulchartOptions" />
            </div>
            <!-- 根据 hasLossData 判断是否显示损失图表 -->
            <div v-if="hasLossData" style="width: 520px; height: 400px; margin: 0 auto;">
              <v-chart :option="lossChartOptions" />
            </div>
          </div>
          <div class="page-break"></div>
        </el-card>
        <el-card style="margin-top: 20px;width:1100px;">
          <div
            style="display: flex; justify-content: space-between; width: 100%; height: 380px; margin: 0 auto;margin-top: 30px;">
            <div style="width: 48%; height: 100%;">
              <v-chart :option="options" />
            </div>
            <div style="width: 48%; height: 100%;">
              <v-chart :option="remainRUL" />
            </div>
          </div>
        </el-card>
        <!-- 预测数据表格 -->
        <div class="page-break"></div>
        <el-card style="margin-top: 20px;width:1100px;">
          <div class="report-title">
            <span style="text-align: center;">预测数据详情</span>
          </div>
          <!-- <div v-for="(chunk, index) in chunkedTableData" :key="index" class="a4-landscape-content"> -->
          <!-- 渲染表格 -->
          <!-- <el-table :data="chunk" border style="width: 100%; margin-top: 10px;" :cell-style="cellStyle">
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
          <!-- 插入分页符 -->
          <!-- <div v-if="index < chunkedTableData.length - 1" class="page-break"></div> -->
          <!-- </div> -->
        </el-card>

        <div v-if="deepseekResponse" class="page-break"></div>
        <el-card class="AI-text-show">
          <div class="ai-text">
            <div v-html="deepseekResponse"></div>
          </div>
        </el-card>
      </div>
      <br>
      <div class="predict-AI-button">
        <el-button type="primary" @click="sendPredictionToAI" style="margin-left: 10px;">
          AI预测结果评价
        </el-button>
      </div>
    </GetPDF>
    <br>
    <DeepSeekApiComponent ref="deepSeekApi" />
  </div>
  <BottomLine />
</template>

<style scoped>
.predict-title {
  text-align: center;
  font-size: 2.2em;
  font-weight: bold;
  margin-bottom: 20px;
}

.report-title {
  text-align: center;
  font-size: 1.7em;
  font-weight: bold;
}

.predict-info {
  width: 1000px;
  margin: 0 auto;
}

.page-break {
  page-break-before: always;
  /* 在元素前插入分页符 */
}

.predict-AI-button {
  text-align: center;
}

.AI-text-show {
  display: none;
  margin-top: 20px;
  width: 1100px;
}

.ai-text {
  display: flex;
  align-items: flex-start;
  margin-bottom: 5px;
  color: #404040;
  margin-top: 10px;
  line-height: 2;
  padding: 18px 40px;
}
</style>