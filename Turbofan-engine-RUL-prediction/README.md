# Turbofan-engine-RUL-prediction — ML 推理服务

基于 PyTorch 和 Flask 的燃气轮机 RUL 预测推理服务。提供 CNN+LSTM、CNN+GRU、Wiener+LSTM 三种深度学习模型的 RESTful API。

## 技术栈

| 类别 | 技术 |
|---|---|
| 深度学习 | PyTorch |
| Web 框架 | Flask + flask-cors |
| 数值计算 | NumPy, SciPy |
| 可视化 | Matplotlib |

## 三种模型详解

### 1. CNN + 双向 LSTM（model.py / model1）

```
输入 (batch, seq_len, 17)
  → 双向 LSTM (hidden=150) → 输出 (batch, seq_len, 300)
  → 1D CNN (128 通道, kernel=3) + ReLU
  → 1D CNN (64 通道, kernel=3) + ReLU
  → Flatten → FC (100) → FC (50) → FC (10) → 输出 RUL
  → Dropout (p=0.2) 防过拟合
```

- 双向结构捕获前后向退化趋势
- CNN 提取局部异常模式（振动突变、温度尖峰）
- 适用于单一工况（FD001、FD003）

### 2. CNN + 双向 GRU（model2.py / model2）

```
输入 (batch, seq_len, 17)
  → 双向 GRU (hidden=150) → 输出 (batch, seq_len, 300)
  → 1D CNN (128→64 通道, kernel=3×3) + ReLU
  → Flatten → FC (100) → FC (50) → FC (10) → 输出 RUL
  → Dropout (p=0.2)
```

- GRU 参数比 LSTM 少约 25%，训练更快
- 适合实时性要求高的场景

### 3. Wiener 过程 + LSTM 混合（HybridModel.py / model3）

```
输入 (batch, seq_len, 17)
  ├── Wiener 分支: 全连接 → 64×2 层 → 输出 wiener_pred
  └── LSTM 分支: 双向 LSTM + CNN (128→64) + Flatten → 输出 lstm_feat
  → Concat [lstm_feat, wiener_pred] → FC 融合 → 输出 RUL
```

- Wiener 过程建模随机退化（偶发故障）
- LSTM 建模确定性退化（渐进磨损）
- 在复杂工况（FD002、FD004：6 种运行条件）下表现最优

### 训练参数

| 参数 | 值 |
|---|---|
| 优化器 | Adam |
| 学习率 | 0.001 |
| Epochs | 10 |
| Batch Size | 64 |
| 损失函数 | MSE |
| 评估指标 | RMSE |

## 项目结构

```
Turbofan-engine-RUL-prediction/
├── main.py                  # Flask 推理服务入口（默认端口 5000）
├── train.py                 # 模型训练逻辑
├── preprocess.py            # 数据预处理（归一化、滑动窗口）
├── turbofandataset.py       # C-MAPSS 数据集 PyTorch Dataset
├── model.py                 # CNN + 双向 LSTM
├── model2.py                # CNN + 双向 GRU
├── WienerProcessModel.py    # Wiener 退化过程模型
├── HybridModel.py           # Wiener + LSTM 混合模型
├── datasets/                # 数据集目录（FD001-FD006）
├── Model_Parameter/         # 预训练模型权重
│   ├── LSTM_model.pth
│   ├── GRU_model.pth
│   └── model_wiener.pth
└── results/                 # 预测结果输出（.mat）
```

## API 接口

| 端点 | 方法 | 参数 | 说明 |
|---|---|---|---|
| `/load-dataset` | GET | `dataset_name` (如 FD001) | 返回数据集文件列表，自动下载/解压 |
| `/get-rul-data` | GET | `dataset_name`, `model_type` (1/2/3), `file_name` | 执行 RUL 预测，返回 `{losses, actual_rul, predicted_rul}` |

`model_type` 对应关系：`1` = LSTM 模型，`2` = GRU 模型，`3` = Wiener+LSTM 混合模型。

## 数据预处理

`preprocess.py` 处理流程：
1. **滑动窗口分割**：将长序列切为固定长度训练样本
2. **归一化**：Min-Max / Z-Score 标准化
3. **RUL 标签构造**：分段线性退化标签（早期常数最大值，后期线性衰减）

## 文件命名规范

| 文件类型 | 格式 | 示例 |
|---|---|---|
| 训练数据 | `train_{数据集名}_normed.txt` | `train_FD001_normed.txt` |
| 测试数据 | `test_{数据集名}_normed.txt` | `test_FD001_normed.txt` |
| RUL 真实值 | `RUL_{数据集名}.txt` | `RUL_FD001.txt` |
| 模型权重 | `{模型名}_model.pth` | `LSTM_model.pth` |

## 启动

### 安装依赖

```bash
cd Turbofan-engine-RUL-prediction
pip install torch numpy scipy flask flask-cors matplotlib
```

### 启动服务

```bash
python main.py        # → http://localhost:5000
```

首次请求时，如果 `Model_Parameter/` 中不存在对应模型权重，服务会自动触发训练流程（Trainer 类）。训练完成后权重自动保存。

### 验证

```bash
curl "http://localhost:5000/load-dataset?dataset_name=FD001"
```

## 数据集

NASA C-MAPSS 涡扇发动机退化模拟数据：

| 子集 | 训练 | 测试 | 运行条件 | 故障模式 |
|---|---|---|---|---|
| FD001 | 100 | 100 | 1 种 | HPC |
| FD002 | 260 | 259 | 6 种 | HPC |
| FD003 | 100 | 100 | 1 种 | HPC+Fan |
| FD004 | 248 | 248 | 6 种 | HPC+Fan |
| FD005 | — | — | — | — |
| FD006 | — | — | — | — |

数据集存放于 `./datasets/`，支持 `.zip` 压缩包自动解压。
