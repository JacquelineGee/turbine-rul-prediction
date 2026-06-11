<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'
import NavMenu from '../../component/NavMenu.vue'
import BottomLine from '@/views/component/BottomLine.vue';
import gsap from 'gsap'
import ChangePic from '@/views/component/ChangePic.vue';
import MovingPage from '@/views/component/MovingPage.vue';


const hasAnimated = ref(false);
// 页面加载时应用动画
const hasAnimated2 = ref(false);
const scrollEnabled = ref(true);
onMounted(() => {
    gsap.fromTo('.title span',
        { opacity: 0, visibility: 'hidden', y: 50 },
        { opacity: 1, visibility: 'visible', y: 0, duration: 1, ease: 'ease-out' }
    );

    gsap.fromTo('.title p',
        { opacity: 0, visibility: 'hidden', y: 50 },
        { opacity: 1, visibility: 'visible', y: 0, duration: 1, ease: 'ease-out' }
    );
    // 使用 IntersectionObserver 监听视口的进入
    const observer = new IntersectionObserver(
        (entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
                    hasAnimated.value = true; // 标记动画已触发

                    gsap.fromTo('.textReason-container',
                        { opacity: 0, visibility: 'hidden', y: 100, ease: 'ease-out' },
                        { opacity: 1, visibility: 'visible', y: 0, duration: 2, ease: 'ease-out' }
                    );
                    // 停止观察
                    // observer.disconnect();
                }
            });
        },
        { threshold: 0.1 } // 50% 可见时触发
    );

    // 开始观察 .app 元素
    const appElement = document.querySelector('.textReason-container');
    if (appElement) {
        observer.observe(appElement);
    }
    window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
    window.removeEventListener("scroll", handleScroll);
});

const handleScroll = () => {
    const currentScroll = window.scrollY;
    if (currentScroll >= 495 && currentScroll <=550) {
        scrollEnabled.value=false;
        window.scrollTo(0, currentScroll);
    }
    else scrollEnabled.value=true;
    console.log('Current Scroll:', currentScroll);
};

const scroll = () => {
    console.log("achieve");
    scrollEnabled.value = true;
};

const scrollContainer = ref<HTMLDivElement | null>(null);

const handleWheel = (event: WheelEvent) => {
    // 如果正在执行动画或者滚动被禁用，不允许滚动
    if (!scrollEnabled.value) {
        event.preventDefault();
        return;
    }
};

const contents = ref(
    [{
        image: 'https://vue-project-image.oss-cn-shanghai.aliyuncs.com/pic1.webp',
        title: '确保设备安全运行',
        text: '通过精准的监测与预警系统，确保设备在稳定的状态下运行，减少故障发生，提升安全性。',
    },
    {
        image: 'https://vue-project-image.oss-cn-shanghai.aliyuncs.com/cost.jpg',
        title: '降低维护成本',
        text: '利用数据分析和预测技术，提前发现潜在问题，优化维护周期，降低维修和更换成本。',
    }, {
        image: 'https://vue-project-image.oss-cn-shanghai.aliyuncs.com/benefit2.webp',
        title: '提高经济效益',
        text: '通过提高设备的运行效率和延长使用寿命，最大化资源利用，提升整体经济效益。',
    },
    {
        image: 'https://vue-project-image.oss-cn-shanghai.aliyuncs.com/design.jpg',
        title: "优化设计",
        text: '基于实时数据反馈和模拟分析，不断优化设备设计，提高性能和可靠性。'
    }
    ]
)
</script>

<template>
    <div class="container" @wheel="handleWheel" ref="scrollContainer">
        <NavMenu />
        <div class="title">
            <span>
                预测目标
            </span>
            <p>优化燃气轮机寿命预测，推动能源行业的高效与稳定发展</p>
        </div>
        <div class="case">
            <MovingPage @scroll="scroll" />
        </div>
        <div class="textReason-container">
            <div class="textReason">
                <span>
                    四大目标
                </span>
            </div>
            <ChangePic class="changePic" :contents="contents" />
        </div>
        
        <BottomLine />
    </div>
</template>

<style scoped>
.container {
    /* display: flex; */
    /* flex-direction: column; */
    align-items: center;
    /* 水平居中 */
    justify-content: center;
    /* 垂直居中 */
    /* min-height: 100vh;  使整个页面高度至少为视口高度 */
    text-align: center;
    /* 使文本在h1中居中 */
    /* overflow:auto; */
}

.case {
    margin-top: 50px;
}

.title {
    background-image: url('https://vue-project-image.oss-cn-shanghai.aliyuncs.com/blade4.jpg');
    height: 400px;
    background-size: cover;
    /* 背景图片覆盖整个容器 */
    background-position: center;
    /* 确保图片居中 */
    background-repeat: no-repeat;
    /* 防止图片重复 */
    position: relative;
}

.title span {
    color: rgb(255, 255, 255);
    font-size: 50px;
    font-weight: bold;
    font-family: Noto Sans;
    display: flex;
    justify-content: center;
    position: absolute;
    /* 使用绝对定位 */
    top: 15%;
    /* 距离上方100px */
    left: 20%;
}

.title p {
    color: white;
    font-size: 25px;
    font-family: Noto Sans;
    display: flex;
    justify-content: center;
    position: absolute;
    /* 使用绝对定位 */
    top: 36%;
    /* 距离上方100px */
    left: 3%;
    line-height: 50px;
}

.case-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 400px;
    /* opacity: 0;
    visibility: hidden;
    overflow: hidden; */
}

.case-text span {
    font-size: 3em;
    font-weight: bold;
    color: rgb(130, 0, 0);
}

.textReason-container {
    opacity: 0;
    visibility: hidden;
    overflow: hidden;
}

.textReason {
    margin-top: 100px;
    font-size: 3em;
    font-weight: bold;
}
</style>