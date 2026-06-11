<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { gsap } from 'gsap';
import { defineEmits } from 'vue';

// 当前页面控制
const currentPage = ref<number>(0);
const Moving_page = ref<number>(1);
const Moving_space = ref<number>(-100);

// 页面内容的引用
const page1 = ref<HTMLElement | null>(null);
const page2 = ref<HTMLElement | null>(null);
const page3 = ref<HTMLElement | null>(null);

// 滚动计数器
// const scrollCount = ref<number>(0);
// const isAnimating = ref<boolean>(false); // 用来标记是否正在执行动画

// 页面初始化时，执行动画使第一个页面渐显
onMounted(() => {
    if (page1.value) {
        gsap.fromTo(
            page1.value,
            { opacity: 0, x: '-100%' }, // 页面初始位置在左边界外，透明度为0
            { opacity: 1, x: '0%', duration: 1, ease: "ease-out" }
        )
    }
});

const scrollOffset = ref(0);
const isAnimating = ref(false);

const emit = defineEmits<{
    (event: 'scroll'): void;
}>();

const changeScrollMessage = () => {
    emit('scroll');  // 通过事件传递数据给父组件
};

const handleWheel = (event: WheelEvent) => {
    // 如果正在执行动画，不允许滚动
    if (isAnimating.value) {
        event.preventDefault();
        return;
    }
    const delta = event.deltaY;  // 获取滚动的增量
    // 根据滚动增量来调整页面偏移
    scrollOffset.value += delta;
    // 控制页面平移的范围
    const maxScroll = 1000; // 假设最大平移为1000px
    const minScroll = -1000; // 最小平移为-1000px
    // 限制滚动范围
    if (scrollOffset.value > maxScroll) scrollOffset.value = maxScroll;
    if (scrollOffset.value < minScroll) scrollOffset.value = minScroll;
    // 平移页面
    if (scrollOffset.value > 0 && Moving_page.value <= 2)
        pageTranslateAdd(scrollOffset.value);
    if (scrollOffset.value < 0 && Moving_page.value > 0)
        pageTranslateMinus(scrollOffset.value);
};

const pageTranslateAdd = (offset: number) => {
    // console.log(offset);
    const pages = [page1.value, page2.value, page3.value];
    if (pages[0] && pages[1] && pages[2]) {
        isAnimating.value = true;
        const targetX = Math.min(Moving_space.value + offset * 0.5, 0);
        // console.log("x:",targetX,' ',-100 + offset * 0.2);
        gsap.to(pages[Moving_page.value], {
            x: targetX + '%',  // 按照滚动的比例来平移页面
            opacity: 1,  // 根据偏移调整透明度
            duration: 0.5,
            ease: "ease-out",
            onComplete: () => {
                isAnimating.value = false;
            }
        });
        // console.log(targetX);
        if (targetX >= 0) {
            Moving_space.value = -100;
            currentPage.value++;
            Moving_page.value++;
            if (Moving_page.value === 3) {
                Moving_page.value = 2;
                Moving_space.value = 0; setTimeout(() => {
                    changeScrollMessage();
                }, 500);
            }
            scrollOffset.value = 0;
        }
    }
};

const pageTranslateMinus = (offset: number) => {
    // console.log(offset);
    const pages = [page1.value, page2.value, page3.value];
    if (pages[0] && pages[1] && pages[2]) {
        isAnimating.value = true;
        const targetX = Math.max(Moving_space.value + offset * 0.5, -100);
        gsap.to(pages[Moving_page.value], {
            x: targetX + '%',  // 按照滚动的比例来平移页面
            opacity: 1,  // 根据偏移调整透明度
            duration: 0.5,
            ease: "ease-out",
            onComplete: () => {
                isAnimating.value = false;
            }
        });
        // console.log(targetX);
        if (targetX <= -100) {
            currentPage.value--;
            Moving_space.value = 0;
            Moving_page.value--;
            if (Moving_page.value === 0) {
                Moving_page.value = 1;
                Moving_space.value = -100;
                setTimeout(() => {
                    changeScrollMessage();
                }, 500);
            }
            scrollOffset.value = 0;
        }
    }
};

const page_1 = () => {
    gsap.to(page2.value, {
        x: '-100%',  // 按照滚动的比例来平移页面
        opacity: 0,  // 根据偏移调整透明度
        duration: 2,
        ease: "ease-out"
    });
    gsap.to(page3.value, {
        x: '-100%',  // 按照滚动的比例来平移页面
        opacity: 0,  // 根据偏移调整透明度
        duration: 2,
        ease: "ease-out"
    });
    gsap.to(page1.value, {
        x: '0%',  // 按照滚动的比例来平移页面
        opacity: 1,  // 根据偏移调整透明度
        duration: 2,
        ease: "ease-out"
    });
    currentPage.value = 0;
    Moving_page.value = 1;
    Moving_space.value = -100;
    scrollOffset.value = 0;
    setTimeout(() => {
        changeScrollMessage();
    }, 500);
}

