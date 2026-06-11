<template>
  <div ref="container" class="three-container">
    <div class="button-style">
      <!-- 初始状态只显示“查看详细结构”按钮 -->
      <el-button v-if="!isDetailPage" type="primary" @click="goToDetailPage">
        查看详细结构
      </el-button>
      <!-- 在 /TurbinDetail 页面显示完整按钮 -->
      <div v-else>
        <el-button type="primary" @click="handleSplitTurbine($event)">
          查看详细结构
        </el-button>
        <el-button type="primary" @click="reset_turbine">
          恢复
        </el-button>
        <el-button type="primary" @click="goBack">
          返回上一个页面
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { onMounted, onUnmounted, ref } from 'vue';
import gsap from 'gsap';
import modelUrl from '../../assets/test3.gltf?url'; // 导入 GLTF 模型文件
import { CSS2DRenderer, CSS2DObject } from 'three/examples/jsm/renderers/CSS2DRenderer.js';
import { useRouter } from 'vue-router';

const router = useRouter();
const isDetailPage = ref(false); // 用于判断是否在 /TurbinDetail 页面

// 跳转到 /TurbinDetail 页面
const goToDetailPage = () => {
  isDetailPage.value = true;
  router.push('/TurbineDetail');
  controls.enabled = true; // 恢复鼠标监听
};

// 返回上一个页面
const goBack = () => {
  isDetailPage.value = false;
  router.go(-1);
  controls.enabled = false; // 禁用鼠标监听
};
const container = ref(null); // 用于引用 DOM 容器
let gear_h11, stator_comp_in1, compressor_blade1, compressor_blade_in1, compressor_shell1, stator_comp1, gear_h21;
let coplete1, comp_runner1, drive1, shaft1;

const hoverText = ref('');
let hoveredObject = null; // 当前悬停的对象
let camera, mixer;
let scene, renderer, labelRenderer, mouse; // 存储鼠标位置;
let controls;
let raycaster, clock;
let animationId;
const if_split = ref('false');
const isComponentSelected = ref('false');
const handleSplitTurbine = (event) => {
  event.stopPropagation(); // 阻止事件冒泡
  split_turbine(); // 调用原有的拆分逻辑
};
const animate = () => {
  animationId = requestAnimationFrame(animate); // 记录 ID
  if (mixer) mixer.update(clock.getDelta());
  controls?.update();
  renderer?.render(scene, camera);
  labelRenderer?.render(scene, camera);
};
// 创建 Three.js 场景
scene = new THREE.Scene();
const handleResize = () => {
  camera.aspect = window.innerWidth / window.innerHeight;
  camera.updateProjectionMatrix();
  renderer?.setSize(window.innerWidth, window.innerHeight);
};
const handleClick = (event) => {
  if (!isDetailPage.value) return; // 如果不是在 /TurbinDetail 页面，直接返回
  // 将鼠标位置归一化到 [-1, 1]
  mouse.x = (event.clientX / window.innerWidth) * 2 - 1;
  mouse.y = -(event.clientY / window.innerHeight) * 2 + 1;

  // 检测点击的对象
  raycaster.setFromCamera(mouse, camera);
  raycaster.far = 1000; // 增加光线投射的检测距离
  const intersects = raycaster.intersectObjects(scene.children, true); // 递归检测所有子对象

  // 调试信息：输出所有相交的对象
  console.log('Intersects:', intersects);

  if (if_split.value === true) {
    if (intersects.length > 0) {
      const object = intersects[0].object;
      console.log('Clicked object:', object.name);

      // 如果点击的对象是组件
      if (object.userData.defaultX !== undefined) {
        if (isComponentSelected.value === true && hoveredObject !== object) {
          return;
        }

        // 将相机移动到组件中心
        const box = new THREE.Box3().setFromObject(object);
        const center = new THREE.Vector3();
        box.getCenter(center);

        const distance = 5; // 相机与组件的距离
        const direction = new THREE.Vector3().subVectors(camera.position, center).normalize();
        const newCameraPosition = new THREE.Vector3().addVectors(center, direction.multiplyScalar(distance));


        // 隐藏其他组件
        scene.traverse((child) => {
          if (child.userData.label2) {
            child.userData.label2.element.style.visibility = 'hidden';
          }
          if (child !== object && child.userData.defaultX !== undefined) {
            // 使用 GSAP 实现缓缓隐藏效果
            gsap.to(child.material, {
              opacity: 0,
              duration: 0,
              ease: 'power2.out',
              onComplete: () => {
                child.visible = false;
              }
            });
          }
        });
        // 使用 GSAP 平滑移动相机
        gsap.to(camera.position, {
          x: newCameraPosition.x,
          y: newCameraPosition.y,
          z: newCameraPosition.z,
          duration: 1,
          ease: 'power2.out',
          onUpdate: () => {
            controls.target.copy(center); // 更新控制器的目标点
            controls.update();
          },
        });

        // 展示组件文字介绍
        if (object.userData.description) {
          hoverText.value = object.userData.description;
        }

        if (object.userData.label) {
          object.userData.label.element.style.visibility = 'visible';
          hoverText.value = object.name; // 更新 hoverText
        }
        // 隐藏其他组件
        scene.traverse((child) => {
          if (child !== object && child.userData.defaultX !== undefined) {
            // 使用 GSAP 实现缓缓隐藏效果
            gsap.to(child.material, {
              opacity: 0,
              duration: 1,
              ease: 'power2.out',
              onComplete: () => {
                child.visible = false;
              }
            });
          }
        });

        // 更新当前选中的对象
        hoveredObject = object;
        isComponentSelected.value = true;
        return;
      }
    }
  }

  // 如果没有点击到任何组件，隐藏所有标签
  hoveredObject = null;
};

