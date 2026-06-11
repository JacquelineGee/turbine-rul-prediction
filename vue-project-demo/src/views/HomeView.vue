<template>
  <div>
    <HeaderView></HeaderView>
    <el-container style="height: 100vh; border: 1px solid #eee">
      <el-container>
        <AsideView></AsideView>
        <el-main>
          <!-- 表格 -->
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
          </el-breadcrumb>
          <br><br>

          <div class="statistic-container">
            <div class="statistic-box">
              <div class="statistic-title">总注册用户数</div>
              <div class="statistic-value">
                {{ value2 }}
              </div>
            </div>
            <div class="statistic-box">
              <div class="statistic-title">新增用户</div>
              <div class="statistic-value">
                {{ newestuser }}
              </div>
            </div>
          </div>
          <!-- ECharts图表容器 -->
          <div class="chart-container">
            <div ref="chartContainer" style="width: 45%; height: 40vh;"></div>
            <div ref="lineChartContainer" style="width: 45%; height: 40vh;"></div>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import axios from 'axios';
import * as echarts from 'echarts'; // 导入echarts
export default {

  data() {
    return {
      username: '',
      userimage: '',
      userId: '',
      operationList: [],
      chartData: [],
      value2: 0,
      newestuser:'',
      option: {
        title: {
          text: '模型调用情况',
          // subtext: 'Fake Data',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'right'
        },
        series: [
          {
            name: 'Access From',
            type: 'pie',
            radius: '50%',
            data: [],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      },
      chartInstance: null, // 存储echarts实例
      lineOption: {
        title: {
          text: '用户登录情况',
          // subtext: 'Fake Data',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: [],
            type: 'line',
            areaStyle: {}
          }
        ]
      },
      lineChartInstance: null,
    }
  },
  created() {
    //this.getUserList()
    this.userId = sessionStorage.getItem('id');
    this.getOperationList();
    this.fetchChartData();
    this.fetchLineChartData();
    this.fetchUserNum();
    this.fetchNewestUser();
  },
  methods: {
    fetchNewestUser(){
      axios.post("http://localhost:8080/api/system/log/NewestUser").then((response) => {
        if (response.data.msg === "success") {
          this.newestuser = response.data.data;
        }
        else {
          console.log("获取最新用户失败");
          this.newestuser = '员工001';
        }
      }

      ).catch(error => {
        console.error("获取最新用户失败", error);
        // 使用模拟数据作为后备
          this.newestuser = '员工001';
      });
    },
    fetchUserNum() {
      axios.post("http://localhost:8080/api/system/log/UserNum").then((response) => {
        if (response.data.msg === "success") {
          this.value2 = response.data.data;
        }
        else {
          console.log("获取用户数量失败");
          this.value2 = 15;
        }
      }

      ).catch(error => {
        console.error("获取用户数量失败", error);
        // 使用模拟数据作为后备
        this.value2 = 15;
      });
    },
    initChart() {
      this.$nextTick(() => {
        this.chartInstance = echarts.init(this.$refs.chartContainer);
        this.chartInstance.setOption(this.option);
        this.lineChartInstance = echarts.init(this.$refs.lineChartContainer);
        this.lineChartInstance.setOption(this.lineOption);

        // 窗口大小变化时重新调整图表大小
        window.addEventListener('resize', this.resizeChart);
      });
    }, resizeChart() {
      if (this.chartInstance) {
        this.chartInstance.resize();
      } if (this.lineChartInstance) {
        this.lineChartInstance.resize();
      }
    },
    fetchChartData() {
      // 这里替换为你的实际API端点
      axios.post("http://localhost:8080/api/system/log/modelUseTimes").then((response) => {
        if (response.data.msg === "success") {
          this.chartData = response.data.data;

          // 转换数据格式以适应ECharts
          const seriesData = this.chartData.map(item => ({
            value: item.modelUseTimes,
            name: item.modelName
          }));

          // 更新图表数据
          this.option.series[0].data = seriesData;

          // 如果图表已初始化，则更新图表
          if (this.chartInstance) {
            this.chartInstance.setOption(this.option);
          }
        }
      }).catch(error => {
        console.error("获取图表数据失败:", error);
        // 使用模拟数据作为后备
        this.option.series[0].data = [
          { value: 4, name: 'LSTM' },
          { value: 4, name: 'GRU' },
          { value: 3, name: 'Wiener' }
        ];
        if (this.chartInstance) {
          this.chartInstance.setOption(this.option);
        }
      });
    },
    fetchLineChartData() {
      axios.post("http://localhost:8080/api/system/log/LoginTimes").then(response => {
        if (response.data.msg === "success") {
          const loginData = response.data.data;

          // 提取日期和次数
          const dates = loginData.map(item => item.logindate);
          const counts = loginData.map(item => item.times);

          // 更新折线图数据
          this.lineOption.xAxis.data = dates;
          this.lineOption.series[0].data = counts;

          // 如果图表已初始化，则更新图表
          if (this.lineChartInstance) {
            this.lineChartInstance.setOption(this.lineOption);
          }
        }
      }).catch(error => {
        console.error("获取折线图数据失败:", error);
        // 使用模拟数据作为后备
        const mockData = [
          { times: 6, logindate: "2025-04-07" },
          { times: 2, logindate: "2025-04-08" },
          { times: 7, logindate: "2025-04-09" },
          { times: 7, logindate: "2025-04-10" },
          { times: 4, logindate: "2025-04-11" },
          { times: 2, logindate: "2025-04-13" }
        ];

        const mockDates = mockData.map(item => item.logindate);
        const mockCounts = mockData.map(item => item.times);

        this.lineOption.xAxis.data = mockDates;
        this.lineOption.series[0].data = mockCounts;

        if (this.lineChartInstance) {
          this.lineChartInstance.setOption(this.lineOption);
        }
      });
    },
    getOperationList() {
      if (this.userId == '8888')
        axios.post("/power/13").then((result) => {
          this.operationList = result.data.data;
        });
      else
        axios.post("/power/112").then((result) => {
          this.operationList = result.data.data;
        });
    },
    op1() {
      this.$router.push('/User');
    },
    op2() {
      this.$router.push('/Role');
    },
    op3() {
      this.$router.push('/op');
    },

    roleManageBind() {
      let str = "角色管理"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
      })
      if (id === '') id = true
      return id
    },
    userManageBind() {
      let str = "用户管理"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
      })
      if (id === '') id = true
      return id
    },
    OpManageBind() {
      let str = "操作管理"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
      })
      if (id === '') id = true
      return id
    },
    FeatureManageBind() {
      let str = "功能菜单管理"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false
      })
      if (id === '') id = true
      return id
    },
    roleSearchBind() {
      let str = "角色查询"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
      })
      if (id === '') id = true
      return id
    },
    roleEditBind() {
      let str = "角色修改"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
        //alert(value.powerName)}
      })
      if (id === '') id = true
      return id
    },
    rolePowerEditBind() {
      let str = "角色权限修改"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
      })
      if (id === '') id = true;
      return id
    },
    roleDeleteBind() {
      let str = "角色删除"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
      })
      if (id === '') id = true;
      return id
    },
    roleAddBind() {
      let str = "添加角色"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
      })
      if (id === '') id = true;
      return id
    },
    userSearchBind() {
      let str = "用户查询"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
      })
      if (id === '') id = true
      return id
    },
    useraddBind() {
      let str = "用户新增"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
      })
      if (id === '') id = true
      return id
    },
    userEditBind() {
      let str = "用户修改"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
      })
      if (id === '') id = true
      return id
    },
    userDeleteBind() {
      let str = "用户删除"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
      })
      if (id === '') id = true
      return id
    },
    userRoleEditBind() {
      let str = "用户角色修改"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = false;
      })
      if (id === '') id = true
      return id
    },
    logout() {
      this.$confirm('即将注销，请确认', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message({
          type: 'success',
          message: '已注销!'
        }
        );
        window.sessionStorage.clear();
        //this.$message.success('已注销');
        this.$router.push("/")
      }).catch(() => {

      });

    },

  },
  mounted() {
    this.username = sessionStorage.getItem('name');

    this.userimage = sessionStorage.getItem('image');
    //alert(this.userimage);

    this.initChart();
  },
  beforeDestroy() {
    // 组件销毁前销毁图表实例
    if (this.chartInstance) {
      this.chartInstance.dispose();
    }
    if (this.lineChartInstance) {
      this.lineChartInstance.dispose();
    }
  },
}
</script>

