import torch
from torch import optim
from torch.utils.data import DataLoader
import os
import numpy as np
import requests
from flask import Flask, jsonify, request
from flask_cors import CORS
import zipfile
import shutil

from WienerProcessModel import WienerProcessModel
from model2 import Model2
from HybridModel import HybridModel
from model import Model
from train import Trainer
from turbofandataset import Turbofandataset

app = Flask(__name__)
CORS(app)  # 允许跨域请求
os.environ['KMP_DUPLICATE_LIB_OK'] = 'TRUE'

# 数据集存储路径
DATASET_DIR = './datasets/'
MODEL_1_SAVE_PATH = './Model_Parameter/LSTM_model.pth'  # 模型1保存路径
MODEL_2_SAVE_PATH = './Model_Parameter/GRU_model.pth'  # 模型2保存路径
MODEL_WIENER_SAVE_PATH = './Model_Parameter/model_wiener.pth'
MODEL_SAVE_PATH = " "


def preprocess_dataset(dataset_folder, dataset_name):
    """预处理数据集"""
    try:
        print(f"开始预处理数据集: {dataset_name}")

        # 构建文件路径
        train_file = os.path.join(dataset_folder, f'train_{dataset_name}.txt')
        test_file = os.path.join(dataset_folder, f'test_{dataset_name}.txt')

        # 检查文件是否存在
        if not os.path.exists(train_file) or not os.path.exists(test_file):
            raise Exception("原始训练集或测试集文件不存在")

        # 数据读取
        full_data = np.loadtxt(fname=train_file, dtype=np.float32)
        full_test = np.loadtxt(fname=test_file, dtype=np.float32)

        # engineID and working time cycle, no need to norm
        prefix_data = full_data[:, [0, 1]]
        prefix_test = full_test[:, [0, 1]]
        # operational settings and raw sensor data
        inputs_data = full_data[:, 2:]
        inputs_test = full_test[:, 2:]

        # to avoid devide zero
        eps = 1e-12
        min_val = np.min(inputs_data, axis=0)
        max_val = np.max(inputs_data, axis=0)
        # 最大最小标准化
        normed_data = (inputs_data - min_val) / (max_val - min_val + eps)
        normed_test = (inputs_test - min_val) / (max_val - min_val + eps)

        output_data = np.concatenate((prefix_data, normed_data), axis=1)
        output_test = np.concatenate((prefix_test, normed_test), axis=1)

        # 保存预处理后的文件
        normed_train_path = os.path.join(dataset_folder, f'train_{dataset_name}_normed.txt')
        normed_test_path = os.path.join(dataset_folder, f'test_{dataset_name}_normed.txt')

        np.savetxt(normed_train_path, output_data, fmt='%f')
        np.savetxt(normed_test_path, output_test, fmt='%f')

        print(f"预处理完成，保存到: {normed_train_path} 和 {normed_test_path}")
        return True
    except Exception as e:
        print(f"预处理数据集失败: {e}")
        return False


def download_and_extract_dataset(dataset_name, dataset_url):
    """下载数据集并解压到指定目录"""
    try:
        print(f"开始下载数据集: {dataset_name}")
        response = requests.get(dataset_url)
        if response.status_code == 200:
            # 确保数据集目录存在
            os.makedirs(DATASET_DIR, exist_ok=True)

            # 保存压缩文件
            zip_path = os.path.join(DATASET_DIR, f"{dataset_name}.zip")
            with open(zip_path, 'wb') as f:
                f.write(response.content)
            print(f"数据集下载完成，保存到: {zip_path}")

            # 解压数据集
            print("开始解压数据集...")
            with zipfile.ZipFile(zip_path, 'r') as zip_ref:
                # 获取解压后的文件夹名称
                extracted_folder_name = zip_ref.namelist()[0].split('/')[0]  # 假设解压后的第一个文件或文件夹是根目录
                zip_ref.extractall(DATASET_DIR)
            print("数据集解压完成")

            # 删除压缩文件
            os.remove(zip_path)
            print(f"删除压缩文件: {zip_path}")

            # 获取解压后的文件夹路径
            extracted_folder = os.path.join(DATASET_DIR, extracted_folder_name)
            if not os.path.isdir(extracted_folder):
                raise Exception("解压后的文件夹未找到")

            # 目标文件夹路径
            new_folder_path = os.path.join(DATASET_DIR, dataset_name)

            # 如果目标文件夹已经存在，检查是否是当前解压的文件夹
            if os.path.exists(new_folder_path):
                if os.path.samefile(extracted_folder, new_folder_path):
                    print(f"目标文件夹已存在且是当前解压的文件夹: {new_folder_path}")
                else:
                    raise Exception(f"目标文件夹已存在且不是当前解压的文件夹: {new_folder_path}")
            else:
                # 重命名解压后的文件夹为数据集名称
                os.rename(extracted_folder, new_folder_path)
                print(f"重命名文件夹为: {new_folder_path}")

            # 预处理数据集
            if not preprocess_dataset(new_folder_path, dataset_name):
                raise Exception("数据集预处理失败")

            return new_folder_path
        else:
            print(f"下载失败，状态码: {response.status_code}")
            return None
    except Exception as e:
        print(f"下载或解压数据集失败: {e}")
        return None


