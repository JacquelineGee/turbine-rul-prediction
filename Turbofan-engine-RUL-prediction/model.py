import torch
from torch import nn


class Model(nn.Module):
    def __init__(self):
        super(Model, self).__init__()
        # 双向LSTM，增加LSTM层数和隐藏层维度
        self.lstm = nn.LSTM(batch_first=True, input_size=17, hidden_size=150, num_layers=3, bidirectional=True)

        # 1D卷积层
        self.conv1d = nn.Sequential(
            nn.Conv1d(in_channels=300, out_channels=128, kernel_size=3, padding=1),  # 双向LSTM时，通道数是300
            nn.ReLU(),
            nn.Conv1d(in_channels=128, out_channels=64, kernel_size=3, padding=1),
            nn.ReLU()
        )

        # 全连接层
        self.linear = nn.Sequential(
            nn.Linear(in_features=64 * 30, out_features=100),
            nn.ReLU(),
            nn.Dropout(p=0.2),
            nn.Linear(in_features=100, out_features=50),
            nn.ReLU(),
            nn.Linear(in_features=50, out_features=10),
            nn.ReLU()
        )

        # 输出层
        self.output = nn.Sequential(
            nn.Linear(in_features=10, out_features=1)
        )

    def forward(self, inputs):
        x, _ = self.lstm(inputs)
        x = x.permute(0, 2, 1)  # 转换维度以适配Conv1d层
        x = self.conv1d(x)
        x = x.reshape(-1, 64 * 30)  # Flatten
        x = self.linear(x)
        out = self.output(x)
        return out
