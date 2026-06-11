import torch
import numpy as np
import os

class Trainer:
    def __init__(self, model, model_optimizer, print_every=50, epochs=200, device='cpu', model_save_path='./LSTM_model.pth'):
        self.model = model.to(device)
        self.model_optimizer = model_optimizer
        self.print_every = print_every
        self.epochs = epochs
        self.device = device
        self.criterion = torch.nn.MSELoss()
        self.model_save_path = model_save_path  # 模型保存路径

    def train_single_epoch(self, dataloader):
        total_loss = 0  # 整个 epoch 的总 loss
        batch_losses = []  # 存储 batch 级别的 loss

        for batch_index, (inputs, labels) in enumerate(dataloader, 0):
            inputs, labels = inputs.to(self.device), labels.to(self.device)
            self.model_optimizer.zero_grad()

            predictions = self.model(inputs)
            loss = self.criterion(predictions, labels)
            loss.backward()
            self.model_optimizer.step()

            total_loss += loss.item()
            batch_losses.append(loss.item())  # 记录 batch 级别的 loss

            if (batch_index + 1) % self.print_every == 0:
                avg_loss = np.mean(batch_losses[-self.print_every:])  # 计算最近 print_every 个 batch 的平均损失
                print(f"Batch {batch_index + 1}/{len(dataloader)}, Loss: {avg_loss:.6f}")

        avg_epoch_loss = total_loss / len(dataloader)  # 计算 epoch 级别的平均损失
        return avg_epoch_loss  # 只返回平均损失

    def train(self, train_loader):
        loss_all_train = []
        for epoch in range(self.epochs):
            print(f"Epoch {epoch + 1}/{self.epochs} training...")
            avg_loss = self.train_single_epoch(train_loader)
            loss_all_train.append(avg_loss)  # 记录每个 epoch 的损失

        # 训练完成后保存模型参数
        torch.save(self.model.state_dict(), self.model_save_path)
        print(f"Model saved to {self.model_save_path}")

        return loss_all_train  # 返回损失列表

    def test(self, test_loader):
        self.model.eval()
        criterion = torch.nn.MSELoss()
        labels_all, predictions_all = [], []
        total_loss = 0.0

        with torch.no_grad():
            for inputs, labels in test_loader:
                inputs, labels = inputs.to(self.device), labels.to(self.device)
                predictions = self.model(inputs)

                # 计算损失
                loss = criterion(labels, predictions)
                total_loss += loss.item() * len(labels)

                # 保存结果
                labels_all.extend(labels.cpu().numpy())
                predictions_all.extend(predictions.cpu().numpy())

        rmse = np.sqrt(total_loss / len(test_loader.dataset))
        print(f"Test Result: RMSE: {rmse:.6f}")
        return rmse, labels_all, predictions_all