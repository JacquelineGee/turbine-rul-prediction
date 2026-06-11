<template>
    <div>
        <!-- 插槽：用于传入需要导出的内容 -->
        <div ref="contentToExport">
            <slot></slot>
        </div>

        <!-- 导出按钮 -->
        <div class="PDF-button">
            <el-button @click="exportToPDF" type="primary" class="export-button">
                {{ buttonText }}
            </el-button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import html2pdf from 'html2pdf.js';
import axios from '@/utils/axios';

onMounted(() => {
    console.log('DOM 已渲染', contentToExport.value);
});

// 定义组件的 Props
const props = defineProps({
    buttonText: {
        type: String,
        default: '导出为 PDF', // 默认按钮文本
    },
    filename: {
        type: String,
        default: 'exported-content.pdf', // 默认文件名
    },
    userId: {
        type: String, // 接收 userId
        required: true, // 设置为必传
    },
    margin: {
        type: Number,
        default: 10, // 默认边距
    },
    scale: {
        type: Number,
        default: 2, // 默认缩放比例
    },
    hideClasses: {
        type: Array as () => string[], // 需要隐藏的类名数组
        default: () => [],
    },
    showClasses: {
        type: Array as () => string[], // 需要显示的类名数组
        default: () => [],
    },
});

// 获取插槽中的 DOM 元素
const contentToExport = ref<HTMLElement | null>(null);

// 导出为 PDF 的函数
const exportToPDF = async () => {
    if (contentToExport.value) {
        const element = contentToExport.value;

        // 动态添加隐藏类
        props.hideClasses.forEach((className) => {
            const elements = element.getElementsByClassName(className);
            Array.from(elements).forEach((el) => {
                el.classList.add('no-print');
                (el as HTMLElement).style.display = 'none'; // 手动隐藏元素
            });
        });

        props.showClasses.forEach((className) => {
            const elements = element.getElementsByClassName(className);
            Array.from(elements).forEach((el) => {
                el.classList.add('show-print');
                (el as HTMLElement).style.display = 'flex'; // 手动显示元素
            });
        });

        // 生成当前时间的年月日
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0'); // 月份从0开始，所以要加1
        const day = String(now.getDate()).padStart(2, '0');
        const formattedDate = `${year}${month}${day}`;

        // 在文件名最后加上当前时间的年月日
        const filenameWithDate = `${props.filename.replace('.pdf', '')}-${formattedDate}.pdf`;

        // 强制触发 DOM 更新
        setTimeout(async () => {
            const opt = {
                margin: props.margin, // 边距
                filename: filenameWithDate, // 文件名
                image: { type: 'jpeg', quality: 1.5 }, // 图片质量
                html2canvas: {
                    scale: props.scale, useCORS: true, // 允许跨域图片
                    logging: true
                }, // 缩放比例
                jsPDF: { unit: 'mm', format: 'a4', orientation: 'landscape' }, // PDF 格式
            };

            try {
                // 使用 html2pdf 导出并保存到本地
                await html2pdf().from(element).set(opt).save();

                // 生成 PDF 文件并上传到后端
                const pdf = await html2pdf().from(element).set(opt).outputPdf('blob');

                // 创建 FormData 对象并添加 PDF 文件
                const formData = new FormData();
                formData.append('file', pdf, filenameWithDate);
                formData.append('userId', props.userId); // 添加 userId
                formData.append('filename', props.filename);

                // 上传 PDF 文件到后端
                const response = await axios.post('http://localhost:8080/upload-pdf', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });

                console.log('文件上传成功:', response.data);

            } catch (error) {
                console.error('导出或上传失败:', error);
            } finally {
                // 导出完成后移除隐藏类
                props.hideClasses.forEach((className) => {
                    const elements = element.getElementsByClassName(className);
                    Array.from(elements).forEach((el) => {
                        el.classList.remove('no-print');
                        (el as HTMLElement).style.display = ''; // 恢复显示
                        console.log(`已移除 no-print 类从元素:`, el);
                    });
                });
                props.showClasses.forEach((className) => {
                    const elements = element.getElementsByClassName(className);
                    Array.from(elements).forEach((el) => {
                        el.classList.remove('show-print');
                        (el as HTMLElement).style.display = 'none'; // 恢复隐藏
                        console.log(`已移除 show-print 类从元素:`, el);
                    });
                });
            }
        }, 0); // 延迟 100ms 确保 DOM 更新
    } else {
        console.error('未找到要导出的内容');
    }
};
</script>

<style scoped>
.PDF-button {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
}

.page-break {
    page-break-before: always;
    /* 在元素前插入分页符 */
}
</style>