const page_2 = () => {
    gsap.set(page1.value, {
        x: '0%',  // 按照滚动的比例来平移页面
        opacity: 1,  // 根据偏移调整透明度
        duration: 0,
        ease: "ease-out"
    });
    gsap.to(page3.value, {
        x: '-100%',  // 按照滚动的比例来平移页面
        opacity: 0,  // 根据偏移调整透明度
        duration: 2,
        ease: "ease-out"
    });
    gsap.to(page2.value, {
        x: '0%',  // 按照滚动的比例来平移页面
        opacity: 1,  // 根据偏移调整透明度
        duration: 2,
        ease: "ease-out"
    });
    Moving_space.value = 0;
    Moving_page.value = 1;
    currentPage.value = 1;
    scrollOffset.value = 0;
}
const page_3 = () => {
    gsap.set(page1.value, {
        x: '0%',  // 按照滚动的比例来平移页面
        opacity: 1,  // 根据偏移调整透明度
        duration: 2,
        ease: "ease-out"
    });
    gsap.set(page2.value, {
        x: '0%',  // 按照滚动的比例来平移页面
        opacity: 1,  // 根据偏移调整透明度
        duration: 2,
        ease: "ease-out"
    });
    gsap.to(page3.value, {
        x: '0%',  // 按照滚动的比例来平移页面
        opacity: 1,  // 根据偏移调整透明度
        duration: 2,
        ease: "ease-out"
    });
    Moving_page.value = 2;
    Moving_space.value = 0;
    currentPage.value = 2;
    scrollOffset.value = 0;
    setTimeout(() => {
        changeScrollMessage();
    }, 500);
}
</script>

<template>
    <div>
        <div class="container">
            <div class="button-container">
                <div>
                    <el-button key="plain" link @click="page_1"><span class="button-style">重要经典案例</span></el-button>
                </div>
                <div class="button-style">
                    <el-button key="plain" link @click="page_2"><span class="button-style">轴承断裂案例</span></el-button>
                </div>
                <div class="button-style">
                    <el-button key="plain" link @click="page_3"><span class="button-style">腐蚀及裂纹案例</span></el-button>
                </div>
            </div>
            <div class="page-wrapper" @wheel="handleWheel">
                <!-- 第一个页面内容 -->
                <div class="content" ref="page1">
                    <div class="case-text">
                        <span>
                            重大事故案例
                        </span>
                    </div>
                </div>

                <div class="content" id="content-page2" ref="page2">
                    <div class="case-1">
                        <span class="title-1">
                            燃气轮机轴承断裂案例：2018年中国中国某电厂事故
                        </span>
                        <p>
                            2018年，中国某大型火力发电厂的一台燃气轮机在运行过程中发生轴承断裂事故。
                            该燃气轮机用于发电，是电厂的核心设备之一。
                            燃气轮机在满负荷运行过程中，突然发出<strong>异常噪音和振动</strong>。
                            运行人员发现轴承温度急剧升高，随后机组自动保护系统触发紧急停机。
                            停机后检查发现，燃气轮机的高压涡轮端轴承完全断裂，导致转子严重偏移。
                        </p>
                        <br>
                        <p>
                            <strong style="color:black">后果：</strong>轴承断裂导致转子与静子部件发生碰撞，高压涡轮叶片严重损坏，机组核心部件需要更换。<br>
                            机组停机维修时间长达3个月，直接导致电厂发电量减少，经济损失<strong>超过5000万元人民币</strong>。<br>
                            电厂发电能力下降，局部电网供电紧张，影响了周边工业生产和居民用电。
                        </p>
                    </div>
                </div>

                <div class="content" ref="page3">
                    <!-- <img src="../../assets/images/腐蚀.jpg" alt="腐蚀"> -->
                    <div class="case-1">
                        <span class="title-1">
                            燃气轮机腐蚀及裂纹案例：中国某海上平台燃气轮机故障
                        </span>
                        <p>
                            2020年，中国某海上石油平台的燃气轮机发电机组在运行过程中发生严重腐蚀和裂纹问题。
                            该燃气轮机用于为平台提供电力，是海上作业的关键设备。
                            燃气轮机在运行过程中出现功率下降和异常振动。
                            运行人员检查发现，燃气轮机的压气机叶片和涡轮叶片表面存在严重腐蚀，部分叶片出现裂纹。
                            进一步检测发现，裂纹已扩展至叶片根部，存在<strong>断裂风险</strong>。
                        </p>
                        <br>
                        <p>
                            <strong style="color:black">后果：</strong>
                            压气机和涡轮叶片因腐蚀和裂纹需要全部更换，维修费用高达2000万元人民币。燃气轮机停机维修导致平台电力供应中断，海上作业暂停，直接经济损失<strong>超过1亿元人民币。</strong><br>
                            叶片裂纹扩展可能导致叶片断裂，引发燃气轮机严重损坏甚至爆炸，威胁<strong>平台工作人员安全</strong>。<br>
                            燃气轮机故障导致平台燃烧效率下降，未完全燃烧的燃料增加了废气排放，对环境造成污染。
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.button-container {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 100px;
    margin-bottom: 20px;
}

.button-style {
    font-size: 1.3em;
}

.container {
    position: relative;
    width: 100%;
    height: 80vh;
    overflow: hidden;
    /* 避免页面溢出 */
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
}

.case-text {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 60%;
}

.case-text span {
    font-size: 3em;
    font-weight: bold;
    color: rgb(130, 0, 0);
}

.case-1 {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 50px;
    /* height: 60%; */
}

.case-1 .title-1 {
    font-size: 2em;
    font-weight: bold;
    color: rgb(4, 0, 115);
    margin-bottom: 10px;
}

.case-1 p {
    font-size: 1.2em;
    color: #555;
    padding: 0px 130px;
}

.page-wrapper {
    position: relative;
    width: 100%;
    height: 100%;
}

#content-page2 {
    background-color: #f4f6f7;
}

.content {
    position: absolute;
    width: 100%;
    height: 100%;
    padding: 20px 20px 20px 20px;
    background-color: #fff;
    box-sizing: border-box;
    opacity: 0;
    transform: translateX(-100%);
    /* 页面初始位置在左边界外 */
}
</style>
