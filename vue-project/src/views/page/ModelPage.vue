<script lang="ts" setup>
import NavMenu from '../component/NavMenu.vue';
import BottomLine from '../component/BottomLine.vue';
import { StarFilled, Operation } from '@element-plus/icons-vue';
import StepChart from '../component/StepChart.vue';

// 注册必要的 ECharts 组件

const handleSelect = (index: string) => {
  console.log('跳转到:', index);
};
const LSTM_Datas = [
  { desc: '双向LSTM层：捕捉时间序列的长期依赖关系' },
  { desc: '维度转换：调整LSTM输出的维度以适配卷积层' },
  { desc: '1D卷积层：提取时间序列中的局部特征' },
  { desc: 'Flatten：将卷积层的输出展平为一维向量' },
  { desc: '全连接层：将特征映射到更高维度的表示并逐步降维' },
  { desc: '输出层：生成最终的预测值' },
];

const GRU_Datas = [
  { desc: '双向GRU层：捕捉时间序列的长期依赖关系' },
  { desc: '维度转换：调整GRU输出的维度以适配卷积层' },
  { desc: '1D卷积层：提取时间序列中的局部特征' },
  { desc: 'Flatten：将卷积层的输出展平为一维向量' },
  { desc: '全连接层：将特征映射到更高维度的表示并逐步降维' },
  { desc: '输出层：生成最终的预测值' },
]

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
        <div class="section-4">
          <div class="section-apply">
            <div class="section-4-1">
              <span class="apply-subtitle">基于LSTM的模型</span>
              <div class="apply-img">
                <img src="https://vue-project-image.oss-cn-shanghai.aliyuncs.com/%E6%B5%81%E7%A8%8B.png"
                  style="width:400px;" alt="">
              </div>
              <StepChart :steps="LSTM_Datas" />
              <p style="text-indent: 2em;">
                LSTM 在燃气轮机剩余寿命预测中具有显著优势。由于燃气轮机运行过程中传感器采集的数据通常是长时间序列，LSTM
                通过遗忘门、输入门和输出门的协同作用，能够有效捕捉长期依赖关系，从而准确预测设备的健康状态和剩余寿命。此外，LSTM 可以记忆远距离的时间依赖特征，在复杂工况下依然能够保持较好的预测能力。然而，由于 LSTM
                结构较复杂，计算开销较大，训练时间较长，在实时预测场景下可能会受到一定限制。
              </p>
            </div>

            <div class="section-4-2">
              <span class="apply-subtitle" style="margin-top:20px;">基于GRU的模型</span>
              <div class="apply-img">
                <img src="https://vue-project-image.oss-cn-shanghai.aliyuncs.com/GRU%E6%B5%81%E7%A8%8B.png"
                  style="width:600px;" alt="">
              </div>
              <StepChart :steps="GRU_Datas" />
              <p style="text-indent: 2em;">
                GRU 在燃气轮机剩余寿命预测中同样表现出色。相比 LSTM，GRU 结构更加简洁，减少了计算量，提高了模型的训练和推理速度，使其更适用于实时性要求较高的燃气轮机健康管理任务。GRU
                通过更新门和重置门的设计，在保留关键历史信息的同时减少了冗余计算，提高了预测效率。在短时间序列数据或计算资源受限的情况下，GRU 能够提供与 LSTM
                相近的预测精度，同时降低计算成本，适用于在线监测和快速预测应用场景。
              </p>
            </div>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
  <BottomLine />
</template>

<style scoped>
/* 可以根据需要添加样式 */
.section-apply {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  background-color: white;
  border-radius: 8px;
  padding: 20px 20px;
}

.section-apply .section-4-1 {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
}

.section-apply .section-4-2 {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
}

.section-apply span {
  font-size: 2.5em;
  font-weight: bold;
  margin-bottom: 30px;
}

.section-apply p {
  color: #3b3b3b;
  font-size: 20px;
  margin-bottom: 30px;
  padding: 0px 100px 0px 100px;
  line-height: 1.8;
}

.section-apply .section-4-1 .apply-title {
  font-size: 2.5em;
  font-weight: bold;
  margin-bottom: 30px;
}

.section-apply .section-4-1 .apply-subtitle {
  font-size: 1.8em;
}

.section-apply .section-4-2 .apply-subtitle {
  font-size: 1.8em;
}

.apply-image {
  display: flex;
  justify-content: center;
  flex-direction: row;
  align-items: center;
}
</style>