<script lang="ts" setup>
import NavMenu from '@/views/component/NavMenu.vue';
import BottomLine from '@/views/component/BottomLine.vue';
import gsap from 'gsap'
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const hasAnimated1 = ref(false);
const hasAnimated2 = ref(false);
const hasAnimated3 = ref(false);

onMounted(() => {
    gsap.fromTo('.service-title span',
        { opacity: 0, visibility: 'hidden', y: 100 },
        { opacity: 1, visibility: 'visible', y: 0, duration: 1, ease: 'ease-out' }
    );
    const route = useRoute();

    // 获取路由中的 hash 部分
    const hash = ref<string>(route.hash);
    if (hash.value) {
    console.log(hash.value);
        const element = document.querySelector(hash.value);
        if (element) {
            element.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    }
    const observer1 = new IntersectionObserver(
        (entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
                    hasAnimated1.value = true; // 标记动画已触发
                    // // 触发动画：产品列表（从透明到不透明）
                    gsap.fromTo('.section1 .Left-text',
                        { opacity: 0, visibility: 'hidden', y: 100 },
                        { opacity: 1, visibility: 'visible', y: 0, duration: 3, ease: 'ease-out' }
                    );
                    // if (!hasAnimated1.value) {
                    gsap.fromTo('.section1 .Left-image',
                        { scale: 0.5, opacity: 0, rotation: 100, visibility: 'hidden', x: -300, y: 200 },
                        { scale: 1, opacity: 1, rotation: 0, visibility: 'visible', x: 0, y: 0, duration: 2, ease: 'ease-out' }
                    );
                    // }
                    // observer1.disconnect();
                }
            });
        },
        { threshold: 0.1 } // 50% 可见时触发
    );
    const appElement1 = document.querySelector('.section1');
    if (appElement1) {
        observer1.observe(appElement1);
    }

    const observer2 = new IntersectionObserver(
        (entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
                    hasAnimated2.value = true; // 标记动画已触发
                    // // 触发动画：产品列表（从透明到不透明）
                    gsap.fromTo('.section2 .Right-text',
                        { opacity: 0, visibility: 'hidden', y: 200 },
                        { opacity: 1, visibility: 'visible', y: 0, duration: 3, ease: 'ease-out' }
                    );
                    gsap.fromTo('.section2 .Right-image',
                        { scale: 0.5, opacity: 0, visibility: 'hidden', x: 100, y: 0 },
                        { scale: 1, opacity: 1, visibility: 'visible', x: 0, y: 0, duration: 2, ease: 'ease-out' }
                    );
                    // observer2.disconnect();
                }
            });
        },
        { threshold: 0 } // 50% 可见时触发
    );

    // 开始观察 .app 元素
    const appElement2 = document.querySelector('.section2');
    if (appElement2) {
        observer2.observe(appElement2);
    }

    const observer3 = new IntersectionObserver(
        (entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
                    hasAnimated3.value = true; // 标记动画已触发
                    // // 触发动画：产品列表（从透明到不透明）
                    gsap.fromTo('.section3 .Left-text',
                        { opacity: 0, visibility: 'hidden', y: 100 },
                        {
                            opacity: 1, visibility: 'visible', y: 0, duration: 3, ease: 'ease-out'
                        }

                    );
                    // if (!hasAnimated1.value) {
                    gsap.fromTo('.section3 .Left-image',
                        { scale: 0.1, opacity: 0, visibility: 'hidden', x: 0, y: 0 },
                        { scale: 1, opacity: 1, rotation: 0, visibility: 'visible', x: 0, y: 0, duration: 2, ease: 'ease-out' }
                    );
                    // }
                    // observer1.disconnect();
                }
            });
        },
        { threshold: 0.1 } // 50% 可见时触发
    );
    const appElement3 = document.querySelector('.section3');
    if (appElement3) {
        observer3.observe(appElement3);
    }
});

</script>

