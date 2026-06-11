<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'
import NavMenu from '../../component/NavMenu.vue'
import BottomLine from '@/views/component/BottomLine.vue';
import gsap from 'gsap'
import ShowCard from '@/views/component/ShowCard.vue';
import MovingPage from '@/views/component/MovingPage.vue';
import ChangePic from '@/views/component/ChangePic.vue';
import GLBViewer from '@/views/component/GLBViewer.vue';
const hasAnimated1 = ref(false);
const hasAnimated2 = ref(false);
const hasAnimated3 = ref(false);
const hasAnimated4 = ref(false);
const scrollEnabled = ref(true);

onMounted(() => {
    gsap.fromTo('.turbine-title span',
        { opacity: 0, visibility: 'hidden', y: 50 },
        { opacity: 1, visibility: 'visible', y: 0, duration: 1, ease: 'ease-out' }
    );
    
    const observer1 = new IntersectionObserver(
        (entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
                    hasAnimated1.value = true;
                    gsap.fromTo('.section2',
                        { opacity: 0, visibility: 'hidden', y: 100 },
                        { opacity: 1, visibility: 'visible', y: 0, duration: 3, ease: 'ease-out' }
                    );
                    gsap.fromTo('.section2 .Left-image',
                        { scale: 0.5, opacity: 0, rotation: -100, visibility: 'hidden', x: -100, y: 200 },
                        { scale: 1, opacity: 1, rotation: 0, visibility: 'visible', x: 0, y: 0, duration: 2, ease: 'ease-out' }
                    );
                }
            });
        },
        { threshold: 0.1 }
    );

    const appElement1 = document.querySelector('.section2');
    if (appElement1) {
        observer1.observe(appElement1);
    }

    const observer2 = new IntersectionObserver(
        (entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
                    hasAnimated2.value = true;
                    gsap.fromTo('.section3',
                        { opacity: 0, visibility: 'hidden', y: 200 },
                        { opacity: 1, visibility: 'visible', y: 0, duration: 3, ease: 'ease-out' }
                    );
                    gsap.fromTo('.section3 .Right-image',
                        { scale: 0.5, opacity: 0, visibility: 'hidden', x: 50, y: 100 },
                        { scale: 1, opacity: 1, visibility: 'visible', x: 0, y: 0, duration: 2, ease: 'ease-out' }
                    );
                }
            });
        },
        { threshold: 0.1 }
    );

    const appElement2 = document.querySelector('.section3');
    if (appElement2) {
        observer2.observe(appElement2);
    }

    const observer3 = new IntersectionObserver(
        (entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
                    hasAnimated3.value = true;
                    gsap.fromTo('.tur-container',
                        { opacity: 0, visibility: 'hidden', y: 10 },
                        { opacity: 1, visibility: 'visible', y: 0, duration: 3, ease: 'ease-out' }
                    );
                }
            });
        },
        { threshold: 0.3 }
    );

    const appElement3 = document.querySelector('.tur-container');
    if (appElement3) {
        observer3.observe(appElement3);
    }

    const observer4 = new IntersectionObserver(
        (entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
                    hasAnimated4.value = true;
                    gsap.fromTo('.textReason-container',
                        { opacity: 0, visibility: 'hidden', y: 100, ease: 'ease-out' },
                        { opacity: 1, visibility: 'visible', y: 0, duration: 2, ease: 'ease-out' }
                    );
                }
            });
        },
        { threshold: 0.1 }
    );

    const appElement4 = document.querySelector('.textReason-container');
    if (appElement4) {
        observer4.observe(appElement4);
    }
    
    window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
    window.removeEventListener("scroll", handleScroll);
});

const handleScroll = () => {
    const currentScroll = window.scrollY;
    // Only disable scroll if we're in the specific range and scroll is not already enabled
    if (currentScroll >= 2800 && currentScroll <= 3000 && scrollEnabled.value) {
        scrollEnabled.value = false;
    } else if ((currentScroll < 2800 || currentScroll > 3000) && !scrollEnabled.value) {
        scrollEnabled.value = true;
    }
};

