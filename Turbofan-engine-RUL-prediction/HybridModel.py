import torch
import torch.nn as nn


class HybridModel(nn.Module):
    def __init__(self, input_size=17, lstm_hidden_size=150, wiener_hidden_size=64, output_size=1, sequence_length=30):
        super(HybridModel, self).__init__()
        self.sequence_length = sequence_length

        # Wiener分支
        self.wiener_fc1 = nn.Linear(input_size + 1, wiener_hidden_size)  # 输入增加Wiener路径
        self.wiener_fc2 = nn.Linear(wiener_hidden_size, wiener_hidden_size)
        self.wiener_fc3 = nn.Linear(wiener_hidden_size, output_size)

        # LSTM分支
        self.lstm = nn.LSTM(
            input_size=input_size + 1,  # 同样接收Wiener路径
            hidden_size=lstm_hidden_size,
            num_layers=3,
            bidirectional=True,
            batch_first=True
        )
        self.conv1d = nn.Sequential(
            nn.Conv1d(2 * lstm_hidden_size, 128, kernel_size=3, padding=1),
            nn.ReLU(),
            nn.Conv1d(128, 64, kernel_size=3, padding=1),
            nn.ReLU()
        )

        # 融合层
        self.fusion = nn.Linear(64 * sequence_length + output_size, output_size)

    def generate_wiener_process(self, batch_size, device):
        increments = torch.randn(batch_size, self.sequence_length, device=device)
        return torch.cumsum(increments, dim=1).unsqueeze(-1)  # [B, T, 1]

    def forward(self, inputs):
        batch_size = inputs.shape[0]
        device = inputs.device

        # 生成Wiener路径并拼接
        wiener_path = self.generate_wiener_process(batch_size, device)
        combined_input = torch.cat([inputs, wiener_path], dim=-1)  # [B, T, input_size+1]

        # Wiener分支
        wiener_out = torch.relu(self.wiener_fc1(combined_input))
        wiener_out = torch.relu(self.wiener_fc2(wiener_out))
        wiener_out = self.wiener_fc3(wiener_out)[:, -1, :]  # [B, 1]

        # LSTM分支
        lstm_out, _ = self.lstm(combined_input)  # [B, T, 2*hidden]
        lstm_out = lstm_out.permute(0, 2, 1)  # [B, 2*hidden, T]
        lstm_out = self.conv1d(lstm_out)  # [B, 64, T]
        lstm_out = lstm_out.reshape(batch_size, -1)  # [B, 64*T]

        # 融合两个分支
        fused = torch.cat([lstm_out, wiener_out], dim=1)  # [B, 64*T + 1]
        output = self.fusion(fused)  # [B, 1]

        return output