onMounted(() => {
  labelRenderer = new CSS2DRenderer();
  labelRenderer.setSize(window.innerWidth, window.innerHeight);
  labelRenderer.domElement.style.position = 'absolute';
  labelRenderer.domElement.style.top = '0';
  labelRenderer.domElement.style.pointerEvents = 'none'; // 防止标签阻挡点击事件
  document.body.appendChild(labelRenderer.domElement);

  // 设置透视相机
  camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
  camera.position.set(2.1988, 3.2648, -6.9349); // 设置相机初始位置

  // 创建 WebGL 渲染器
  renderer = new THREE.WebGLRenderer({ antialias: true });
  renderer.setSize(window.innerWidth, window.innerHeight); // 设置渲染器尺寸
  renderer.setClearColor(0xffffff); // 设置背景颜色
  container.value.appendChild(renderer.domElement); // 将渲染器的 DOM 元素添加到容器中

  // 添加轨道控制器
  controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true; // 启用阻尼效果，使控制更平滑
  controls.enabled = false; // 初始状态下禁用鼠标监听

  // 添加环境光
  const ambientLight = new THREE.AmbientLight(0x404040, 5);
  ambientLight.position.copy(camera.position);
  scene.add(ambientLight);

  // 添加平行光
  const directionalLight = new THREE.DirectionalLight(0xffffff, 5);
  directionalLight.position.copy(camera.position);
  scene.add(directionalLight);

  // 定义变量

  raycaster = new THREE.Raycaster(); // 光线投射器，用于检测鼠标交互
  mouse = new THREE.Vector2(); // 存储鼠标位置

  // 加载 GLTF 模型
  const loader = new GLTFLoader();
  loader.load(
    modelUrl, // 模型文件路径
    (gltf) => {
      const model = gltf.scene;
      scene.add(model); // 将模型添加到场景中

      model.traverse((child) => {
        if (child.isMesh) {
          // 创建标签
          const labelDiv = document.createElement('div');
          labelDiv.className = 'label1';

          // 根据子对象的名称或其他属性设置不同的文字说明
          let description = ''; // 默认文字说明
          let labelPosition = { x: 10, y: 0.5, z: 0 };
          switch (child.name) {
            case 'Turbine_stg-200_-_comp_runner-1':
              description = '<strong>名称:</strong> 涡轮叶片 (Turbine Blade)<br><strong>作用:</strong> 涡轮叶片用于接收来自燃烧室的高温高压燃气冲击，将热能和动能转换为旋转机械能，驱动压气机运转或输出动力，是能量转换的关键部件。<br><strong>常见故障：</strong>涡轮叶片在极端工况下长期运行，容易发生多种故障。常见的有热疲劳裂纹，由于高温周期性变化引起材料膨胀收缩，导致裂纹产生；机械疲劳断裂，是在高转速下长时间运行造成的金属疲劳断裂；腐蚀，包括高温氧化和硫腐蚀，会破坏叶片表面金属；熔蚀则是高温气流冲刷叶片表面，导致局部熔化或变形；异物击伤（FOD）多见于进气时吸入小石子、鸟类等，直接撞击叶片造成缺口或变形；还有表面涂层剥落，指保护叶片的耐热涂层在长时间高温下逐渐脱落，降低抗热抗腐蚀能力。这些故障若不及时发现，会严重影响发动机安全和性能。';
              labelPosition = { x: -90, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_Compressor_Blade_-_in-1':
              description = '<strong>名称:</strong> 压缩机叶片 (Compressor Blade)<br><strong>作用:</strong> 压缩机叶片安装在压缩机转子上，通过与空气的相互作用，将空气压缩并推动其向燃烧室方向流动。';
              labelPosition = { x: 100, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_Compressor_Blade-1':
              description = '<strong>名称:</strong> 压缩机叶片 (Compressor Blade)<br><strong>作用:</strong> 与上述压缩机叶片类似，负责在压缩机转子旋转时对空气进行压缩，确保空气压力的提升。';
              labelPosition = { x: 90, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_coplete-1':
              description = '<strong>名称:</strong> 低压涡轮叶片 (Low-pressure turbine blade)<br><strong>作用:</strong> 低压涡轮叶片用于接收燃气在高压涡轮做功后的剩余能量，继续膨胀气流，驱动风扇或低压压气机旋转，是提升整体效率和提供推力的重要部件。';
              labelPosition = { x: 80, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_Drive_Turbine-1':
              description = '<strong>名称:</strong> 驱动涡轮 (Drive Turbine)<br><strong>作用:</strong> 驱动涡轮是燃气轮机的动力输出部分，通过高温高压气体的膨胀驱动涡轮旋转，进而带动压缩机和其他外部负载。';
              labelPosition = { x: -90, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_shaft-1':
              description = '<strong>名称:</strong> 轴 (Shaft)<br><strong>作用:</strong> 轴是燃气轮机中连接压缩机、涡轮和其他旋转部件的关键部件，负责传递旋转动力，确保各个部件的同步运转。';
              break;
            case 'Turbine_stg-200_-_stator_comp_-_in-1':
              description = '<strong>名称:</strong> 压缩机静叶 (Stator Compressor Blade)<br><strong>作用:</strong> 压缩机静叶固定在压缩机壳体上，用于引导气流方向，确保空气在压缩机内的流动更加高效和稳定。';
              labelPosition = { x: 80, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_stator_comp-1':
              description = '<strong>名称:</strong> 压缩机静叶 (Stator Compressor Blade)<br><strong>作用:</strong> 与上述压缩机静叶类似，用于调整气流方向，确保空气在压缩机内的流动更加高效和稳定。';
              labelPosition = { x: 100, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_Turbine_stg-100-1_Compressor_Shell-1':
              description = '<strong>名称:</strong> 压缩机壳体 (Compressor Shell)<br><strong>作用:</strong> 压缩机壳体是压缩机的外部结构，用于容纳和保护压缩机内部的转子、叶片等部件，同时确保气流的密封性和稳定性。';
              labelPosition = { x: 800, y: -80, z: -100 };
              break;
            case 'Turbine_stg-200_-_Turbine_stg-100-1_gear_H1-1':
              description = '<strong>名称:</strong> 进气道外壳 (Inlet duct casing)<br><strong>作用:</strong> 进气道外壳是发动机进气系统的外部结构，主要作用是引导气流平稳进入压气机，同时保护内部部件，确保整体结构的稳定性。';
              // labelPosition = { x: 25, y: 0, z: 0 }; 
              break;
            case 'Turbine_stg-200_-_Turbine_stg-100-1_Gear_H2-1':
              description = '<strong>名称:</strong> 燃烧室 (Combustion Chamber)<br><strong>作用:</strong> 在高速空气流中高效、稳定地燃烧燃料，产生高温高压气体，并安全送入涡轮，为整个燃气轮机提供能量源泉。';
              labelPosition = { x: 15, y: -50, z: 0 };
              break;
            default:
              description = `<strong>部件名称:</strong> ${child.name}`; // 默认显示部件名称
              break;
          }
          labelDiv.innerHTML = description; // 使用 innerHTML 插入带格式的文本

          labelDiv.style.visibility = 'hidden'; // 初始隐藏标签

          const label = new CSS2DObject(labelDiv);
          label.position.set(labelPosition.x, labelPosition.y, labelPosition.z);
          // label.position.set(10, 0.5, 0); // 设置标签的位置偏移
          child.add(label); // 将标签绑定到组件

          // 存储标签的引用
          child.userData.label = label;

          // 打印标签的可见性状态（调试用）
          console.log(child.userData.label.element.style.visibility);
        }
      });


      model.traverse((child) => {
        if (child.isMesh) {
          // 创建标签
          const labelDiv2 = document.createElement('div');
          labelDiv2.className = 'label2';

          // 根据子对象的名称或其他属性设置不同的文字说明
          let description = ''; // 默认文字说明
          let labelPosition = { x: 10, y: 0.5, z: 0 };
          switch (child.name) {
            case 'Turbine_stg-200_-_comp_runner-1':
              description = '涡轮叶片 (Turbine Blade)';
              labelPosition = { x: -75, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_Compressor_Blade_-_in-1':
              description = '压缩机叶片 (Compressor Blade)';
              labelPosition = { x: 90, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_Compressor_Blade-1':
              description = '压缩机叶片 (Compressor Blade)';
              labelPosition = { x: 80, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_coplete-1':
              description = '低压涡轮叶片 (Low-pressure turbine blade)';
              labelPosition = { x: 60, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_Drive_Turbine-1':
              description = '驱动涡轮 (Drive Turbine)';
              labelPosition = { x: -90, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_shaft-1':
              description = '轴 (Shaft)';
              break;
            case 'Turbine_stg-200_-_stator_comp_-_in-1':
              description = '压缩机静叶 (Stator Compressor Blade)';
              labelPosition = { x: 60, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_stator_comp-1':
              description = '压缩机静叶 (Stator Compressor Blade)';
              labelPosition = { x: 80, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_Turbine_stg-100-1_Compressor_Shell-1':
              description = '压缩机壳体 (Compressor Shell)';
              labelPosition = { x: 80, y: -80, z: -100 };
              break;
            case 'Turbine_stg-200_-_Turbine_stg-100-1_gear_H1-1':
              description = '进气道外壳 (Inlet duct casing)';
              labelPosition = { x: 0, y: 0, z: 0 };
              break;
            case 'Turbine_stg-200_-_Turbine_stg-100-1_Gear_H2-1':
              description = '燃烧室 (Combustion Chamber)';
              labelPosition = { x: 15, y: -50, z: 0 };
              break;
            default:
              description = `<strong>部件名称:</strong> ${child.name}`; // 默认显示部件名称
              break;
          }
          labelDiv2.innerHTML = description; // 使用 innerHTML 插入带格式的文本

          labelDiv2.style.visibility = 'hidden'; // 初始隐藏标签

          const label2 = new CSS2DObject(labelDiv2);
          label2.position.set(labelPosition.x, labelPosition.y, labelPosition.z);
          // label.position.set(10, 0.5, 0); // 设置标签的位置偏移
          child.add(label2); // 将标签绑定到组件

          // 存储标签的引用
          child.userData.label2 = label2;

          // 打印标签的可见性状态（调试用）
          console.log(child.userData.label2.element.style.visibility);
        }
      });

      // 进入时的动画效果
      // Turbine stg-200 - Turbine stg-100-1 gear H1-1
      gear_h11 = model.getObjectByName('Turbine_stg-200_-_Turbine_stg-100-1_gear_H1-1');
      if (gear_h11) {
        gear_h11.userData.defaultX = gear_h11.position.x;
        gear_h11.userData.defaultY = gear_h11.position.y;
        gear_h11.userData.defaultZ = gear_h11.position.z;
        gear_h11.userData.initialScale = gear_h11.scale.clone(); // 记录初始缩放
        // 使用 GSAP 创建动画
        gsap.fromTo(
          gear_h11.position,
          {
            x: gear_h11.position.x + 2, // 起始位置（向右移动 2 个单位）
            z: gear_h11.position.z + 3 // 起始位置（向前移动 3 个单位）
          },
          {
            x: gear_h11.position.x, // 结束位置（回到原始位置）
            z: gear_h11.position.z,
            duration: 3, // 动画持续时间
            ease: 'power2.out', // 缓动函数
          }
        );
      }

      // Turbine stg-200 - Compressor Blade - in-1
      compressor_blade_in1 = model.getObjectByName('Turbine_stg-200_-_Compressor_Blade_-_in-1');
      if (compressor_blade_in1) {
        compressor_blade_in1.userData.defaultX = compressor_blade_in1.position.x;
        compressor_blade_in1.userData.defaultY = compressor_blade_in1.position.y;
        compressor_blade_in1.userData.defaultZ = compressor_blade_in1.position.z;
        compressor_blade_in1.userData.initialScale = compressor_blade_in1.scale.clone(); // 记录初始缩放
        gsap.fromTo(
          compressor_blade_in1.position,
          {
            x: compressor_blade_in1.position.x + 5, // 起始位置（向右移动 5 个单位）
          },
          {
            x: compressor_blade_in1.position.x, // 结束位置（回到原始位置）
            duration: 3,
            ease: 'power2.out',
          }
        );
      }

      // Turbine stg-200 - stator comp - in-1
      stator_comp_in1 = model.getObjectByName('Turbine_stg-200_-_stator_comp_-_in-1');
      if (stator_comp_in1) {
        stator_comp_in1.userData.defaultX = stator_comp_in1.position.x;
        stator_comp_in1.userData.defaultY = stator_comp_in1.position.y;
        stator_comp_in1.userData.defaultZ = stator_comp_in1.position.z;
        stator_comp_in1.userData.initialScale = stator_comp_in1.scale.clone();
        gsap.fromTo(
          stator_comp_in1.position,
          {
            x: stator_comp_in1.position.x + 3, // 起始位置（向右移动 3 个单位）
          },
          {
            x: stator_comp_in1.position.x, // 结束位置（回到原始位置）
            duration: 3,
            ease: 'power2.out',
          }
        );
      }

      // Turbine stg-200 - Turbine stg-100-1 Compressor Shell-1
      compressor_shell1 = model.getObjectByName('Turbine_stg-200_-_Turbine_stg-100-1_Compressor_Shell-1');
      if (compressor_shell1) {
        compressor_shell1.userData.defaultX = compressor_shell1.position.x;
        compressor_shell1.userData.defaultY = compressor_shell1.position.y;
        compressor_shell1.userData.defaultZ = compressor_shell1.position.z;
        compressor_shell1.userData.initialScale = compressor_shell1.scale.clone();
        gsap.fromTo(
          compressor_shell1.position,
          {
            x: compressor_shell1.position.x - 4, // 起始位置（向左移动 4 个单位）
            z: compressor_shell1.position.z + 3, // 起始位置（向前移动 3 个单位）
          },
          {
            x: compressor_shell1.position.x, // 结束位置（回到原始位置）
            z: compressor_shell1.position.z,
            duration: 3,
            ease: 'power2.out',
          }
        );
      }

      // 悬浮效果
      // Turbine stg-200 - Compressor Blade-1
      compressor_blade1 = model.getObjectByName('Turbine_stg-200_-_Compressor_Blade-1');
      if (compressor_blade1) {
        compressor_blade1.userData.defaultX = compressor_blade1.position.x; // 保存默认 Y 位置
        compressor_blade1.userData.defaultY = compressor_blade1.position.y; // 保存默认 Y 位置
        compressor_blade1.userData.defaultZ = compressor_blade1.position.z; // 保存默认 Z 位置
        compressor_blade1.userData.initialScale = compressor_blade1.scale.clone();
        gsap.fromTo(
          compressor_blade1.position,
          {
            y: compressor_blade1.position.y + 2, // 起始位置（向上移动 2 个单位）
            z: compressor_blade1.position.z + 0.5, // 起始位置（向前移动 0.5 个单位）
          },
          {
            y: compressor_blade1.position.y, // 结束位置（回到原始位置）
            z: compressor_blade1.position.z,
            duration: 3,
            ease: 'power2.out',
          }
        );
      }

      // Turbine stg-200 - stator comp-1
      stator_comp1 = model.getObjectByName('Turbine_stg-200_-_stator_comp-1');
      if (stator_comp1) {
        stator_comp1.userData.defaultX = stator_comp1.position.x;
        stator_comp1.userData.defaultY = stator_comp1.position.y; // 保存默认 Y 位置
        stator_comp1.userData.defaultZ = stator_comp1.position.z; // 保存默认 Z 位置
        stator_comp1.userData.initialScale = stator_comp1.scale.clone();
        gsap.fromTo(
          stator_comp1.position,
          {
            y: stator_comp1.position.y - 2, // 起始位置（向下移动 2 个单位）
            z: stator_comp1.position.z - 0.5, // 起始位置（向后移动 0.5 个单位）
          },
          {
            y: stator_comp1.position.y, // 结束位置（回到原始位置）
            z: stator_comp1.position.z,
            duration: 3,
            ease: 'power2.out',
          }
        );
      }

      // Turbine stg-200 - Turbine stg-100-1 Gear H2-1
      gear_h21 = model.getObjectByName('Turbine_stg-200_-_Turbine_stg-100-1_Gear_H2-1');
      if (gear_h21) {
        gear_h21.userData.defaultX = gear_h21.position.x;
        gear_h21.userData.defaultY = gear_h21.position.y; // 保存默认 Y 位置
        gear_h21.userData.defaultZ = gear_h21.position.z; // 保存默认 Z 位置
        gear_h21.userData.initialScale = gear_h21.scale.clone();
        gsap.fromTo(
          gear_h21.position,
          {
            x: gear_h21.position.x - 2, // 起始位置（向左移动 2 个单位）
            z: gear_h21.position.z + 2, // 起始位置（向前移动 2 个单位）
          },
          {
            x: gear_h21.position.x, // 结束位置（回到原始位置）
            z: gear_h21.position.z,
            duration: 3,
            ease: 'power2.out',
          }
        );
      }

      // Turbine stg-200 - coplete-1
      coplete1 = model.getObjectByName('Turbine_stg-200_-_coplete-1');
      if (coplete1) {
        coplete1.userData.defaultX = coplete1.position.x;
        coplete1.userData.defaultY = coplete1.position.y; // 保存默认 Y 位置
        coplete1.userData.defaultZ = coplete1.position.z; // 保存默认 Z 位置
        coplete1.userData.initialScale = coplete1.scale.clone();
        gsap.fromTo(
          coplete1.position,
          {
            x: coplete1.position.x - 3, // 起始位置（向左移动 3 个单位）
          },
          {
            x: coplete1.position.x, // 结束位置（回到原始位置）
            duration: 3,
            ease: 'power2.out',
          }
        );
      }

      // Turbine stg-200 - comp runner-1
      comp_runner1 = model.getObjectByName('Turbine_stg-200_-_comp_runner-1');
      if (comp_runner1) {
        comp_runner1.userData.defaultX = comp_runner1.position.x;
        comp_runner1.userData.defaultY = comp_runner1.position.y; // 保存默认 Y 位置
        comp_runner1.userData.defaultZ = comp_runner1.position.z; // 保存默认 Z 位置
        comp_runner1.userData.initialScale = comp_runner1.scale.clone();
        gsap.fromTo(
          comp_runner1.position,
          {
            x: comp_runner1.position.x - 5, // 起始位置（向左移动 5 个单位）
          },
          {
            x: comp_runner1.position.x, // 结束位置（回到原始位置）
            duration: 3,
            ease: 'power2.out',
          }
        );
      }

      // Turbine stg-200 - Drive Turbine-1
      drive1 = model.getObjectByName('Turbine_stg-200_-_Drive_Turbine-1');
      if (drive1) {
        drive1.userData.defaultX = drive1.position.x;
        drive1.userData.defaultY = drive1.position.y; // 保存默认 Y 位置
        drive1.userData.defaultZ = drive1.position.z; // 保存默认 Z 位置
        drive1.userData.initialScale = drive1.scale.clone();
        gsap.fromTo(
          drive1.position,
          {
            x: drive1.position.x - 7, // 起始位置（向左移动 7 个单位）
          },
          {
            x: drive1.position.x, // 结束位置（回到原始位置）
            duration: 3,
            ease: 'power2.out',
          }
        );
      }

      shaft1 = model.getObjectByName('Turbine_stg-200_-_shaft-1');
      if (shaft1) {
        console.log('Shaft1 found:', shaft1);
        shaft1.userData.defaultX = shaft1.position.x;
        shaft1.userData.defaultY = shaft1.position.y;
        shaft1.userData.defaultZ = shaft1.position.z;
        shaft1.userData.initialScale = shaft1.scale.clone();
        console.log('Shaft1 userData:', shaft1.userData);
        console.log('Shaft1 position:', shaft1.position);
        console.log('Shaft1 geometry:', shaft1.geometry);
        console.log('Shaft1 material:', shaft1.material);
      } else {
        console.error('Shaft1 not found in model');
      }

      // 设置动画混合器
      mixer = new THREE.AnimationMixer(model);
      gltf.animations.forEach((clip) => {
        mixer.clipAction(clip).play(); // 播放所有动画
      });

      // 将相机对准模型中心
      const box = new THREE.Box3().setFromObject(model);
      const center = new THREE.Vector3();
      box.getCenter(center);
      camera.lookAt(center);
    },
    undefined,
    (error) => console.error('Error loading GLB:', error) // 处理加载错误
  );
  window.addEventListener('click', handleClick);
  // 动画循环

  clock = new THREE.Clock();
  animate();
  window.addEventListener('resize', handleResize);
});

const split_turbine = () => {
  controls.enabled = true; // 启用鼠标监听
  const duration = 2; // 动画持续时间
  const ease = 'power2.out'; // 缓动函数
  isComponentSelected.value = false;

  scene.traverse((child) => {
    if (child.userData.label) {
      child.userData.label.element.style.visibility = 'hidden';
    }
    if (!child.visible) {
      child.visible = true;
      // opacity=true;
    }
    if (child.userData.label2) {
      child.userData.label2.element.style.visibility = 'visible';
    }
  });
  // 将摄像机拉远
  gsap.to(camera.position, {
    x: 2.1988,
    y: 3.2648,
    z: -6.9349 - 5, // 调整 z 值以拉远摄像机
    duration,
    ease,
    onUpdate: () => {
      controls.update(); // 更新控制器
      controls.target.set(0, 0, 0);
    },
  });

  if (gear_h11) {
    gsap.to(gear_h11.position, {
      x: gear_h11.userData.defaultX + 8,
      y: gear_h11.userData.defaultY + 5,
      z: gear_h11.userData.defaultZ + 2,
      duration,
      ease,
    });
    gsap.to(gear_h11.scale, {
      x: gear_h11.userData.initialScale.x,
      y: gear_h11.userData.initialScale.y,
      z: gear_h11.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (stator_comp_in1) {
    gsap.to(stator_comp_in1.position, {
      x: stator_comp_in1.userData.defaultX + 2,
      y: stator_comp_in1.userData.defaultY + 4,
      z: stator_comp_in1.userData.defaultZ + 1,
      duration,
      ease,
    });
    gsap.to(stator_comp_in1.scale, {
      x: stator_comp_in1.userData.initialScale.x,
      y: stator_comp_in1.userData.initialScale.y,
      z: stator_comp_in1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (compressor_blade1) {
    gsap.to(compressor_blade1.position, {
      x: compressor_blade1.userData.defaultX + 5,
      y: compressor_blade1.userData.defaultY - 3,
      z: compressor_blade1.userData.defaultZ - 1,
      duration,
      ease,
    });
    gsap.to(compressor_blade1.scale, {
      x: compressor_blade1.userData.initialScale.x,
      y: compressor_blade1.userData.initialScale.y,
      z: compressor_blade1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (compressor_blade_in1) {
    gsap.to(compressor_blade_in1.position, {
      x: compressor_blade_in1.userData.defaultX + 8,
      y: compressor_blade_in1.userData.defaultY - 2,
      z: compressor_blade_in1.userData.defaultZ,
      duration,
      ease,
    });
    gsap.to(compressor_blade_in1.scale, {
      x: compressor_blade_in1.userData.initialScale.x,
      y: compressor_blade_in1.userData.initialScale.y,
      z: compressor_blade_in1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (compressor_shell1) {
    gsap.to(compressor_shell1.position, {
      x: compressor_shell1.userData.defaultX - 15,
      y: compressor_shell1.userData.defaultY + 6,
      z: compressor_shell1.userData.defaultZ + 2,
      duration,
      ease,
    });
    gsap.to(compressor_shell1.scale, {
      x: compressor_shell1.userData.initialScale.x,
      y: compressor_shell1.userData.initialScale.y,
      z: compressor_shell1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (stator_comp1) {
    gsap.to(stator_comp1.position, {
      x: stator_comp1.userData.defaultX,
      y: stator_comp1.userData.defaultY + 4,
      z: stator_comp1.userData.defaultZ - 1,
      duration,
      ease,
    });
    gsap.to(stator_comp1.scale, {
      x: stator_comp1.userData.initialScale.x,
      y: stator_comp1.userData.initialScale.y,
      z: stator_comp1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (gear_h21) {
    gsap.to(gear_h21.position, {
      x: gear_h21.userData.defaultX - 6,
      y: gear_h21.userData.defaultY - 6,
      z: gear_h21.userData.defaultZ - 1,
      duration,
      ease,
    });
    gsap.to(gear_h21.scale, {
      x: gear_h21.userData.initialScale.x,
      y: gear_h21.userData.initialScale.y,
      z: gear_h21.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (coplete1) {
    gsap.to(coplete1.position, {
      x: coplete1.userData.defaultX + 3,
      y: coplete1.userData.defaultY - 3,
      z: coplete1.userData.defaultZ - 2,
      duration,
      ease,
    });
    gsap.to(coplete1.scale, {
      x: coplete1.userData.initialScale.x,
      y: coplete1.userData.initialScale.y,
      z: coplete1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (comp_runner1) {
    gsap.to(comp_runner1.position, {
      x: comp_runner1.userData.defaultX - 3,
      y: comp_runner1.userData.defaultY + 2,
      z: comp_runner1.userData.defaultZ - 2,
      duration,
      ease,
    });
    gsap.to(comp_runner1.scale, {
      x: comp_runner1.userData.initialScale.x,
      y: comp_runner1.userData.initialScale.y,
      z: comp_runner1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (drive1) {
    gsap.to(drive1.position, {
      x: drive1.userData.defaultX - 9,
      y: drive1.userData.defaultY,
      z: drive1.userData.defaultZ - 2,
      duration,
      ease,
    });
    gsap.to(drive1.scale, {
      x: drive1.userData.initialScale.x,
      y: drive1.userData.initialScale.y,
      z: drive1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (shaft1) {
    gsap.to(shaft1.position, {
      x: shaft1.userData.defaultX,
      y: shaft1.userData.defaultY,
      z: shaft1.userData.defaultZ,
      duration,
      ease,
    });
    gsap.to(shaft1.scale, {
      x: shaft1.userData.initialScale.x,
      y: shaft1.userData.initialScale.y,
      z: shaft1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if_split.value = true;
};

// 修改 reset_turbine 函数
const reset_turbine = () => {
  controls.enabled = true; // 启用鼠标监听
  const duration = 2; // 动画持续时间
  const ease = 'power2.out'; // 缓动函数
  if_split.value = false;
  isComponentSelected.value = false;
  scene.traverse((child) => {
    if (child.userData.label) {
      child.userData.label.element.style.visibility = 'hidden';
    }
    if (!child.visible) {
      child.visible = true;
    } if (child.userData.label2) {
      child.userData.label2.element.style.visibility = 'hidden';
    }
  });
  // 将摄像机恢复到初始位置
  gsap.to(camera.position, {
    x: 2.1988,
    y: 3.2648,
    z: -6.9349,
    duration: 3,
    ease,
    onUpdate: () => {
      controls.update(); // 更新控制器
      controls.target.set(0, 0, 0); // 恢复控制器的目标点
    },
  });
  if (gear_h11) {
    gsap.to(gear_h11.position, {
      x: gear_h11.userData.defaultX,
      y: gear_h11.userData.defaultY,
      z: gear_h11.userData.defaultZ,
      duration,
      ease,
    }); gsap.to(gear_h11.scale, {
      x: gear_h11.userData.initialScale.x,
      y: gear_h11.userData.initialScale.y,
      z: gear_h11.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (stator_comp_in1) {
    gsap.to(stator_comp_in1.position, {
      x: stator_comp_in1.userData.defaultX,
      y: stator_comp_in1.userData.defaultY,
      z: stator_comp_in1.userData.defaultZ,
      duration,
      ease,
    }); gsap.to(stator_comp_in1.scale, {
      x: stator_comp_in1.userData.initialScale.x,
      y: stator_comp_in1.userData.initialScale.y,
      z: stator_comp_in1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (compressor_blade1) {
    gsap.to(compressor_blade1.position, {
      x: compressor_blade1.userData.defaultX,
      y: compressor_blade1.userData.defaultY,
      z: compressor_blade1.userData.defaultZ,
      duration,
      ease,
    }); gsap.to(compressor_blade1.scale, {
      x: compressor_blade1.userData.initialScale.x,
      y: compressor_blade1.userData.initialScale.y,
      z: compressor_blade1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (compressor_blade_in1) {
    gsap.to(compressor_blade_in1.position, {
      x: compressor_blade_in1.userData.defaultX,
      y: compressor_blade_in1.userData.defaultY,
      z: compressor_blade_in1.userData.defaultZ,
      duration,
      ease,
    }); gsap.to(compressor_blade_in1.scale, {
      x: compressor_blade_in1.userData.initialScale.x,
      y: compressor_blade_in1.userData.initialScale.y,
      z: compressor_blade_in1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (compressor_shell1) {
    gsap.to(compressor_shell1.position, {
      x: compressor_shell1.userData.defaultX,
      y: compressor_shell1.userData.defaultY,
      z: compressor_shell1.userData.defaultZ,
      duration,
      ease,
    }); gsap.to(compressor_shell1.scale, {
      x: compressor_shell1.userData.initialScale.x,
      y: compressor_shell1.userData.initialScale.y,
      z: compressor_shell1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (stator_comp1) {
    gsap.to(stator_comp1.position, {
      x: stator_comp1.userData.defaultX,
      y: stator_comp1.userData.defaultY,
      z: stator_comp1.userData.defaultZ,
      duration,
      ease,
    }); gsap.to(stator_comp1.scale, {
      x: stator_comp1.userData.initialScale.x,
      y: stator_comp1.userData.initialScale.y,
      z: stator_comp1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (gear_h21) {
    gsap.to(gear_h21.position, {
      x: gear_h21.userData.defaultX,
      y: gear_h21.userData.defaultY,
      z: gear_h21.userData.defaultZ,
      duration,
      ease,
    }); gsap.to(gear_h21.scale, {
      x: gear_h21.userData.initialScale.x,
      y: gear_h21.userData.initialScale.y,
      z: gear_h21.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (coplete1) {
    gsap.to(coplete1.position, {
      x: coplete1.userData.defaultX,
      y: coplete1.userData.defaultY,
      z: coplete1.userData.defaultZ,
      duration,
      ease,
    }); gsap.to(coplete1.scale, {
      x: coplete1.userData.initialScale.x,
      y: coplete1.userData.initialScale.y,
      z: coplete1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (comp_runner1) {
    gsap.to(comp_runner1.position, {
      x: comp_runner1.userData.defaultX,
      y: comp_runner1.userData.defaultY,
      z: comp_runner1.userData.defaultZ,
      duration,
      ease,
    }); gsap.to(comp_runner1.scale, {
      x: comp_runner1.userData.initialScale.x,
      y: comp_runner1.userData.initialScale.y,
      z: comp_runner1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (drive1) {
    gsap.to(drive1.position, {
      x: drive1.userData.defaultX,
      y: drive1.userData.defaultY,
      z: drive1.userData.defaultZ,
      duration,
      ease,
    }); gsap.to(drive1.scale, {
      x: drive1.userData.initialScale.x,
      y: drive1.userData.initialScale.y,
      z: drive1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
  if (shaft1) {
    gsap.to(shaft1.position, {
      x: shaft1.userData.defaultX,
      y: shaft1.userData.defaultY,
      z: shaft1.userData.defaultZ,
      duration,
      ease,
    });
    gsap.to(shaft1.scale, {
      x: shaft1.userData.initialScale.x,
      y: shaft1.userData.initialScale.y,
      z: shaft1.userData.initialScale.z,
      duration: 1,
      ease: 'power2.out',
    });
  }
};
const cleanup = () => {
  // 1. 清理 WebGLRenderer
  if (renderer) {
    renderer.dispose(); // 释放 WebGL 上下文
    container.value?.removeChild(renderer.domElement); // 移除 DOM 元素
    renderer = null;
  }

  // 2. 清理 CSS2DRenderer
  if (labelRenderer) {
    document.body.removeChild(labelRenderer.domElement);
    labelRenderer = null;
  }

  // 3. 清理 OrbitControls
  if (controls) {
    controls.dispose(); // 移除事件监听
    controls = null;
  }

  // 4. 清理场景中的几何体和材质
  if (scene) {
    scene.traverse((child) => {
      if (child.isMesh) {
        child.geometry?.dispose(); // 释放几何体
        if (Array.isArray(child.material)) {
          child.material.forEach(m => m.dispose()); // 释放材质数组
        } else {
          child.material?.dispose(); // 释放单一材质
        }
      }
    });
    scene.clear(); // 清空场景
  }
  if (animationId) {
    cancelAnimationFrame(animationId); // 停止递归调用
  }
};
onUnmounted(() => {
  cleanup(); // 组件销毁时触发资源释放
});
</script>

<style scoped>
.three-container {
  width: 100%;
  height: 100vh;
  margin: 0;
  padding: 0;
  overflow: hidden;
}

.button-style {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  /* margin:0 auto; */
}
</style>
<!-- Turbine stg-200 - shaft-1 -->

<style>
.label {
  /* display:none; */
  color: rgb(0, 0, 0);
  background-color: rgb(232, 232, 232);
  padding: 5px 5px;
  border-radius: 5px;
  font-family: Arial, sans-serif;
  font-size: 14px;
  pointer-events: none;
}

.label1 {
  color: white;
  /* 文字颜色 */
  background-color: rgba(0, 0, 0, 0.7);
  /* 背景颜色 */
  padding: 8px;
  /* 内边距 */
  border-radius: 4px;
  /* 圆角 */
  font-size: 14px;
  /* 字体大小 */
  max-width: 200px;
  /* 最大宽度，超出自动换行 */
  white-space: normal;
  /* 允许换行 */
  word-wrap: break-word;
  /* 长单词换行 */
  pointer-events: none;
  /* 防止标签阻挡点击事件 */
}


.label2 {
  color: white;
  /* 文字颜色 */
  background-color: rgba(83, 83, 83, 0.9);
  /* 背景颜色 */
  padding: 8px;
  /* 内边距 */
  border-radius: 4px;
  /* 圆角 */
  font-size: 12px;
  /* 字体大小 */
  max-width: 150px;
  /* 最大宽度，超出自动换行 */
  white-space: normal;
  /* 允许换行 */
  word-wrap: break-word;
  /* 长单词换行 */
  pointer-events: none;
  /* 防止标签阻挡点击事件 */
}
</style>