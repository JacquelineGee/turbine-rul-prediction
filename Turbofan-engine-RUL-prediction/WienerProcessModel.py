import torch
import torch.nn as nn

class WienerProcessModel(nn.Module):
    def __init__(self, input_size, hidden_size=64, output_size=1, sequence_length=30):
        super(WienerProcessModel, self).__init__()
        self.input_size = input_size
        self.hidden_size = hidden_size
        self.output_size = output_size
        self.sequence_length = sequence_length

        # 定义模型层
        self.fc1 = nn.Linear(input_size + 1, hidden_size)  # 输入维度增加 1，用于维纳路径
        self.fc2 = nn.Linear(hidden_size, hidden_size)
        self.fc3 = nn.Linear(hidden_size, output_size)

        # 初始化权重
        self.init_weights()

    def init_weights(self):
        """初始化模型权重"""
        for m in self.modules():
            if isinstance(m, nn.Linear):
                nn.init.xavier_normal_(m.weight)
                nn.init.zeros_(m.bias)

    def generate_wiener_process(self, batch_size, sequence_length):
        """
        生成维纳过程路径
        :param batch_size: 批量大小
        :param sequence_length: 序列长度
        :return: 维纳路径，形状为 (batch_size, sequence_length)
        """
        # 生成随机步长
        increments = torch.randn(batch_size, sequence_length)  # 标准正态分布随机数
        wiener_paths = torch.cumsum(increments, dim=1)  # 累积求和，生成维纳路径
        return wiener_paths

    def forward(self, inputs):
        """
        前向传播
        :param inputs: 输入数据，形状为 (batch_size, sequence_length, input_size)
        :return: 模型输出，形状为 (batch_size, output_size)
        """
        # 打印输入形状
        #print(f"inputs shape: {inputs.shape}")  # 例如 (64, 30, 17)

        # 生成维纳路径，确保长度与 inputs 的 sequence_length 一致
        batch_size, sequence_length, input_size = inputs.shape
        wiener_paths = self.generate_wiener_process(batch_size, sequence_length)  # 形状为 (batch_size, sequence_length)

        # 将 wiener_paths 移动到与 inputs 相同的设备
        wiener_paths = wiener_paths.to(inputs.device)

        # 扩展 wiener_paths 的维度以匹配 inputs
        wiener_paths = wiener_paths.unsqueeze(-1)  # 形状变为 (batch_size, sequence_length, 1)
       # print(f"wiener_paths shape: {wiener_paths.shape}")  # 例如 (64, 30, 1)

        # 连接 inputs 和 wiener_paths
        combined_inputs = torch.cat([inputs, wiener_paths], dim=-1)  # 形状为 (batch_size, sequence_length, input_size + 1)
        #print(f"combined_inputs shape: {combined_inputs.shape}")  # 例如 (64, 30, 18)

        # 通过全连接层
        hidden = torch.relu(self.fc1(combined_inputs))  # 形状为 (batch_size, sequence_length, hidden_size)
        hidden = torch.relu(self.fc2(hidden))  # 形状不变
        output = self.fc3(hidden)  # 形状为 (batch_size, sequence_length, output_size)

        # 只返回最后一个时间步的输出
        return output[:, -1, :]  # 形状为 (batch_size, output_size)