const scroll = () => {
    scrollEnabled.value = true;
};

const scrollContainer = ref<HTMLDivElement | null>(null);

const handleWheel = (event: WheelEvent) => {
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
    <NavMenu />
    <div class="turbine-title">
        <span>
            燃气轮机介绍
        </span>
    </div>
    <div>
        <div class="section-introduction">
            <span>什么是燃气轮机？</span>
            <p style="text-indent: 2em;">
                燃气轮机（GasTurbine）是一种通过燃烧化石燃料（如天然气或柴油）产生动力的机械设备。它利用高温高压的燃气推动涡轮旋转，从而产生机械能，进而驱动发电机或其他机械设备。燃气轮机广泛应用于航空、发电、工业、船舶等领域，具有高效、轻便等优点。
            </p>
        </div>
    </div>
    <div class="section2">
        <div class="Left-bgc">
            <div class="Left-container">
                <div class="Left-image">
                    <img src='https://vue-project-image.oss-cn-shanghai.aliyuncs.com/turbine4.webp' alt="Image" />
                </div>
                <div style="flex:0.5;"></div>
                <div class="Left-text">
                    <span>工作原理</span>
                    <p>燃气轮机的工作原理基于热力学循环。空气首先进入压缩机，被压缩后通过燃烧室与燃料混合燃烧，产生高温高压气体。这些气体经过涡轮，涡轮推动压缩机和发电机运转，最后通过排气系统排出废气。</p>
                </div>
            </div>
        </div>
    </div>
    <div class="section3">
        <div class="Right-bgc">
            <div class="Right-container">
                <div class="Right-text">
                    <span>燃气轮机的应用</span>
                    <p>燃气轮机广泛应用于航空发动机、发电厂、海洋船舶以及工业制造领域。它们的高效性使得其在能源转换和高动力要求的场景中得到了极大的应用。</p>
                </div>
                <div style="flex:1;"></div>
                <div class="Right-image">
                    <img src='https://vue-project-image.oss-cn-shanghai.aliyuncs.com/turbine6.webp' alt="Image" />
                </div>
            </div>
        </div>
    </div>
    <div class="case" @wheel="handleWheel" ref="scrollContainer">
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
    <el-backtop :right="100" :bottom="100" />
    <div class="bottom-line-turbine">
        <BottomLine />
    </div>
</template>

<style scoped>
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
    flex: 4;
}

.Left-image img {
    width: 100%;
    height: auto;
}

.Left-text {
    padding: 20px;
    /* margin-left:100psx; */
    display: flex;
    flex: 5;
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
    font-size: 1.2em;
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
    flex: 4;
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
    font-size: 1.2em;
    color: rgb(118, 118, 118);
    line-height: 1.6;
}

.section2 {
    /* opacity: 0; */
    /* visibility: hidden; */
    overflow: hidden;
}

.section3 {
    /* opacity: 0; */
    /* visibility: hidden; */
    overflow: hidden;
}

.turbine-title {
    background-image: url('https://vue-project-image.oss-cn-shanghai.aliyuncs.com/turbine5.webp');
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

.turbine-title span {
    color: rgb(255, 255, 255);
    font-size: 50px;
    font-weight: bold;
    font-family: Noto Sans;
    display: flex;
    justify-content: center;
    position: absolute;
    /* 使用绝对定位 */
    top: 35%;
    /* 距离上方100px */
    left: 13%;
}

.section-introduction {
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;
    background-color: #f4f6f7;
    border-radius: 8px;
    padding: 100px 250px;
    /* box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); */
    /* margin-bottom: 30px; */
}

.section-introduction span {
    font-size: 2.5em;
    font-weight: bold;
    margin-bottom: 30px;
}

.section-introduction p {
    color: #3b3b3b;
    font-size: 20px;
    margin-bottom: 30px;
}

.case{
    margin-top:200px;
}

.textReason-container {
    align-items: center;
    justify-content: center;
    text-align: center;
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