<template>
    <NavMenu />
    <div class="service-title">
        <span>产品与服务</span>
    </div>

    <!-- 剩余寿命预测与健康监测 -->
    <div id="rul-prediction" class="section1">
        <div class="Left-bgc">
            <div class="Left-container">
                <div class="Left-image">
                    <img src="https://vue-project-image.oss-cn-shanghai.aliyuncs.com/predict.jpg" alt="Image" />
                </div>
                <div style="flex: 0.5;"></div>
                <div class="Left-text">
                    <span>剩余寿命预测与健康监测</span>
                    <p>通过实时采集燃气轮机的运行数据，如温度、压力、振动等关键参数，结合先进的数据分析与机器学习技术，精准预测设备的剩余使用寿命。系统能提前发现潜在故障，提供维护建议，帮助企业优化运维策略，减少停机时间，降低维护成本。通过这一智能化监测平台，用户能够有效延长设备使用寿命，提高能源利用效率，保障生产安全和设备稳定运行。
                    </p>
                </div>
            </div>
        </div>
    </div>

    <!-- 智能数据分析与可视化 -->
    <div id="data-analysis" class="section2">
        <div class="Right-bgc">
            <div class="Right-container">
                <div class="Right-text">
                    <span>智能数据分析与可视化</span>
                    <p>利用大数据和机器学习技术对燃气轮机的实时运行数据进行深度分析，准确预测设备的剩余寿命。系统通过对温度、压力、振动等关键指标的监控，生成精确的健康评估报告，并通过直观的可视化界面展示预测结果。用户可实时查看设备状态、故障预警与维护建议，从而优化运维计划，提前采取预防措施，减少突发故障，延长设备生命周期，提高整体运营效率。
                    </p>
                </div>
                <div style="flex: 1;"></div>
                <div class="Right-image">
                    <img src="../../assets/images/analyze2.gif" alt="Image" />
                </div>
            </div>
        </div>
    </div>

    <!-- 预警与维护决策支持 -->
    <div id="maintenance-support" class="section3">
        <div class="Left-bgc">
            <div class="Left-container">
                <div class="Left-image">
                    <img src="https://vue-project-image.oss-cn-shanghai.aliyuncs.com/warning_1.jpg" alt="Image" />
                </div>
                <div style="flex: 0.5;"></div>
                <div class="Left-text">
                    <span>预警与维护决策支持</span>
                    <p>通过实时监控设备运行状态，分析温度、压力、振动等关键数据，精准预测燃气轮机的剩余使用寿命。系统能够在潜在故障发生前提前发出预警，帮助用户及时制定维护计划，优化维修资源配置。借助智能算法和数据驱动的决策支持，企业可以减少突发性停机、降低维护成本，并提高设备的可靠性和运行效率，确保生产的连续性和安全性。
                    </p>
                </div>
            </div>
        </div>
    </div>

    <BottomLine />
</template>

<style scoped>
.service-title {
    background-image: url('https://vue-project-image.oss-cn-shanghai.aliyuncs.com/servicetitle.jpeg');
    height: 400px;
    /* background-color: black; */
    background-size: cover;
    /* 背景图片覆盖整个容器 */
    background-position: center;
    /* 确保图片居中 */
    background-repeat: no-repeat;
    /* 防止图片重复 */
    position: relative;
}

.service-title span {
    color: rgb(255, 255, 255);
    font-size: 55px;
    font-weight: bold;
    font-family: Noto Sans;
    display: flex;
    justify-content: center;
    position: absolute;
    /* 使用绝对定位 */
    top: 35%;
    /* 距离上方100px */
    left: 39%;
}

.Left-bgc {
    width: 100%;
    height: 500px;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: nowrap;
    /* 确保内容不换行 */
    margin: 0 auto;
    background-color: white;
}

.Left-container {
    display: flex;
    width: 1200px;
    justify-content: center;
    align-items: center;
    flex-wrap: nowrap;
    /* 确保内容不换行 */
    margin: 0 auto;
}

.Left-image {
    padding: 10px;
    padding-left: 50px;
    flex: 3;
}

.Left-image img {
    width: 100%;
    height: auto;
}

.Left-text {
    padding: 10px;
    /* margin-left:100psx; */
    display: flex;
    flex: 6;
    flex-direction: column;
    justify-content: center;
    /* align-items: center; */
}

.Left-text span {
    font-size: 2.5em;
    font-weight: bold;
    margin-bottom: 10px;
}

.Left-text p {
    font-family: 方正汉真广标;
    font-size: 1.15em;
    color: rgb(118, 118, 118);
    line-height: 1.6;
    padding-right: 70px;
}

.Right-bgc {
    width: 100%;
    height: 500px;
    display: flex;
    background-color: #f4f6f7;
    justify-content: center;
    align-items: center;
    flex-wrap: nowrap;
    /* 确保内容不换行 */
    margin: 0 auto;
}

.Right-container {
    display: flex;
    width: 1200px;
    justify-content: center;
    align-items: center;
    flex-wrap: nowrap;
    /* 确保内容不换行 */
    margin: 0 auto;
}


.Right-image {
    padding: 10px;
    padding-right: 50px;
    flex: 4;
}

.Right-image img {
    width: 100%;
    height: auto;
}

.Right-text {
    padding: 20px;
    padding-left: 50px;
    /* margin-left:100psx; */
    display: flex;
    flex: 5;
    flex-direction: column;
    justify-content: center;
    /* align-items: center; */
}

.Right-text span {
    font-size: 2.5em;
    font-weight: bold;
    margin-bottom: 10px;
}

.Right-text p {
    font-family: 方正汉真广标;
    font-size: 1.15em;
    color: rgb(118, 118, 118);
    line-height: 1.6;
}
</style>