<style scoped>
.fixed-color-link {
  text-decoration: none;
  color: #000000;
  /* 替换为你想要的颜色 */
}

.welcomeword {
  margin-right: 15px;
  font-size: 15px !important;
  /* 强制指定字体大小 */
}

/* 覆盖访问过的链接颜色 */
.fixed-color-link:visited {
  color: #000000;
  /* 保持与未访问时相同的颜色 */
}

.chart-container {
  display: flex;
  justify-content: space-between;
  width: 100%;
  gap: 20px;
  /* 添加间距 */
}

/* 统计卡片容器 - 横向排列 */
.statistic-container {
  display: flex;          /* 启用 Flex 布局 */
  justify-content: space-between; /* 两个卡片左右分布 */
  align-items: center;    /* 垂直居中（可选） */
  margin-bottom: 8vh;
  width: 100%;
  gap: 20px;             /* 卡片间距 */
}

/* 整体样式 */
.statistic-box {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 16px;
  border-radius: 4px;
  background: #fff;
  /* box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); */
  text-align: center;
  /* 可以改成 left / right */
  font-family: 'Helvetica Neue', Arial, sans-serif;
  width:45%;
}

/* 标题（可选） */
.statistic-title {
  font-size: 20px;
  color: #2a2a2a;
  /* Element UI 的次要文字颜色 */
  margin-bottom: 8px;
}

/* 数值（核心样式） */
.statistic-value {
  font-size: 30px;
  font-weight: bold;
  color: #000000;
  /* Element UI 主色，可改成 #67C23A（绿色） */
  line-height: 1.2;
}
</style>