def find_dataset_files(dataset_folder, dataset_name):
    """在数据集文件夹中查找训练集、测试集和测试集结果文件"""
    # 使用预处理后的归一化文件
    train_file = os.path.join(dataset_folder, f'train_{dataset_name}_normed.txt')
    test_file = os.path.join(dataset_folder, f'test_{dataset_name}_normed.txt')
    rul_file = os.path.join(dataset_folder, f'RUL_{dataset_name}.txt')

    # 检查文件是否存在
    if not os.path.exists(train_file):
        train_file = None
    if not os.path.exists(test_file):
        test_file = None
    if not os.path.exists(rul_file):
        rul_file = None

    return train_file, test_file, rul_file


@app.route('/load-dataset', methods=['GET'])
def load_dataset():
    """加载数据集接口"""
    # 获取请求参数
    dataset_name = request.args.get('datasetName')
    dataset_url = request.args.get('datasetUrl')

    # 检查数据集文件夹是否存在
    dataset_folder = os.path.join(DATASET_DIR, dataset_name)
    if not os.path.exists(dataset_folder):
        # 如果不存在，则下载并解压数据集
        dataset_folder = download_and_extract_dataset(dataset_name, dataset_url)
        if not dataset_folder:
            return jsonify({"error": "数据集下载或解压失败"}), 500

        # 数据集下载和解压完成后，返回 200 OK
        return jsonify({"message": "数据集下载并解压完成"}), 200

    # 检查预处理文件是否存在
    normed_train_file = os.path.join(dataset_folder, f'train_{dataset_name}_normed.txt')
    normed_test_file = os.path.join(dataset_folder, f'test_{dataset_name}_normed.txt')

    if not os.path.exists(normed_train_file) or not os.path.exists(normed_test_file):
        # 如果预处理文件不存在，则进行预处理
        if not preprocess_dataset(dataset_folder, dataset_name):
            return jsonify({"error": "数据集预处理失败"}), 500
        return jsonify({"message": "数据集预处理完成"}), 200

    # 如果数据集已存在且已预处理，返回 200 OK
    return jsonify({"message": "数据集已存在且已预处理"}), 200


@app.route('/get-rul-data', methods=['GET'])
def get_rul_data():
    """获取预测数据接口"""
    # 获取请求参数
    dataset_name = request.args.get('datasetName')
    model_id = request.args.get('modelId')

    # 检查数据集文件夹是否存在
    dataset_folder = os.path.join(DATASET_DIR, dataset_name)
    if not os.path.exists(dataset_folder):
        return jsonify({"error": "数据集未加载"}), 404

    # 查找训练集、测试集和测试集结果文件
    train_file, test_file, rul_file = find_dataset_files(dataset_folder, dataset_name)
    if not train_file or not test_file or not rul_file:
        return jsonify({"error": "数据集文件不完整"}), 500

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    epochs = 10
    batch_size = 64

    # 加载训练和测试数据集
    trainset = Turbofandataset(mode='train', dataset=train_file)
    train_loader = DataLoader(dataset=trainset, batch_size=batch_size, shuffle=True)

    testset = Turbofandataset(mode='test', dataset=test_file, rul_result=rul_file)
    test_loader = DataLoader(dataset=testset, batch_size=batch_size, shuffle=False)
    print('Dataset loaded successfully!')

    # 初始化模型、优化器和训练器
    if model_id == '1':
        model = Model()
        MODEL_SAVE_PATH = MODEL_1_SAVE_PATH
        print("使用模型1预测")
    elif model_id == '2':
        model = Model2()
        MODEL_SAVE_PATH = MODEL_2_SAVE_PATH
        print("使用模型2预测")
    elif model_id == '3':  # 替换原Wiener模型为HybridModel
        model = HybridModel(
            input_size=17,  # 输入特征维度（根据数据集调整）
            lstm_hidden_size=150,  # 与LSTM分支一致
            wiener_hidden_size=64,  # Wiener分支隐藏层
            sequence_length=30  # 序列长度（根据数据集调整）
        )
        MODEL_SAVE_PATH = './Model_Parameter/hybrid_model.pth'  # 新保存路径
        print("使用Wiener+LSTM混合模型预测")
    else:
        return jsonify({"error": "无效的模型ID"}), 400

    optimizer = optim.Adam(model.parameters(), lr=1e-3)
    trainer = Trainer(
        model=model,
        model_optimizer=optimizer,
        print_every=50,
        epochs=epochs,
        device=device,
        model_save_path=MODEL_SAVE_PATH
    )

    # 初始化 losses 变量
    losses = []

    # 检查是否有保存的模型参数
    if os.path.exists(MODEL_SAVE_PATH):
        model.load_state_dict(torch.load(MODEL_SAVE_PATH))
        print("Loaded model parameters from", MODEL_SAVE_PATH)
    else:
        # 训练模型，并获取每个 epoch 的损失值
        losses = trainer.train(train_loader)

    # 测试模型
    rmse, labels, predictions = trainer.test(test_loader)

    # 转换数据为标准 Python 类型
    labels = [float(x) for x in labels]
    predictions = [float(x) for x in predictions]
    losses = [float(x) for x in losses]

    return jsonify({
        "labels": labels,
        "predictions": predictions,
        "losses": losses
    })


if __name__ == '__main__':
    app.run(debug=False)