import numpy as np

# 数据读取
full_data = np.loadtxt(fname='./datasets/CMAPSSData/train_FD001.txt', dtype=np.float32)
full_test = np.loadtxt(fname='./datasets/CMAPSSData/test_FD001.txt', dtype=np.float32)

# engineID and working time cycle, no need to norm
prefix_data = full_data[:, [0, 1]]
prefix_test = full_test[:, [0, 1]]
# operational settings and raw sensor data
inputs_data = full_data[:, 2:]
inputs_test = full_test[:, 2:]

# to avoid devide zero
eps = 1e-12
min = np.min(inputs_data, axis=0)
max = np.max(inputs_data, axis=0)
# 最大最小标准化
normed_data = (inputs_data - min) / (max - min + eps)
normed_test = (inputs_test - min) / (max - min + eps)

output_data = np.concatenate((prefix_data, normed_data), axis=1)
output_test = np.concatenate((prefix_test, normed_test), axis=1)
np.savetxt('./datasets/CMAPSSData/train_FD001_normed.txt', output_data, fmt='%f')
np.savetxt('./datasets/CMAPSSData/test_FD001_normed.txt', output_test, fmt='%f')
