<template>
    <div ref="cardRef" class="show-card" @mouseenter="onMouseEnter" @mouseleave="onMouseLeave">
        <div class="show-card-header">
            {{ props.title }}
        </div>
        <div class="show-card-content">
            {{ props.content }}
        </div>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import gsap from 'gsap';

// 定义 props
const props = defineProps<{
    title: string;
    content: string;
    card_height: number;
    card_up: number;
}>();

// 绑定到整个卡片容器
const cardRef = ref<HTMLElement | null>(null);

onMounted(() => {
    if (cardRef.value) {
        gsap.set(cardRef.value, {
            x:0,
            y: 0,
        });
        // 初始化内容区域的高度和透明度
        gsap.set(cardRef.value.querySelector('.show-card-content'), {
            opacity: 0,
            y: 0,
            height: 10,
            ease: 'circ.inOut',
        });
    }
});

// 定义 hover 状态
const isHovered = ref(false);
const isAnimating = ref(false);

const onMouseEnter = () => {
    if (cardRef.value && !isAnimating.value) {
        isHovered.value = true;
        isAnimating.value = true;
        gsap.to(cardRef.value, {
            y: -props.card_up,
            x:-20,
            width:200,
            duration:0.8,
        }
        );
        // 使用 GSAP 时间轴控制动画顺序
        const tl = gsap.timeline({
            onComplete: () => {
                isAnimating.value = false;
                if (isHovered.value===false){
                    onMouseLeave();
                }
            }
        });
        tl.to(cardRef.value.querySelector('.show-card-content'), {
            opacity: 1,
            y: 0,
            duration: 0.5,
            ease: 'circ.inOut',
        }).to(
            cardRef.value.querySelector('.show-card-content'),
            {
                height: props.card_height,
                duration: 0.5,
                ease: 'circ.inOut',
            },
            '-=0.5' // 与前一个动画重叠 0.5 秒
        );
    }
};

const onMouseLeave = () => {
    if (cardRef.value && !isAnimating.value) {
        isHovered.value = false;
        isAnimating.value = true;
        gsap.to(cardRef.value, {
            x:0,
            y: 0,
            width:160,
            duration: 0.5
        }
        );
        // 使用 GSAP 时间轴控制动画顺序
        const tl = gsap.timeline({
            onComplete: () => {
                isAnimating.value = false;
            }
        });
        tl.to(cardRef.value.querySelector('.show-card-content'), {
            opacity: 0,
            y: 0,
            duration: 0.5,
            ease: 'circ.inOut',
        }).to(
            cardRef.value.querySelector('.show-card-content'),
            {
                height: 10,
                duration: 0.5,
                ease: 'circ.inOut',
            },
            '-=0.5' // 与前一个动画重叠 0.5 秒
        );
    }
};
</script>

<style scoped>
.show-card {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    border: 2px solid #efefef;
    border-radius: 4px;
    padding: 16px;
    width: 160px;
    cursor: pointer;
    box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.1);
    background-color: white;
}

.show-card-header {
    font-size: 20px;
    font-weight: bold;
    font-family: Noto Sans, sans-serif;
}

.show-card-content {
    margin-top: 10px;
    font-size: 14px;
    color: #545454;
    font-family: Noto Sans, sans-serif;
    overflow: hidden;
    /* 确保内容不会溢出 */
}
</style>