import os
import sys
import json
import pandas
import numpy as np
import matplotlib.mlab as mlab
import matplotlib.pyplot as plt

def get_data(src_dir):
    if os.path.exists(src_dir + "/latency.json"):
        with open(src_dir + "/latency.json", 'r') as r:
            try:
                latency_info = json.load(r)
            except Exception as e:
                print("Exception", e)
                sys.exit(1)
        base_data = latency_info['base']
        new_data = latency_info['new']
    return base_data, new_data

def remove_error_data(list):
    list_mean = np.mean(list)
    list_std = np.std(list)
    list = filter(lambda x: x >= list_mean - 3*list_std & x <= list_mean + 3*list, list)
    return list, list_mean, list_std

def draw_result(list, list_mean, list_std, exp_type, save_path):
    num_bins = 20
    n, bins, patches = plt.hist(list, num_bins, normed=1, facecolor='blue', alpha=0.8) # 直方图
    y = mlab.normpdf(bins, list_mean, list_std)  # 拟合概率分布
    plt.plot(bins, y, 'r--') #绘制y的曲线
    plt.xlabel('Latency (ms)') #绘制x轴
    plt.ylabel('Probability') #绘制y轴
    plt.title('Latency distribution based on ' + exp_type + ' strategy')
    plt.savefig(save_path)
    return

def main(src_dir, target_dir):
    base_data, new_data = get_data(src_dir)
    base_data, base_mean, base_std = remove_error_data(base_data)
    draw_result(base_data, base_mean, base_std, "default", target_dir + "/latency_default.jpg")
    new_data, new_mean, new_std = remove_error_data(new_data)
    draw_result(new_data, new_mean, new_std, "new", target_dir + "/latency_new.jpg")
    return

if __name__ = "__main__":
    src_dir = sys.argv[1]
    target_dir = sys.argv[2]
    main(src_dir, target_dir)