import torch
from torch import nn


class Model2(nn.Module):
    def __init__(self, n_features=17, hidden_units=150):
        super().__init__()
        self.n_features = n_features
        self.hidden_units = hidden_units
        self.n_layers = 3
        self.gru = nn.GRU(input_size=n_features, hidden_size=self.hidden_units, batch_first=True,
                          num_layers=self.n_layers, bidirectional=True)

        self.conv1d = nn.Sequential(
            nn.Conv1d(in_channels=300, out_channels=128, kernel_size=3, padding=1),
            nn.ReLU(),
            nn.Conv1d(in_channels=128, out_channels=64, kernel_size=3, padding=1),
            nn.ReLU()
        )

        self.linear1 = nn.Linear(in_features=64 * 30, out_features=100)
        self.relu1 = nn.ReLU()
        self.dropout = nn.Dropout(p=0.2)
        self.linear2 = nn.Linear(in_features=100, out_features=50)
        self.relu2 = nn.ReLU()
        self.linear3 = nn.Linear(in_features=50, out_features=10)
        self.relu3 = nn.ReLU()
        self.output = nn.Linear(in_features=10, out_features=1)

    def forward(self, x):
        batch_size = x.shape[0]
        h0 = torch.zeros(self.n_layers * 2, batch_size, self.hidden_units).to(x.device)  # 双向GRU时，层数翻倍
        x, _ = self.gru(x, h0)  # 直接使用 x 而不是 hn
        x = x.permute(0, 2, 1)  # 变成 (batch_size, 300, 30)
        x = self.conv1d(x)  # 经过1D卷积
        x = x.reshape(batch_size, -1)  # 展平为 (batch_size, 64 * 30)
        x = self.linear1(x)
        x = self.relu1(x)
        x = self.dropout(x)
        x = self.linear2(x)
        x = self.relu2(x)
        x = self.linear3(x)
        x = self.relu3(x)
        out = self.output(x)
        return out
